package com.johnmillercoding.cryptonotify.models;

public class ExchangePrice {

    // Class vars
    private final String coin, exchange;
    private final double btcValue, usdValue;

    public ExchangePrice(String coin, String exchange, double btcValue, double usdValue){
        this.coin = coin;
        this.exchange = exchange;
        this.btcValue = btcValue;
        this.usdValue = usdValue;
    }
    public String getCoin() {
        return coin;
    }

//    public void setCoin(String coin) {
//        this.coin = coin;
//    }

    public String getExchange() {
        return exchange;
    }

//    public void setExchange(String exchange) {
//        this.exchange = exchange;
//    }

    public double getBtcValue() {
        return btcValue;
    }

//    public void setBtcValue(double btcValue) {
//        this.btcValue = btcValue;
//    }

    public double getUsdValue() {
        return usdValue;
    }

//    public void setUsdValue(double usdValue) {
//        this.usdValue = usdValue;
//    }
}
