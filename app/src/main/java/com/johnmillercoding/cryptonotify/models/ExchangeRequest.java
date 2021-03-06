package com.johnmillercoding.cryptonotify.models;

/**
 * Class to model an exchange request.
 */
public class ExchangeRequest {

    // Class vars
    private final String coin, exchange, url;
    private final double btcValue, usdValue;

    // Constructor
    public ExchangeRequest(String coin, String exchange, String url, double btcValue, double usdValue){
        this.coin = coin;
        this.exchange = exchange;
        this.url = url;
        this.btcValue = btcValue;
        this.usdValue = usdValue;
    }

    /**
     * Gets the crypto coin.
     * @return the coin.
     */
    public String getCoin() {
        return coin;
    }

    /**
     * Gets the exchange.
     * @return the exchange.
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * Gets the url.
     * @return the url.
     */
    public String getUrl(){ return url; }

    /**
     * Gets the value in Bitcoin.
     * @return the value in BTC.
     */
    public double getBtcValue() {
        return btcValue;
    }

    /**
     * Gets the value in U.S. dollars.
     * @return the value in USD.
     */
    public double getUsdValue() {
        return usdValue;
    }
}
