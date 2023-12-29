package javaapi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.Set;

public class Run {

    public static void main(String username, String password, String connection, String instruments, String dateFrom, String dateTo, String timeframe) throws FileNotFoundException {

        HistData.run(new String[]{username, password, connection,"https://fxcorporate.com/Hosts.jsp", instruments, timeframe, dateFrom, dateTo});
        SubscribeMarketData.run(new String[]{username, password, connection, "https://fxcorporate.com/Hosts.jsp", instruments});
        CreateMarketOrder.run(new String[]{username, password, connection, "https://fxcorporate.com/Hosts.jsp", instruments, "1000", "SELL", "GTC"});
        CreateStopEntryOrder.run(new String[]{username, password, connection, "https://fxcorporate.com/Hosts.jsp", instruments, "1000", "BUY", "GTC"});
        CreateLimitEntryOrder.run(new String[]{username, password, connection, "https://fxcorporate.com/Hosts.jsp", instruments, "1000", "SELL", "GTC"});

    }
}


