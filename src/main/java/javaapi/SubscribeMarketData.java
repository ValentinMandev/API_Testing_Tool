package javaapi;

import com.fxcm.external.api.transport.FXCMLoginProperties;
import com.fxcm.external.api.transport.GatewayFactory;
import com.fxcm.external.api.transport.IGateway;
import com.fxcm.external.api.transport.listeners.IGenericMessageListener;
import com.fxcm.external.api.transport.listeners.IStatusMessageListener;
import com.fxcm.fix.IFixDefs;
import com.fxcm.fix.NotDefinedException;
import com.fxcm.fix.SubscriptionRequestTypeFactory;
import com.fxcm.fix.TradingSecurity;
import com.fxcm.fix.pretrade.MarketDataRequest;
import com.fxcm.fix.pretrade.MarketDataRequestReject;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.fxcm.messaging.ISessionStatus;
import com.fxcm.messaging.ITransportable;
import com.fxcm.util.Util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;


public class SubscribeMarketData {
    private String mAccountMassID;
    private IGateway mFxcmGateway;
    private final String mPassword;
    private final String mServer;
    private final String mStation;
    private IStatusMessageListener mStatusListener;
    private final String mUsername;
    private final List<String> mSymbol;
    private final PrintStream out;

    public SubscribeMarketData(String aUsername, String aPassword, String aStation, String aServer, String aSymbol) throws FileNotFoundException {
        mServer = aServer;
        mUsername = aUsername;
        mPassword = aPassword;
        mStation = aStation;
        mSymbol = Arrays.stream(aSymbol.split(", ")).collect(Collectors.toList());
        String outputFile = "output/javaapi/subscribe_market_data.txt";
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        out = new PrintStream(outputStream);
    }

    private boolean doResult(final MessageTestHandler aMessageTestHandler) {
        new Thread(() -> setup(aMessageTestHandler, false)).start();
        int expiration = 30; //seconds

        while (expiration > 0) {
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
        mFxcmGateway.logout();
        return aMessageTestHandler.isSuccess();
    }

    private void handleMessage(ITransportable aMessage, List aAccounts, MessageTestHandler aMessageTestHandler) throws NotDefinedException {
        if (aMessage instanceof MarketDataSnapshot) {
            aMessageTestHandler.process((MarketDataSnapshot) aMessage);
        } else if (aMessage instanceof MarketDataRequestReject) {
            aMessageTestHandler.process((MarketDataRequestReject) aMessage);
        } else if (aMessage instanceof TradingSessionStatus) {
            aMessageTestHandler.process((TradingSessionStatus) aMessage);
        }
    }

    private static void runTest(String[] aArgs) throws FileNotFoundException {

        SubscribeMarketData subscribeMarketData = new SubscribeMarketData(aArgs[0], aArgs[1], aArgs[2], aArgs[3], aArgs[4]);
        subscribeMarketData.testMarketDataRequest();

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

    public boolean testMarketDataRequest() throws FileNotFoundException {
        String cem = Util.getCurrentlyExecutingMethod();
        System.out.println(cem);
        class GenericListener extends MessageTestHandler {
            private String mMarketDataRequestID;

            public void process(TradingSessionStatus aTradingSessionStatus) {
                try {
                    MarketDataRequest mdr = new MarketDataRequest();
                    Enumeration securities = aTradingSessionStatus.getSecurities();
                    while (securities.hasMoreElements()) {
                        TradingSecurity o = (TradingSecurity) securities.nextElement();
                        if (mSymbol.contains(o.getSymbol())) {
                            mdr.addRelatedSymbol(o);
                        }

                    }
                    mdr.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SUBSCRIBE);
                    mdr.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_ALL);
                    mMarketDataRequestID = mFxcmGateway.sendMessage(mdr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void process(MarketDataSnapshot aMarketDataSnapshot) throws NotDefinedException {
                super.process(aMarketDataSnapshot);
                if (safeEquals(aMarketDataSnapshot.getRequestID(), mMarketDataRequestID) &&
                        mSymbol.contains(aMarketDataSnapshot.getInstrument().getSymbol())) {
                    if (aMarketDataSnapshot.getFXCMContinuousFlag() == IFixDefs.FXCMCONTINUOUS_END) {
                        setSuccess(true);
                    }
                }
            }

            public void messageArrived(ITransportable aMessage) {
                try {
                    handleMessage(aMessage, null, this);
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

        public void setSuccess(boolean aSuccess) {
            mSuccess = aSuccess;
        }


        public void process(MarketDataSnapshot aMarketDataSnapshot) throws NotDefinedException {
            if (mSymbol.contains(aMarketDataSnapshot.getInstrument().getSymbol())) {
//                System.out.println("client inc: aMarketDataSnapshot = " + aMarketDataSnapshot);
                out.println(
                        "Instrument: " + aMarketDataSnapshot.getInstrument().getSymbol()
                                + " Time: " + aMarketDataSnapshot.getDate().toString().substring(0, 4)
                                + "/" + aMarketDataSnapshot.getDate().toString().substring(4, 6)
                                + "/" + aMarketDataSnapshot.getDate().toString().substring(6)
                                + " " + aMarketDataSnapshot.getTime()
                                + " Bid: " + String.format("%.5f", aMarketDataSnapshot.getBidClose())
                                + " Ask: " + String.format("%.5f", aMarketDataSnapshot.getAskClose()));
                if (aMarketDataSnapshot.getFXCMContinuousFlag() == IFixDefs.FXCMCONTINUOUS_END) {
                    setSuccess(true);
                }
            }

        }

        public void process(MarketDataRequestReject aMarketDataRequestReject) {
        }

        public void process(TradingSessionStatus aTradingSessionStatus) {
            mTradingSessionStatus = aTradingSessionStatus;
            System.out.println("client inc: aTradingSessionStatus = " + aTradingSessionStatus);
        }

    }

    public static void run(String[] aArgs) throws FileNotFoundException {
        if (aArgs.length < 5) {
            System.out.println("must supply 5 arguments: username, password, station, hostname, symbol");
            return;
        }
        runTest(aArgs);
    }

    public static void main(String[] aArgs) throws FileNotFoundException {
        run(aArgs);
    }
}
