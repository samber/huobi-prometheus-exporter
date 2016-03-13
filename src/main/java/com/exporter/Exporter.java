package com.exporter;


import com.exporter.providers.*;


public class Exporter {

    public static void main(String[] args) throws Exception {
        Huobi huobi = new Huobi();
        ExposeMetrics servlet = new ExposeMetrics();

        huobi.start_polling();
        servlet.start_server();
    }
}
