package javaapi;

import java.io.FileNotFoundException;

public class Run {

    public static void main(String[] args) throws FileNotFoundException {

        HistData.run(new String[]{"ValioDemo", "1234", "Demo", "https://fxcorporate.com/Hosts.jsp", "EUR/USD", "D1", "20231013", "00:00", "20231018", "00:00"});
        SubscribeMarketData.run(new String[]{"ValioDemo", "1234", "Demo", "https://fxcorporate.com/Hosts.jsp", "USD/JPY"});
        CreateMarketOrder.run(new String[]{"ValioDemo", "1234", "Demo", "https://fxcorporate.com/Hosts.jsp", "USD/JPY", "4000", "SELL", "GTC"});
        CreateStopEntryOrder.run(new String[]{"ValioDemo", "1234", "Demo", "https://fxcorporate.com/Hosts.jsp", "EUR/USD", "1000", "BUY", "GTC"});
        CreateLimitEntryOrder.run(new String[]{"ValioDemo", "1234", "Demo", "https://fxcorporate.com/Hosts.jsp", "US30", "10", "SELL", "GTC"});

    }
}


