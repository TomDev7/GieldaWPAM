package com.tsluszniak.stockmonitor.retrofit;


public class APIUtils {

    public static final String BASE_PRODUCTION_URL = "https://api.worldtradingdata.com/api/v1/";

    private APIUtils() {

    }

    public static APIService getProductionAPIService() {

        return RetrofitClient.getClient(BASE_PRODUCTION_URL).create(APIService.class);
    }
}