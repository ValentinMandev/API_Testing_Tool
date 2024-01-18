package javaapi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.Set;

public class Run {

    public static void main(String username, String password, String url, String connection, String instruments, String dateFrom, String dateTo, String timeframe) throws FileNotFoundException {

        HistData.run(new String[]{username, password, connection,url, instruments, timeframe, dateFrom, dateTo});
        SubscribeMarketData.run(new String[]{username, password, connection, url, instruments});
        CreateMarketOrder.run(new String[]{username, password, connection, url, instruments, "1000", "SELL", "GTC", "open_position"});
        CreateMarketOrder.run(new String[]{username, password, connection, url, instruments, "1000", "BUY", "GTC", "close_position"});
        CreateStopEntryOrder.run(new String[]{username, password, connection, url, instruments, "1000", "BUY", "GTC"});
        CreateLimitEntryOrder.run(new String[]{username, password, connection, url, instruments, "1000", "SELL", "GTC"});

    }
}


