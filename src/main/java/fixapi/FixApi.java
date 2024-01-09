package fixapi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class FixApi {

    private String username;
    private String password;
    private String instruments;
    private String senderCompID;
    private String socketConnectHost;
    private String socketConnectPort;
    private String senderCompIDMD;
    private String socketConnectHostMD;
    private String socketConnectPortMD;
    private String targetSubID;

    private static String cfgFile = "quickfix.cfg";
    private static String cfgFileMD = "quickfixMD.cfg";
    private static FileOutputStream outputStream;
    private static FileOutputStream outputStreamMD;

    static {
        try {
            outputStream = new FileOutputStream(cfgFile);
            outputStreamMD = new FileOutputStream(cfgFileMD);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final PrintStream out = new PrintStream(outputStream);
    private static final PrintStream outMD = new PrintStream(outputStreamMD);

    public FixApi(String username, String password, String instruments, String senderCompID, String socketConnectHost,
                  String socketConnectPort, String senderCompIDMD, String socketConnectHostMD,
                  String socketConnectPortMD, String targetSubID) {
        this.username = username;
        this.password = password;
        this.instruments = instruments;
        this.senderCompID = senderCompID;
        this.socketConnectHost = socketConnectHost;
        this.socketConnectPort = socketConnectPort;
        this.senderCompIDMD = senderCompIDMD;
        this.socketConnectHostMD = socketConnectHostMD;
        this.socketConnectPortMD = socketConnectPortMD;
        this.targetSubID = targetSubID;
    }


    public void createCfgFiles() {
        out.println(CfgFileContent.beginString);
        out.println(String.format(CfgFileContent.userAndPassString, username, password));
        out.println(CfgFileContent.tradingSessionString);
        out.println(String.format(CfgFileContent.sessionDetailsString, socketConnectHost, socketConnectPort,
                senderCompID, targetSubID));

        outMD.println(CfgFileContent.beginString);
        outMD.println(String.format(CfgFileContent.userAndPassString, username, password));
        outMD.println(CfgFileContent.marketDataSessionString);
        outMD.println(String.format(CfgFileContent.sessionDetailsString, socketConnectHostMD, socketConnectPortMD,
                senderCompIDMD, targetSubID));
    }


    public void run() {
        createCfgFiles();
        fixapi.FIXTradingTester.main(new String[]{cfgFile, cfgFileMD, instruments});
    }



}
