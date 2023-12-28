package forexconnect;
import forexconnect.createStopAndLimitEntryOrders.CreateEntryOrder;
import forexconnect.getHistory.GetHistoryData;
import forexconnect.subscribeMarketData.SubscribeMarketData;
import forexconnect.createMarketOrder.CreateMarketOrder;

public class Run {

    public static void main(String[] args) {

//        GetHistoryData.run(new String[]{"ValioDemo", "1234", "https://fxcorporate.com/Hosts.jsp", "Demo", "EUR/USD", "D1", "20231013 00:00:00", "20231218 00:00:00", "10"});
//        GetHistoryData.run(new String[]{"ValioDemo", "1234", "https://fxcorporate.com/Hosts.jsp", "Demo", "EUR/USD", "D1", "20231013 00:00:00", "20231218 00:00:00"});
        GetHistoryData.run(new String[]{"ValioDemo", "1234", "https://fxcorporate.com/Hosts.jsp", "Demo", "EUR/USD", "D1", "10"});

        SubscribeMarketData.run(new String[]{"ValioDemo", "1234", "https://fxcorporate.com/Hosts.jsp", "Demo", "USD/JPY"});

        CreateMarketOrder.run(new String[]{"ValioDemo", "1234", "https://fxcorporate.com/Hosts.jsp", "Demo", "USD/JPY", "B", "4"});

        // Stop entry:
        CreateEntryOrder.run(new String[]{"ValioDemo", "1234", "https://fxcorporate.com/Hosts.jsp", "Demo", "EUR/GBP", "S", "2", "stop"});

        // Limit entry:
        CreateEntryOrder.run(new String[]{"ValioDemo", "1234", "https://fxcorporate.com/Hosts.jsp", "Demo", "USD/JPY", "B", "3", "limit"});
    }
}


