package com.tsluszniak.stockmonitor.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockQuote {

    @SerializedName("symbols_requested")
    @Expose
    private Integer symbolsRequested;
    @SerializedName("symbols_returned")
    @Expose
    private Integer symbolsReturned;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public Integer getSymbolsRequested() {
        return symbolsRequested;
    }

    public void setSymbolsRequested(Integer symbolsRequested) {
        this.symbolsRequested = symbolsRequested;
    }

    public Integer getSymbolsReturned() {
        return symbolsReturned;
    }

    public void setSymbolsReturned(Integer symbolsReturned) {
        this.symbolsReturned = symbolsReturned;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

}