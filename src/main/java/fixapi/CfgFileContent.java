package fixapi;

public class CfgFileContent {

    public static String beginString = "[default]\n" +
            "FileStorePath=.\\appRun\\store\n" +
            "FileLogPath=.\\appRun\\log\n" +
            "ConnectionType=initiator\n" +
            "StartTime=00:00:00\n" +
            "EndTime=00:00:00\n" +
            "HeartBtInt=30\n" +
            "UseDataDictionary=Y\n" +
            "DefaultMarketPrice=12.30\n" +
            "SocketAcceptPort=80\n" +
            "Timezone=America/New_York\n" +
            "StartDay=Sunday\n" +
            "StartTime=00:00:00\n" +
            "EndDay=Sunday\n" +
            "EndTime=00:00:00\n" +
            "ValidateUserDefinedFields=N\n" +
            "ValidateFieldsHaveValues=N\n" +
            "ValidateFieldsOutOfOrder=N\n" +
            "AllowUnknownMsgFields=Y\n" +
            "SocketTcpNoDelay=Y\n" +
            "DataDictionary=.\\FIXFXCM10.xml\n" +
            "ContinueInitializationOnError=Y\n" +
            "ResetOnLogon=Y\n";

    public static String tradingSessionString = "\n[session]\n" +
            "BeginString=FIX.4.4\n" +
            "TargetCompID=FXCM";

    public static String marketDataSessionString = "\n[session]\n" +
            "BeginString=FIX.4.4\n" +
            "TargetCompID=FXCM\n" +
            "MDEntryType = Y";

    public static String userAndPassString = "username=%s\n" +
            "password=%s\n";

    public static String sessionDetailsString = "SocketConnectHost=%s\n" +
            "SocketConnectPort=%s\n" +
            "SenderCompID=%s\n" +
            "TargetSubID=%s\n";

}
