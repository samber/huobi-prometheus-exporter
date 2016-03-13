package com.exporter;

import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

import java.util.Map;


public class Trade {

    static public String serialize_currency_pair(CurrencyPair currencyPair) {
        return currencyPair.toString().replace("/", "").toLowerCase();
    }

    static public Counter get_volume_counter(String exchange_name, String currencyPair) {
        String key = "volume_" + exchange_name + "_" + currencyPair;

        if (ExposeMetrics.volumes.get(key) == null) {
            Counter counter = Counter.build()
                    .name(key)
                    .help("Number of " + currencyPair + " bought or sold on " + exchange_name).register();
            ExposeMetrics.volumes.put(key, counter);
        }

        return ExposeMetrics.volumes.get(key);
    }

    static public Gauge get_ticker_gauge(String exchange_name, String currencyPair, Order.OrderType orderType) {
        String key = "ticker_" + exchange_name + "_" + currencyPair + "_" + orderType.toString();

        Map<String, Gauge> list = null;
        if (orderType.equals(Order.OrderType.ASK))
            list = ExposeMetrics.tickers_ask;
        else if (orderType.equals(Order.OrderType.BID))
            list = ExposeMetrics.tickers_bid;

        if (list.get(key) == null) {
            Gauge gauge = Gauge.build()
                    .name(key)
                    .help(orderType.toString() + " price of " + currencyPair + " on " + exchange_name).register();
            list.put(key, gauge);
        }

        return list.get(key);
    }

    static public void export_trade(String exchange_name, com.xeiam.xchange.dto.marketdata.Trade trade) {
        String currencyPair = serialize_currency_pair(trade.getCurrencyPair());
        Counter volume = get_volume_counter(exchange_name, currencyPair);
        Gauge ticker = get_ticker_gauge(exchange_name, currencyPair, trade.getType());

        volume.inc(trade.getTradableAmount().doubleValue());
        ticker.set(trade.getPrice().doubleValue());
    }

}
