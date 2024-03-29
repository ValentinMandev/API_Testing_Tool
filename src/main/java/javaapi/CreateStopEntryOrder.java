package javaapi;

import com.fxcm.external.api.transport.FXCMLoginProperties;
import com.fxcm.external.api.transport.GatewayFactory;
import com.fxcm.external.api.transport.IGateway;
import com.fxcm.external.api.transport.listeners.IGenericMessageListener;
import com.fxcm.external.api.transport.listeners.IStatusMessageListener;
import com.fxcm.external.api.util.MessageGenerator;
import com.fxcm.fix.NotDefinedException;
import com.fxcm.fix.OrdTypeFactory;
import com.fxcm.fix.SubscriptionRequestTypeFactory;
import com.fxcm.fix.TradingSecurity;
import com.fxcm.fix.posttrade.CollateralReport;
import com.fxcm.fix.pretrade.MarketDataRequest;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.fxcm.fix.trade.ExecutionReport;
import com.fxcm.fix.trade.OrderSingle;
import com.fxcm.messaging.ISessionStatus;
import com.fxcm.messaging.ITransportable;
import com.fxcm.util.Util;
import javaapi.enums.Sides;
import javaapi.enums.TIFs;
import javaapi.common.Order;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class CreateStopEntryOrder {
    private String mAccountMassID;
    private IGateway mFxcmGateway;
    private final String mPassword;
    private final String mServer;
    private final String mStation;
    private IStatusMessageListener mStatusListener;
    private final String mUsername;
    private final Order order;
    private final String symbol;
    private final List<String> reports;
    private static String outputFile = "output/javaapi/execution_report_stop_entry_order.txt";
    private static FileOutputStream outputStream;

    static {
        try {
            outputStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static PrintStream out = new PrintStream(outputStream);
    private static List<String> result = new ArrayList<>();


    public CreateStopEntryOrder(String[] aArgs) throws FileNotFoundException {
        mUsername = aArgs[0];
        mPassword = aArgs[1];
        mStation = aArgs[2];
        mServer = aArgs[3];
        order = getOrder(Arrays.stream(aArgs).skip(4).toArray(String[]::new));
        symbol = aArgs[4];
        reports = new ArrayList<>();
    }

    private boolean doResult(final MessageTestHandler aMessageTestHandler) {
        new Thread(() -> setup(aMessageTestHandler, false)).start();
        int expiration = 30; //seconds
        while (!aMessageTestHandler.isSuccess() && expiration > 0) {
            try {
                Thread.sleep(1000);
                expiration--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (aMessageTestHandler.isSuccess()) {
            System.out.println("done waiting.\nstatus = " + aMessageTestHandler.isSuccess());
        } else {
            System.err.println("done waiting.\nstatus = " + aMessageTestHandler.isSuccess());
        }
        mFxcmGateway.removeGenericMessageListener(aMessageTestHandler);
        mFxcmGateway.removeStatusMessageListener(mStatusListener);

        String[] executionReport = reports.get(0).split("\\{");

        try {
            result.add(executionReport[3].split(", ")[17]);
            result.add(executionReport[2].split(", ")[7]);
            result.add(executionReport[1].split(", ")[20]);
            result.add(executionReport[2].split(", ")[5]);
            result.add(executionReport[2].split(", ")[10]);
            result.add(executionReport[2].split(", ")[11].split(",")[1]);
            result.add(executionReport[1].split(", ")[10]);
        } catch (Exception e) {
            result.add(executionReport[4].split(", ")[17]);
            result.add(executionReport[3].split(", ")[7]);
            result.add(executionReport[1].split(", ")[20]);
            result.add(executionReport[3].split(", ")[5]);
            result.add(executionReport[3].split(", ")[10]);
            result.add(executionReport[2].split(", ")[3]);
            result.add(executionReport[2].split(", ")[1]);
            result.add(executionReport[1].split(", ")[8]);
        }

        result.forEach(out::println);
        result.clear();

        mFxcmGateway.logout();
        return aMessageTestHandler.isSuccess();
    }

    private void handleMessage(ITransportable aMessage, List aAccounts, MessageTestHandler aMessageTestHandler) throws NotDefinedException {
        if (aMessage instanceof CollateralReport) {
            CollateralReport cr = (CollateralReport) aMessage;
            if (safeEquals(mAccountMassID, cr.getRequestID()) && aAccounts != null) {
                aAccounts.add(cr);
            }
            aMessageTestHandler.process(cr);
        } else if (aMessage instanceof TradingSessionStatus) {
            aMessageTestHandler.process((TradingSessionStatus) aMessage);
        } else if (aMessage instanceof MarketDataSnapshot && ((MarketDataSnapshot) aMessage).getInstrument().getSymbol().equals(symbol)) {
            aMessageTestHandler.process((MarketDataSnapshot) aMessage);
        } else if (aMessage instanceof ExecutionReport) {
            aMessageTestHandler.process((ExecutionReport) aMessage);
        }
    }

    private static Order getOrder(String[] aArgs) {
        return new Order(aArgs[0], Integer.parseInt(aArgs[1]), aArgs[2], aArgs[3]);
    }

    private static void runTest(String[] aArgs) throws FileNotFoundException {
        result.add(aArgs[4]);
        CreateStopEntryOrder createEntryOrder = new CreateStopEntryOrder(aArgs);
        createEntryOrder.testCreateEntryOrder(false);
        result.add("\n");
    }

    public static boolean safeEquals(String aString1, String aString2) {
        return !(aString1 == null || aString2 == null) && aString1.equals(aString2);
    }

    private void setup(IGenericMessageListener aGenericListener, boolean aPrintStatus) {
        try {
            if (mFxcmGateway == null) {
                // step 1: get an instance of IGateway from the GatewayFactory
                mFxcmGateway = GatewayFactory.createGateway();
            }
            /*
                step 2: register a generic message listener with the gateway, this
                listener in particular gets all messages that are related to the trading
                platform Quote,OrderSingle,ExecutionReport, etc...
            */
            mFxcmGateway.registerGenericMessageListener(aGenericListener);
            mStatusListener = new DefaultStatusListener(aPrintStatus);
            mFxcmGateway.registerStatusMessageListener(mStatusListener);
            if (!mFxcmGateway.isConnected()) {
                System.out.println("client: login");
                FXCMLoginProperties properties = new FXCMLoginProperties(mUsername, mPassword, mStation, mServer);
                /*
                    step 3: call login on the gateway, this method takes an instance of FXCMLoginProperties
                    which takes 4 parameters: username,password,terminal and server or path to a Hosts.xml
                    file which it uses for resolving servers. As soon as the login  method executes your listeners begin
                    receiving asynch messages from the FXCM servers.
                */
                mFxcmGateway.login(properties);
            }
            //after login you must retrieve your trading session status and get accounts to receive messages
            mFxcmGateway.requestTradingSessionStatus();
            mAccountMassID = mFxcmGateway.requestAccounts();
            mFxcmGateway.requestOpenPositions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean testCreateEntryOrder(final boolean aIsDay) {
        final String cem = Util.getCurrentlyExecutingMethod();
        System.out.println(cem);
        class GenericListener extends MessageTestHandler {
            private final List mAccounts = new ArrayList();
            private boolean mOrder = true;
            private String mRequestId;
            private final boolean mIsDay = aIsDay;

            public void process(MarketDataSnapshot aMarketDataSnapshot) {
                try {
                    if (mOrder && !mAccounts.isEmpty()) {
                        CollateralReport acct = (CollateralReport) mAccounts.get(0);
                        mOrder = false;

                        double price = order.getSide().equalsIgnoreCase("BUY")
                                ? aMarketDataSnapshot.getBidHigh() + aMarketDataSnapshot.getBidHigh() * 0.05
                                : aMarketDataSnapshot.getAskLow() - aMarketDataSnapshot.getAskLow() * 0.05;

                        OrderSingle os2 = MessageGenerator.generateStopLimitEntry(
//                                add(aMarketDataSnapshot.getBidClose(), order.getPrice(), aMarketDataSnapshot.getInstrument().getSymbol()),
                                price,
                                OrdTypeFactory.STOP,
                                acct.getAccount(),
                                order.getAmount(),
                                Sides.valueOf(order.getSide().toUpperCase()).getSide(),
                                order.getSymbol(),
                                cem);
                        if (mIsDay) {
                            os2.setTimeInForce(TIFs.valueOf("DAY").getTIF());
                        }
                        mRequestId = mFxcmGateway.sendMessage(os2);
                        System.out.println("client: entry order requestId = " + mRequestId);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            public void messageArrived(ITransportable aMessage) {
                try {
                    handleMessage(aMessage, mAccounts, this);
                } catch (NotDefinedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return doResult(new GenericListener());
    }

    private static class DefaultStatusListener implements IStatusMessageListener {
        private final boolean mPrint;

        DefaultStatusListener(boolean aPrint) {
            mPrint = aPrint;
        }

        public void messageArrived(ISessionStatus aStatus) {
            if (mPrint) {
                System.out.println("aStatus = " + aStatus);
            }
            if (aStatus.getStatusCode() == ISessionStatus.STATUSCODE_ERROR
                    || aStatus.getStatusCode() == ISessionStatus.STATUSCODE_DISCONNECTING
                    || aStatus.getStatusCode() == ISessionStatus.STATUSCODE_DISCONNECTED) {
                System.out.println("aStatus = " + aStatus);
            }
        }
    }

    private abstract class MessageTestHandler implements IGenericMessageListener {
        private boolean mSuccess;
        protected TradingSessionStatus mTradingSessionStatus;

        public boolean isSuccess() {
            return mSuccess;
        }

        public void process(CollateralReport aCollateralReport) {
//            System.out.println("client inc: aCollateralReport = " + aCollateralReport);
            if (mAccountMassID.equals(aCollateralReport.getRequestID()) && aCollateralReport.isLastRptRequested()) {
                try {
//                    System.out.println("client out: do marketdatarequest for testing to get fast mds");
                    MarketDataRequest mdr = new MarketDataRequest();
                    Enumeration securities = mTradingSessionStatus.getSecurities();
                    while (securities.hasMoreElements()) {
                        TradingSecurity o = (TradingSecurity) securities.nextElement();
                        if (order.getSymbol().equals(o.getSymbol())) {
                            mdr.addRelatedSymbol(o);
                        }
                    }
                    mdr.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SUBSCRIBE);
                    mdr.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_ALL);
                    mFxcmGateway.sendMessage(mdr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public double add(double aValue1, double aValue2, String aSymbol) throws NotDefinedException {
            int precision = mTradingSessionStatus.getSecurity(aSymbol).getFXCMSymPrecision();
            return BigDecimal.valueOf(aValue1 + aValue2).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        public void process(TradingSessionStatus aTradingSessionStatus) {
            mTradingSessionStatus = aTradingSessionStatus;
//            System.out.println("client inc: aTradingSessionStatus = " + aTradingSessionStatus);
        }

        public void process(MarketDataSnapshot aMarketDataSnapshot) {
            //nothing
        }

        public void process(ExecutionReport aExecutionReport) {
            reports.add(aExecutionReport.toString());
//            System.out.println("client inc: aExecutionReport = " + aExecutionReport);
        }

    }

    public static void run(String[] aArgs) throws FileNotFoundException {
        if (aArgs.length < 8) {
            System.out.println("must supply 8 arguments: username, password, station, hostname," +
                    "symbol, amount, side, time in force");
            return;
        }

        String[] instruments = aArgs[4].split(", ");
        for (String instr : instruments) {
            aArgs[4] = instr;
            runTest(aArgs);
        }
    }

    public static void main(String[] aArgs) throws FileNotFoundException {
        run(aArgs);
    }
}

