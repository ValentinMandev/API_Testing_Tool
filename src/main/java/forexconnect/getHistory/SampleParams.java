/* Copyright 2019 FXCM Global Services, LLC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use these files except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package forexconnect.getHistory;

import com.candleworks.quotesmgr.OpenPriceCandlesMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class SampleParams {
    public static final String INSTRUMENT_NOT_SPECIFIED = "'Instrument' is not specified (/i|-i|/instrument|--instrument)";
    public static final String TIMEFRAME_NOT_SPECIFIED = "'Timeframe' is not specified (/timeframe|--timeframe)";

    // Getters

    public String getInstrument() {
        return mInstrument;
    }

    private String mInstrument;

    public String getTimeframe() {
        return mTimeframe;
    }
    private String mTimeframe;

    public Calendar getDateFrom() {
        return mDateFrom;
    }

    private Calendar mDateFrom;

    public Calendar getDateTo() {
        return mDateTo;
    }

    private Calendar mDateTo;

    public int getQuotesCount() {
        return mQuotesCount;
    }

    private int mQuotesCount;

    public OpenPriceCandlesMode getOpenPriceCandlesMode() {
        return mOpenPriceCandlesMode;
    }

    private OpenPriceCandlesMode mOpenPriceCandlesMode;

    // Setters

    public void setDateFrom(Calendar dtFrom) {
        mDateFrom = dtFrom;
    }

    public void setDateTo(Calendar dtTo) {
        mDateTo = dtTo;
    }

    // ctor
    public SampleParams(String[] args) throws ParseException {

        mOpenPriceCandlesMode = OpenPriceCandlesMode.OpenPricePrevClose;

        // Get parameters with short keys
        mInstrument = args[4];
        mTimeframe = args[5];

        if (args.length > 7) {
            DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            df.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));

            try {
                String sDateFrom = args[6];
                mDateFrom = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                mDateFrom.setTime(df.parse(sDateFrom));

                String sDateTo = args[7];
                mDateTo = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                mDateTo.setTime(df.parse(sDateTo));
            } catch (Exception e) {
                mDateFrom = null;
                mDateTo = null;
            }

            if (args.length > 8) {
                mQuotesCount = Integer.parseInt(args[8]);
            } else {
                mQuotesCount = -1;
            }

        } else {
            mQuotesCount = Integer.parseInt(args[6]);
            mDateFrom = null;
            mDateTo = null;
        }
    }

}
