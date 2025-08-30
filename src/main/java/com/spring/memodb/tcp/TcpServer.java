package com.spring.memodb.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.spring.memodb.utils.ApplicationConstants;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple TCP server that listens for incoming connections and delegates each
 * connection to a TcpWorker. The server runs on a specified port and uses a
 * virtual thread pool to handle multiple clients concurrently.
 */
@Component
@Slf4j
public class TcpServer implements CommandLineRunner {

    // TCP server port provided via application properties (or defaults to 6379)
    @Value("${tcp.server.port:6379}")
    private int port;

    private String appName = ApplicationConstants.APP_NAME;

    // The server socket that listens for incoming connections
    private static ServerSocket serverSocket;

    // Executor service with virtual threads
    ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    /**
     * Starts the TCP server and listens for incoming connections. Each
     * connection is handled by a TcpWorker running in a separate virtual thread
     * from the thread pool.
     */
    @Override
    public void run(String[] args) {
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true); // allow quick restart of server on same port
            serverSocket.bind(new InetSocketAddress(port));

            log.info("{} server started on port {}", appName, port);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept(); // accept new client connection
                    executorService.submit(new TcpWorker(clientSocket)); // handle connection in separate virtual thread
                } catch (IOException e) {
                    if (serverSocket.isClosed()) {
                        log.debug("Server socket closed, stopping accept loop.");
                        break; // exit loop on shutdown
                    }
                    log.error("Error accepting client connection", e);
                }
            }
        } catch (IOException e) {
            log.error("Error starting TCP server", e);
        }
    }

    /**
     * Shuts down the server socket and executor service when the application is
     * stopping.
     */
    @PreDestroy
    private void destroy() {
        log.debug("Shutting down TCP server...");
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // unblocks accept()
            }
        } catch (IOException ignore) {
        }
        executorService.shutdown();
    }
}
