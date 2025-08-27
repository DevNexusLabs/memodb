package com.spring.memodb.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

import lombok.extern.slf4j.Slf4j;

/**
 * Worker class that handles communication with a connected TCP client. Each
 * instance runs in its own virtual thread and processes messages from the
 * client until the connection is closed.
 */
@Slf4j
public class TcpWorker implements Runnable {

    // The client socket associated with this worker.
    private final Socket clientSocket;

    private static final int CONNECTION_TIMEOUT_MS = 60 * 1000; // Connection idle timeout in milliseconds, currently 60 seconds

    public TcpWorker(Socket clientSocket) {
        log.info("Accepted connection from {}", clientSocket.getRemoteSocketAddress());

        this.clientSocket = clientSocket;

        try {
            clientSocket.setSoTimeout(CONNECTION_TIMEOUT_MS); // set read timeout
        } catch (IOException e) {
            log.error("Error setting socket timeout", e);
        }
    }

    /*
     * Handles communication with the connected client. Reads messages from the
     * client and prints them to the console. Closes the connection when the
     * client disconnects or an error occurs.
     * 
     * TODO: Implement actual message processing logic as needed.
     */
    @Override
    public void run() {
        // Buffered reader to read text from the client socket
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"))) {

            // Read messages from the client until the connection is closed
            while (true) {
                try {
                    /**
                     * Read a line from the client. Blocking call, so this
                     * thread will wait here until a message is received or the
                     * client disconnects.
                     *
                     * TODO: Connection will still close if client keeps sending
                     * messages without a newline character. Need to handle that
                     * case as well. Read each byte and keep track of idle time?
                     */
                    String message = reader.readLine();

                    // If message is null, the client has disconnected
                    if (message == null) {
                        log.info("Client disconnected: {}", clientSocket.getRemoteSocketAddress());
                        break;
                    }

                    /**
                     * Print the received message to the console. TODO: Replace
                     * this logic as needed.
                     */
                    log.info("Received message from {}: {}", clientSocket.getRemoteSocketAddress(), message);
                } catch (SocketTimeoutException e) {
                    log.info("No data for {} milliseconds, closing connection to {}", CONNECTION_TIMEOUT_MS, clientSocket.getRemoteSocketAddress());
                    break;
                }
            }

        } catch (IOException e) {
            log.error("Error handling client connection", e);
        } finally {
            // Ensure the socket is closed when done
            try {
                clientSocket.close();
            } catch (IOException ignore) {
            }
        }
    }
}
