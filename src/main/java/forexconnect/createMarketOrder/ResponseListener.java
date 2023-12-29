package forexconnect.createMarketOrder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.fxcore2.*;

public class ResponseListener implements IO2GResponseListener {

    private O2GSession mSession;
    private String mRequestID;
    private O2GResponse mResponse;
    private Semaphore mSemaphore;
    private OrderMonitor mOrderMonitor;
    private final PrintStream out;

    // ctor
    public ResponseListener(O2GSession session) throws FileNotFoundException {
        mSession = session;
        mRequestID = "";
        mResponse = null;
        mSemaphore = new Semaphore(0);
        mOrderMonitor = null;
        String outputFile = "output/forexconnect/execution_report_market_order.txt";
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        out = new PrintStream(outputStream);
    }

    public void setRequestID(String sRequestID, String instrument) {
        mResponse = null;
        mRequestID = sRequestID;
        out.println(instrument + ":");
    }

    public boolean waitEvents() throws Exception {
        return mSemaphore.tryAcquire(30, TimeUnit.SECONDS);
    }

    public O2GResponse getResponse() {
        return mResponse;
    }

    public void onRequestCompleted(String sRequestId, O2GResponse response) {
        if (mRequestID.equals(response.getRequestId())) {
            mResponse = response;
            if (response.getType() != O2GResponseType.CREATE_ORDER_RESPONSE) {
                mSemaphore.release();
            }
        }
        // real order execution is processed by Order monitor
    }

    public void onRequestFailed(String sRequestID, String sError) {
        if (mRequestID.equals(sRequestID)) {
            out.println("Request failed: " + sError);
            mSemaphore.release();
        }
    }

    public void onTablesUpdates(O2GResponse response) {
        O2GResponseReaderFactory factory = mSession.getResponseReaderFactory();
        if (factory != null) {
            O2GTablesUpdatesReader reader = factory.createTablesUpdatesReader(response);
            for (int ii = 0; ii < reader.size(); ii++) {
                switch (reader.getUpdateTable(ii)) {
                case ACCOUNTS:
                    O2GAccountRow account = reader.getAccountRow(ii);
                    // Show balance updates
//                    out.println(String.format("Balance: %.2f", account.getBalance()));
                    out.println();
                    break;
                case ORDERS:
                    O2GOrderRow order = reader.getOrderRow(ii);
                    switch (reader.getUpdateType(ii)) {
                    case INSERT:
                        if ((OrderMonitor.isClosingOrder(order) || OrderMonitor.isOpeningOrder(order))
                                ) {
                            out.println(String.format("The order has been added. Order ID: %s, Rate: %s, Time In Force: %s",
                                    order.getOrderID(),
                                    order.getRate(),
                                    order.getTimeInForce()));
                            mOrderMonitor = new OrderMonitor(order);
                        }
                        break;
                    case DELETE:
                        if (mOrderMonitor != null) {
                            out.println(String.format("The order has been deleted. Order ID: %s",
                                    order.getOrderID()));
                            mOrderMonitor.onOrderDeleted(order);
                            if (mOrderMonitor.isOrderCompleted()) {
                                printResult();
                                mSemaphore.release();
                            }
                        }
                        break;
                    }
                    break;
                case TRADES:
                    O2GTradeRow trade = reader.getTradeRow(ii);
                    if (reader.getUpdateType(ii) == O2GTableUpdateType.INSERT) {
                        if (mOrderMonitor != null) {
                            mOrderMonitor.onTradeAdded(trade);
                            if (mOrderMonitor.isOrderCompleted()) {
                                printResult();
                                mSemaphore.release();
                            }
                        }
                    }
                    break;
                case CLOSED_TRADES:
                    O2GClosedTradeRow closedTrade = reader.getClosedTradeRow(ii);
                    if (reader.getUpdateType(ii) == O2GTableUpdateType.INSERT) {
                        if (mOrderMonitor != null) {
                            mOrderMonitor.onClosedTradeAdded(closedTrade);
                            if (mOrderMonitor.isOrderCompleted()) {
                                printResult();
                                mSemaphore.release();
                            }
                        }
                    }
                    break;
                case MESSAGES:
                    O2GMessageRow message = reader.getMessageRow(ii);
                    if (reader.getUpdateType(ii) == O2GTableUpdateType.INSERT) {
                        if (mOrderMonitor != null) {
                            mOrderMonitor.onMessageAdded(message);
                            if (mOrderMonitor.isOrderCompleted()) {
                                printResult();
                                mSemaphore.release();
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void printResult() {
        if (mOrderMonitor != null) {
            OrderMonitor.ExecutionResult result = mOrderMonitor.getResult();
            List<O2GTradeRow> trades;
            List<O2GClosedTradeRow> closedTrades;
            O2GOrderRow order = mOrderMonitor.getOrder();
            String sOrderID = order.getOrderID();
            trades = mOrderMonitor.getTrades();
            closedTrades = mOrderMonitor.getClosedTrades();

            switch (result) {
            case Canceled:
                if (trades.size() > 0) {
                    printTrades(trades, sOrderID);
                    printClosedTrades(closedTrades, sOrderID);
                    this.out.println(String.format("A part of the order has been canceled. Amount = %s",
                            mOrderMonitor.getRejectAmount()));
                } else {
                    this.out.println(String.format(
                            "The order: OrderID = %s  has been canceled.",
                            sOrderID));
                    this.out.println(String.format(
                            "The cancel amount = %s.",
                            mOrderMonitor.getRejectAmount()));
                }
                break;
            case FullyRejected:
                this.out.println(String.format(
                        "The order has been rejected. OrderID = %s",
                        sOrderID));
                this.out.println(String.format("The rejected amount = %s",
                        mOrderMonitor.getRejectAmount()));
                this.out.println(String.format("Rejection cause: %s",
                        mOrderMonitor.getRejectMessage()));
                break;
            case PartialRejected:
                printTrades(trades, sOrderID);
                printClosedTrades(closedTrades, sOrderID);
                this.out.println(String.format(
                        "A part of the order has been rejected. Amount = %s",
                        mOrderMonitor.getRejectAmount()));
                this.out.println(String.format("Rejection cause: %s ",
                        mOrderMonitor.getRejectMessage()));
                break;
            case Executed:
                printTrades(trades, sOrderID);
                printClosedTrades(closedTrades, sOrderID);
                break;
            }
        }
    }

    private void printTrades(List<O2GTradeRow> trades, String sOrderID) {
        if (trades.size() == 0)
            return;
        out.println(String.format("For the order: OrderID = %s the following positions have been opened:",
                sOrderID));

        for (int i = 0; i < trades.size(); i++) {
            O2GTradeRow trade = trades.get(i);
            String sTradeID = trade.getTradeID();
            String iSymbol;
            int iAmount = trade.getAmount();
            double dRate = trade.getOpenRate();
            out.println(String.format(
                    "Trade ID: %s; Amount: %s; Rate: %s",
                    sTradeID, iAmount, dRate));
        }
    }

    private void printClosedTrades(List<O2GClosedTradeRow> closedTrades, String sOrderID) {
        if (closedTrades.size() == 0)
            return;
        out.println(String.format("For the order: OrderID = %s the following positions have been closed: ",
                sOrderID));

        for (int i = 0; i < closedTrades.size(); i++) {
            O2GClosedTradeRow closedTrade = closedTrades.get(i);
            String sTradeID = closedTrade.getTradeID();
            int iAmount = closedTrade.getAmount();
            double dRate = closedTrade.getCloseRate();
            out.println(String.format(
                    "Closed Trade ID: %s; Amount: %s; Closed Rate: %s",
                    sTradeID, iAmount, dRate));
        }
    }
}
