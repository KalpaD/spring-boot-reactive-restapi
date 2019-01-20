package com.kds.nio_demo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    private static final int PORT = 8888;
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;

    public static void main(String[] args) {
        logger("Starting NIOServer...");
        try {
            InetAddress hostIP = InetAddress.getLocalHost();
            logger(String.format("Trying to accept connections on %s:%d...", hostIP.getHostAddress(), PORT));
            // create selector via open();
            selector = Selector.open();
            // create a server socket channel
            ServerSocketChannel server = ServerSocketChannel.open();
            // get the server socket
            ServerSocket serverSocket = server.socket();
            InetSocketAddress address = new InetSocketAddress(hostIP, PORT);
            // bind the server socket to address
            serverSocket.bind(address);
            // configure socket to be non-blocking
            server.configureBlocking(false);
            // register selector interest for accept event.
            server.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                // get a channel from selector, this will block until a channel get selected
                selector.select();
                // get keys for that channel
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();

                // go through selection keys one by one and see any of those events are ready
                // if ready process that
                while (i.hasNext()) {
                    SelectionKey key = i.next();

                    if (key.isAcceptable()) {

                        processAcceptEvent(server, key);

                    } else if (key.isReadable()) {

                        processReadEvent(key);
                    }
                    i.remove();
                }
            }
        } catch (IOException e) {
            logger(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle the accept event
     *
     * @param socket    Server socket channel
     * @param key       Selection key
     * @throws IOException  In case of error while accept the connection
     */
    private static void processAcceptEvent(ServerSocketChannel socket, SelectionKey key) throws IOException {
        logger("Connection Accepted...");
        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = socket.accept();
        socketChannel.configureBlocking(false);
        // Register interest in reading this channel
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * Handle the read event
     *
     * @param key    Selection key for the channel.
     * @throws IOException
     */
    private static void processReadEvent(SelectionKey key) throws IOException {
        logger("Handling ReadEvent...");
        // create a ServerSocketChannel to read the request
        SocketChannel client = (SocketChannel) key.channel();
        // Set up out 1k buffer to read data into
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        client.read(byteBuffer);
        String data = new String(byteBuffer.array()).trim();
        if (data.length() > 0) {
            logger(String.format("Message Received.....: %s\n", data));
        } else {
            client.close();
            logger("Closing Server Connection...");
        }
    }

    private static void logger(String msg) {
        System.out.println(msg);
    }
}
