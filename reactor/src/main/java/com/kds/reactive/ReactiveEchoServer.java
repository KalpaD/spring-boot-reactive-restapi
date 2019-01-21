package com.kds.reactive;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReactiveEchoServer implements Runnable {

    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private static final int WORKER_POOL_SIZE = 10;
    private static ExecutorService _workerPool;


    ReactiveEchoServer(int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        // Register serverSocketChannel with selector listening on OP_ACCEPT events.
        // Callback: Acceptor, selected when a new connection incomes.
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor());
    }

    @Override
    public void run() {
        try {
            // Event Loop
            while (true) {
                selector.select();
                Iterator it = selector.selectedKeys().iterator();

                while (it.hasNext()) {
                    SelectionKey sk = (SelectionKey) it.next();
                    it.remove();
                    Runnable r = (Runnable) sk.attachment(); // handler or acceptor callback/runnable
                    if (r != null) {
                        r.run();
                    }
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static ExecutorService getWorkerPool() {
        return _workerPool;
    }

    // Acceptor: if connection is established, assign a handler to it.
    private class Acceptor implements Runnable {
        public void run() {
            try {
                logger("Acceptor invoked");
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    new Handler(selector, socketChannel);
                    logger("Acceptor finished");
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        _workerPool = Executors.newFixedThreadPool(WORKER_POOL_SIZE);

        try {
            new Thread(new ReactiveEchoServer(9090), "eventLoopThread").start(); // a single thread blocking on selector for events
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private static void logger(String msg) {
        String log  = String.format("%s: %s", Thread.currentThread().getName(), msg);
        System.out.println(log);
    }
}
