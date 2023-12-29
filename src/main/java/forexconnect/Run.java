package forexconnect;
import forexconnect.createStopAndLimitEntryOrders.CreateEntryOrder;
import forexconnect.getHistory.GetHistoryData;
import forexconnect.subscribeMarketData.SubscribeMarketData;
import forexconnect.createMarketOrder.CreateMarketOrder;

public class Run {

    public static void main(String username, String password, String connection, String instruments, String dateFrom, String dateTo, String timeframe) {

        GetHistoryData.run(new String[]{username, password, "https://fxcorporate.com/Hosts.jsp", connection, instruments, timeframe, dateFrom, dateTo});

        SubscribeMarketData.run(new String[]{username, password, "https://fxcorporate.com/Hosts.jsp", connection, instruments});

        CreateMarketOrder.run(new String[]{username, password, "https://fxcorporate.com/Hosts.jsp", connection, instruments, "B", "1"});

        // Stop entry:
        CreateEntryOrder.run(new String[]{username, password, "https://fxcorporate.com/Hosts.jsp", connection, instruments, "S", "1", "stop"});

        // Limit entry:
        CreateEntryOrder.run(new String[]{username, password, "https://fxcorporate.com/Hosts.jsp", connection, instruments, "B", "1", "limit"});
    }
}


