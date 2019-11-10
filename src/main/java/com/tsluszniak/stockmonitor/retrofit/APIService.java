package com.tsluszniak.stockmonitor.retrofit;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    @GET("stock")
    Call<StockQuote> getStockQuotes(@Query(value = "symbol", encoded = true) String symbols,
                                    @Query("api_token") String apiToken);

    @GET("history")
    Call<String> getStockHistory(@Query("symbol") String symbol,
                                 @Query("api_token") String apiToken);
}