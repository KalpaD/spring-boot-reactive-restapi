package com.kds.improved;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ImprovedAcceptEventHandler implements ImprovedEventHandler {

    private Selector selector;
    private SelectionKey selectionKey;

    public ImprovedAcceptEventHandler(Selector selector, SelectionKey selectionKey) {
        this.selector = selector;
        this.selectionKey = selectionKey;
    }

    public synchronized void handleEvent() throws Exception {
        logger("Accept event handler invoked.");
        // get channel from the key
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        // If in non-blocking mode then this method will immediately return null if there are no pending connections.
        // Otherwise it will block indefinitely until a new connection is available or an I/O error occurs.
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel != null && socketChannel.isConnected()) {
            // configure socket channel to be non-blocking
            socketChannel.configureBlocking(false);
            // register the selector for accept events from this socket channel.
            socketChannel.register(selector, SelectionKey.OP_READ);
            //selector.wakeup();
        }
        logger("Accept event handler completed.");
    }

    public void run() {
        try {
            handleEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void logger(String msg) {
        String log  = String.format("%s: %s", Thread.currentThread().getName(), msg);
        System.out.println(log);
    }
}
