package fixapi;

import quickfix.*;
import quickfix.field.Account;
import quickfix.field.OrdType;
import quickfix.field.Side;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Edit of FIXTradingTester.java
 * <p>
 * Separated MyApp to separate file and altered program flow
 */
public class FIXTradingTester {

    private static Set<Map<Integer, List<String>>> marketData = new LinkedHashSet<>();
    private static final String LOG_PATH = "appRun/log/";
    private static String outputFile = "output/fixapi/subscribe_market_data.txt";
    private static FileOutputStream outputStream;

    static {
        try {
            outputStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final PrintStream out = new PrintStream(outputStream);

    public static void main(String[] args) {

        if (args.length >= 2) {
            String config1 = args[0];
            FileInputStream fileInputStream = null;

            try {
                fileInputStream = new FileInputStream(config1);
                System.out.println("Cleaning Trading session log files");
                clearLogs(fileInputStream);

                fileInputStream = new FileInputStream(config1);
                SessionSettings settings = new SessionSettings(fileInputStream);
                fileInputStream.close();
                MyApp tradingApp = new MyApp(settings, marketData);
                MessageStoreFactory storeFactory = new FileStoreFactory(settings);
                LogFactory logFactory = new FileLogFactory(settings);
                MessageFactory messageFactory = new DefaultMessageFactory();
                SocketInitiator tradingInitiator = new SocketInitiator(tradingApp, storeFactory, settings, logFactory, messageFactory);
                tradingInitiator.start();
                Thread.sleep(5000);
                runExample(tradingApp, args[args.length - 1]);

                tradingInitiator.stop(true);

                if (args.length == 3) {
                    String config2 = args[1];

                    fileInputStream = new FileInputStream(config2);
                    System.out.println("Cleaning Market data session log files");
                    clearLogs(fileInputStream);

                    fileInputStream = new FileInputStream(config2);
                    settings = new SessionSettings(fileInputStream);
                    fileInputStream.close();
                    MyApp mdApp = new MyApp(settings, marketData);
                    MessageStoreFactory mdStoreFactory = new FileStoreFactory(settings);
                    LogFactory mdLogFactory = new FileLogFactory(settings);
                    SocketInitiator mdInitiator = new SocketInitiator(mdApp, mdStoreFactory, settings, mdLogFactory, messageFactory);
                    mdInitiator.start();
                    Thread.sleep(5000);

                    runExampleMD(mdApp, args[args.length - 1]);

                    mdInitiator.stop(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: Supply configuration file");
        }

    }

    private static void clearLogs(FileInputStream stream) {
        String[] parameters = getSenderCompIdAndTargetSubId(stream);

        String filename = String.format("FIX.4.4-%s-FXCM_%s.event.log",
                parameters[0], parameters[1]);

        deleteLogFile(filename);

        filename = String.format("FIX.4.4-%s-FXCM_%s.messages.log",
                parameters[0], parameters[1]);

        deleteLogFile(filename);
    }

    private static void deleteLogFile(String filename) {
        boolean fileDeleted = new File(LOG_PATH + filename).delete();
        if (!fileDeleted) {
            System.out.println("File " + filename + " not found, no need to delete.");
        }
    }

    private static String[] getSenderCompIdAndTargetSubId(FileInputStream stream) {
        Map<String, String> config = new HashMap<>();

        Scanner scanner = new Scanner(stream);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            if (s.contains("=")) {
                String[] parameter = s.split("=");
                config.putIfAbsent(parameter[0], parameter[1]);
            }
        }

        return new String[]{config.get("SenderCompID"), config.get("TargetSubID")};
    }

    public static void runExample(MyApp application, String instruments) {
        try {
            String accountNumber = application.getAccts().toArray()[0].toString().split("=")[1];

            String[] instrumentArray = instruments.split(", ");

            for (String instrument : instrumentArray) {
                application.sendMarketOrder(new Account(accountNumber), new Side('2'), instrument);
                Thread.sleep(5000);
                application.sendMarketOrder(new Account(accountNumber), new Side('1'), instrument);
                Thread.sleep(5000);

                Double rate = marketData.stream()
                        .filter(d -> d.get(55).get(0).equals(instrument))
                        .map(d -> Double.parseDouble(d.get(270).get(0)))
                        .collect(Collectors.toList())
                        .get(0);

                application.sendEntryOrder(new Account(accountNumber), new Side('2'), instrument, new OrdType('2'), rate + rate * 0.01);
                application.sendEntryOrder(new Account(accountNumber), new Side('1'), instrument, new OrdType('3'), rate + rate * 0.01);
                Thread.sleep(5000);
            }

//            application.sendOrdersRequest();
//            Thread.sleep(5000);


        } catch (Exception e) {
        }
    }

    public static void runExampleMD(MyApp application, String instruments) {
        try {
            application.sendMarketDataRequest('2');
            Thread.sleep(2000);
            marketData.clear();
            application.sendMarketDataRequest('1', List.of(instruments.split(", ")));
            Thread.sleep(12000);

            marketData.forEach(snapshot -> out.printf("%s: Date and time:%s Bid:%s Ask:%s\n",
                    snapshot.get(55).get(0), snapshot.get(52).get(0), snapshot.get(270).get(0), snapshot.get(270).get(1)));

        } catch (Exception e) {
        }
    }
}

