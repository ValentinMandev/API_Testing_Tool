import fixapi.FixApi;

import java.io.FileNotFoundException;

public class Main {

    final static String username = "ValioDemo";
    final static String password = "1234";
    final static String connection = "Demo";
    final static String instruments = "EUR/USD, XAU/USD, NAS100";
    final static String senderCompID = "valiodemo_client1";
    final static String socketConnectHost = "fixdemo.fxcorporate.com";
    final static String socketConnectPort = "8043";
    final static String senderCompIDMD = "MD_valiodemo_client1";
    final static String socketConnectHostMD = "fixdemo.fxcorporate.com";
    final static String socketConnectPortMD = "9043";
    final static String targetSubID = "EUDEMO";

    final static String timeframe = "D1";
    final static String dateFrom = "20231203 00:00:00";
    final static String dateTo = "20231218 00:00:00";


    public static void main(String[] args) throws FileNotFoundException {

        // RUN FOREXCONNECT TEST
        forexconnect.Run.main(username, password, connection, instruments, dateFrom, dateTo, timeframe);

        // RUN JAVA API TEST
        javaapi.Run.main(username, password, connection, instruments, dateFrom, dateTo, timeframe);

        // RUN FIX API TEST
        FixApi fixApi = new FixApi(username, password, instruments, senderCompID, socketConnectHost, socketConnectPort,
                senderCompIDMD, socketConnectHostMD, socketConnectPortMD, targetSubID);
        fixApi.run();
    }
}