package com.exporter.providers;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.huobi.HuobiExchange;
import com.xeiam.xchange.huobi.service.streaming.HuobiExchangeStreamingConfiguration;
import com.xeiam.xchange.service.streaming.ExchangeEvent;
import com.xeiam.xchange.service.streaming.ExchangeEventType;
import com.xeiam.xchange.service.streaming.ExchangeStreamingConfiguration;
import com.xeiam.xchange.service.streaming.StreamingExchangeService;


public class Huobi extends Provider {

    public Huobi() {
        super("huobi", new CurrencyPair[]{CurrencyPair.BTC_CNY});
    }

    public void start_polling() throws Exception {
        ExchangeSpecification exSpec = new ExchangeSpecification(HuobiExchange.class);

        Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exSpec);
        ExchangeStreamingConfiguration streamingConfiguration = new HuobiExchangeStreamingConfiguration(this.currencyPairs);
        final StreamingExchangeService service = exchange.getStreamingExchangeService(streamingConfiguration);

        Thread consumer = new Thread("trades-huobi") {

            @Override
            public void run() {

                while (!isInterrupted()) {
                    try {
                        ExchangeEvent event = service.getNextEvent();

                        if (event != null) {
                            if (event.getEventType().equals(ExchangeEventType.TRADE)) {
                                Trade trade = (Trade) event.getPayload();
                                com.exporter.Trade.export_trade(exchange_name, trade);
                            }
                        }

                    } catch (InterruptedException e) {
                        this.interrupt();
                    }
                }
            }

        };

        // Start consumer.
        consumer.start();

        // Start streaming service.
        service.connect();
    }

}
