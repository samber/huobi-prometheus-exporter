package com.exporter;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.MetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.HashMap;
import java.util.Map;


public class ExposeMetrics {

    public static final Map<String, Counter> volumes = new HashMap<String, Counter>();
    public static final Map<String, Gauge> tickers_ask = new HashMap<String, Gauge>();
    public static final Map<String, Gauge> tickers_bid = new HashMap<String, Gauge>();

    private Server server;

    public ExposeMetrics() {
        this.server = new Server(9999);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        this.server.setHandler(context);

        // Expose Promtheus metrics.
        context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");
    }

    public void start_server() throws Exception {
        // Start the webserver.
        this.server.start();
        this.server.join();
    }
}
