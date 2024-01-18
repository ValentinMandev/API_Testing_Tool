package forexconnect;
import forexconnect.createStopAndLimitEntryOrders.CreateEntryOrder;
import forexconnect.getHistory.GetHistoryData;
import forexconnect.subscribeMarketData.SubscribeMarketData;
import forexconnect.createMarketOrder.CreateMarketOrder;

public class Run {

    public static void main(String username, String password, String url, String connection, String instruments, String dateFrom, String dateTo, String timeframe) {

        GetHistoryData.run(new String[]{username, password, url, connection, instruments, timeframe, dateFrom, dateTo});

        SubscribeMarketData.run(new String[]{username, password, url, connection, instruments});

        CreateMarketOrder.run(new String[]{username, password, url, connection, instruments, "B", "1", "open_position"});
        CreateMarketOrder.run(new String[]{username, password, url, connection, instruments, "S", "1", "close_position"});

        // Stop entry:
        CreateEntryOrder.run(new String[]{username, password, url, connection, instruments, "S", "1", "stop"});

        // Limit entry:
        CreateEntryOrder.run(new String[]{username, password, url, connection, instruments, "B", "1", "limit"});
    }
}


