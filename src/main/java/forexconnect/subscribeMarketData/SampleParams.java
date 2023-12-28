package forexconnect.subscribeMarketData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SampleParams {
    public static final String INSTRUMENT_NOT_SPECIFIED = "'Instrument' is not specified (/i|-i|/instrument|--instrument)";
    public static final String BUYSELL_NOT_SPECIFIED = "'BuySell' is not specified (/d|-d|/buysell|--buysell)";
    public static final String RATE_NOT_SPECIFIED = "'Rate' is not specified (/r|-r|/rate|--rate)";
    public static final String RATESTOP_NOT_SPECIFIED = "'RateStop' is not specified (/ratestop|--ratestop)";
    public static final String RATELIMIT_NOT_SPECIFIED = "'RateLimit' is not specified (/ratelimit|--ratelimit)";
    public static final String ORDERID_NOT_SPECIFIED = "'OrderID' is not specified (/orderid|--orderid)";
    public static final String PRIMARYID_NOT_SPECIFIED = "'PrimaryID' is not specified (/primaryid|--primaryid)";
    public static final String SECONDARYID_NOT_SPECIFIED = "'SecondaryID' is not specified (/secondaryid|--secondaryid)";
    public static final String CONTINGENCYID_NOT_SPECIFIED = "'ContingencyID' is not specified (/contingencyid|--contingencyid)";
    public static final String TIMEFRAME_NOT_SPECIFIED = "'Timeframe' is not specified (/timeframe|--timeframe)";
    public static final String STATUS_NOT_SPECIFIED = "'SubscriptionStatus' is not specified (/status|--status)";

    public static final String ORDERS_TABLE = "orders";
    public static final String TRADES_TABLE = "trades";

    // Getters

    public String getInstrument() {
        return mInstrument;
    }
    private String mInstrument;

    public String getBuySell() {
        return mBuySell;
    }
    private String mBuySell;

    public String getContingencyID() {
        return mContingencyID;
    }
    private String mContingencyID;

    public String getOrderID() {
        return mOrderID;
    }
    private String mOrderID;

    public String getPrimaryID() {
        return mPrimaryID;
    }
    private String mPrimaryID;

    public String getSecondaryID() {
        return mSecondaryID;
    }
    private String mSecondaryID;

    public String getTimeframe() {
        return mTimeframe;
    }
    private String mTimeframe;

    public int getLots() {
        return mLots;
    }
    private int mLots;

    public String getAccountID() {
        return mAccountID;
    }
    private String mAccountID;

    public String getOrderType() {
        return mOrderType;
    }
    private String mOrderType;

    public Calendar getDateFrom() {
        return mDateFrom;
    }
    private Calendar mDateFrom;

    public Calendar getDateTo() {
        return mDateTo;
    }
    private Calendar mDateTo;

    public double getRate() {
        return mRate;
    }
    private double mRate;

    public double getRateStop() {
        return mRateStop;
    }
    private double mRateStop;

    public double getRateLimit() {
        return mRateLimit;
    }
    private double mRateLimit;

    public String getStatus() {
        return mStatus;
    }
    private String mStatus;

    public String getExpireDate() {
        return mExpireDate;
    }
    private String mExpireDate;

    public String getTableType() {
        return mTableType;
    }
    private String mTableType;

    // Setters

    public void setAccountID(String sAccountID) {
        mAccountID = sAccountID;
    }

    public void setOrderType(String sOrderType) {
        mOrderType = sOrderType;
    }

    public void setDateFrom(Calendar dtFrom) {
        mDateFrom = dtFrom;
    }

    public void setDateTo(Calendar dtTo) {
        mDateTo = dtTo;
    }

    public void setTableType(String sTableType) {
        mTableType = sTableType;
    }

    // ctor
    public SampleParams(String[] args) {
        // Get parameters with short keys
        mInstrument = args[4];
    }
}
