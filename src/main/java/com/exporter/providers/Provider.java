package com.exporter.providers;


import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order;


abstract public class Provider {

    protected String            exchange_name;
    protected CurrencyPair[]    currencyPairs;

    public Provider(String exchange_name, CurrencyPair[] currencyPairs) {
        this.exchange_name = exchange_name;
        this.currencyPairs = currencyPairs;

        for (int i = 0; i < this.currencyPairs.length; ++i) {
            String currencyPair = com.exporter.Trade.serialize_currency_pair(this.currencyPairs[i]);
            com.exporter.Trade.get_volume_counter(this.exchange_name, currencyPair);
            com.exporter.Trade.get_ticker_gauge(this.exchange_name, currencyPair, Order.OrderType.ASK);
            com.exporter.Trade.get_ticker_gauge(this.exchange_name, currencyPair, Order.OrderType.BID);
        }
    }

}
