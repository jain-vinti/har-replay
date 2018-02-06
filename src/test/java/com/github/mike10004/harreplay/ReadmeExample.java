package com.github.mike10004.harreplay;

import com.github.mike10004.nativehelper.subprocess.ProcessMonitor;
import com.github.mike10004.nativehelper.subprocess.ScopedProcessTracker;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;

/**
 * Example code for the readme.
 */
@SuppressWarnings({"unused", "SameParameterValue"})
public class ReadmeExample {

    public void execute(File harFile) throws IOException, InterruptedException {
        ReplayManagerConfig replayManagerConfig = ReplayManagerConfig.auto();
        ReplayManager replayManager = new ReplayManager(replayManagerConfig);
        ReplaySessionConfig sessionConfig = ReplaySessionConfig.usingTempDir()
                .build(harFile);
        try (ScopedProcessTracker processTracker = new ScopedProcessTracker()) {
            ProcessMonitor<?, ?> serverMonitor = replayManager.startAsync(processTracker, sessionConfig);
            doSomethingWithProxy("localhost", sessionConfig.port);
            serverMonitor.destructor().sendTermSignal().await().kill().awaitKill();
        }
    }

    protected void doSomethingWithProxy(String host, int port) throws IOException {
        System.out.format("do something with proxy on %s:%d%n", host, port);
        try (CloseableHttpClient client = HttpClients.custom().setProxy(new HttpHost(host, port)).build()) {
            try (CloseableHttpResponse response = client.execute(new HttpGet("http://www.example.com/"))) {
                System.out.println("response: " + response.getStatusLine());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new ReadmeExample().execute(new File(ReadmeExample.class.getResource("/https.www.example.com.har").toURI()));
    }
}
