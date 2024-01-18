import fixapi.FixApi;

import java.io.FileNotFoundException;

public class Main {

    final static String username = "";
    final static String password = "";
    final static String connection = "";
    final static String url = "https://fxcorporate.com/Hosts.jsp";
    final static String instruments = "EUR/USD, XAU/USD, NAS100";
    final static String senderCompID = "";
    final static String socketConnectHost = "";
    final static String socketConnectPort = "";
    final static String senderCompIDMD = "";
    final static String socketConnectHostMD = "";
    final static String socketConnectPortMD = "";
    final static String targetSubID = "";

    final static String timeframe = "D1";
    final static String dateFrom = "20231203 00:00:00";
    final static String dateTo = "20231218 00:00:00";


    public static void main(String[] args) throws FileNotFoundException {

        // RUN FOREXCONNECT TEST
        forexconnect.Run.main(username, password, url, connection, instruments, dateFrom, dateTo, timeframe);

        // RUN JAVA API TEST
        javaapi.Run.main(username, password, url, connection, instruments, dateFrom, dateTo, timeframe);

        // RUN FIX API TEST
        FixApi fixApi = new FixApi(username, password, instruments, senderCompID, socketConnectHost, socketConnectPort,
                senderCompIDMD, socketConnectHostMD, socketConnectPortMD, targetSubID);
        fixApi.run();
    }
}