package com.tsluszniak.stockmonitor.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tsluszniak.stockmonitor.data.Contract;
import com.tsluszniak.stockmonitor.data.PrefUtils;
import com.tsluszniak.stockmonitor.retrofit.APIService;
import com.tsluszniak.stockmonitor.retrofit.APIUtils;
import com.tsluszniak.stockmonitor.retrofit.Datum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public final class QuoteSyncJob {

    private static final int ONE_OFF_ID = 2;
    public static final String ACTION_DATA_UPDATED = "com.tsluszniak.stockmonitor.ACTION_DATA_UPDATED";
    public static final String ACTION_UPDATE_WIDGET = "android.appwidget.action.APPWIDGET_UPDATE";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 2;

    static String apiKey = "7F9gAvnhniRoBgREJrFfOONlQUtzdbwqVCA5k90GZZgToGitJjciYbzDwMB0";


    private QuoteSyncJob() {
    }

    static void getQuotes(Context context) {

        Timber.d("Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);


        Set<String> stockPref = PrefUtils.getStocks(context);
        Set<String> stockCopy = new HashSet<>();
        stockCopy.addAll(stockPref);
        String[] stockArray = stockPref.toArray(new String[stockPref.size()]);
        List<String> stockList = new ArrayList<>();

        for (int i = 0; i < stockArray.length; i++) {
            stockList.add(stockArray[i]);
        }

        Timber.d("stocks in prefs: " + stockCopy.toString());

        if (stockArray.length == 0) {
            return;
        }

        fetchStockQuotes(stockList, context);
    }


    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");


        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }


    public static synchronized void initialize(final Context context) {

        schedulePeriodic(context);
        syncImmediately(context);
    }

    public static synchronized void syncImmediately(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());


        }
    }

    // ===================================

    static void fetchStockQuotes(List<String> symbols, final Context context) {

        APIService mAPIService;
        mAPIService = APIUtils.getProductionAPIService();
        String queryString = symbols.toString().replace(" ", "").replace("[", "").replace("]", "");
        mAPIService.getStockQuotes(queryString, apiKey).enqueue(new Callback<com.tsluszniak.stockmonitor.retrofit.StockQuote>() {
            @Override
            public void onResponse(Call<com.tsluszniak.stockmonitor.retrofit.StockQuote> call, Response<com.tsluszniak.stockmonitor.retrofit.StockQuote> response) {

                if (response.isSuccessful() && !response.body().getData().isEmpty()) {

                    saveQuotes(response.body().getData(), context);
                }
            }

            @Override
            public void onFailure(Call<com.tsluszniak.stockmonitor.retrofit.StockQuote> call, Throwable t) {

            }
        });
    }


    static void saveQuotes(List<Datum> quotesData, Context context) {

        ArrayList<ContentValues> quoteCVs = new ArrayList<>();

        for (Datum data : quotesData) {


            if (data.getPrice() == null) {
                //System.out.println("getQuotes - quote " + data.getSymbol() + " does not exist");
                continue;
            }

            float price = Float.parseFloat(data.getPrice());
            float change = Float.parseFloat(data.getDayChange());
            float percentChange = Float.parseFloat(data.getChangePct());


            //StringBuilder historyBuilder = new StringBuilder();

            /*try {

                //TODO get historical data for every quote
                //List<HistoricalQuote> history = stock.getHistory(from, to, Interval.WEEKLY);

                for (HistoricalQuote it : history) {
                    historyBuilder.append(it.getDate().getTimeInMillis());
                    historyBuilder.append(", ");
                    historyBuilder.append(it.getClose());
                    historyBuilder.append("\n");

                    //System.out.println("quote history sample: " + it.getClose());
                }
            } catch (IOException exception) {
                Timber.e(exception, "Error fetching history. ");
                historyBuilder.append("10000000000, 111.22");
            } */

            ContentValues quoteCV = new ContentValues();
            quoteCV.put(Contract.Quote.COLUMN_SYMBOL, data.getSymbol());
            quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
            quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
            quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
            quoteCV.put(Contract.Quote.COLUMN_HISTORY, ""/*historyBuilder.toString()*/);    //TODO

            quoteCVs.add(quoteCV);
        }

        context.getContentResolver()
                .bulkInsert(
                        Contract.Quote.URI,
                        quoteCVs.toArray(new ContentValues[quoteCVs.size()]));

        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        context.sendBroadcast(dataUpdatedIntent);
        Intent updateWidgetIntent = new Intent(ACTION_UPDATE_WIDGET);
        context.sendBroadcast(updateWidgetIntent);
    }
}
