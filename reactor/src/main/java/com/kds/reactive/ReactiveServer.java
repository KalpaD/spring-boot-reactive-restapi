package com.kds.reactive;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReactiveServer implements Runnable {

    private Map<Integer, EventHandler> registeredHandlers = new ConcurrentHashMap<Integer, EventHandler>();
    private static final int PORT = 8888;
    private Selector selector;

    public ReactiveServer() throws IOException {

        // create and unbounded server socket channel
        ServerSocketChannel server = ServerSocketChannel.open();
        // bind that to the selected port
        server.bind(new InetSocketAddress(PORT));
        // configure server socket channel to be non-blocking
        server.configureBlocking(false);

        // create a selector
        selector = Selector.open();

        // here server act as the channel, and we are registering a Selector with Accept event which we are interested.
        SelectionKey selectionKey = server.register(selector, SelectionKey.OP_ACCEPT);

        // keep the events and relevant handlers in a map
        registeredHandlers.put(SelectionKey.OP_ACCEPT, new AcceptEventHandler(selector));
        registeredHandlers.put(SelectionKey.OP_READ, new ReadEventHandler(selector));
        // write event handler does not need to selector, since it does not register any event interest.
        registeredHandlers.put(SelectionKey.OP_WRITE, new WriteEventHandler());
    }

    public void run() {
        try {
            // Event Loop
            while (true) {
                selector.select();

                Set<SelectionKey> readyHandles = selector.selectedKeys();
                Iterator<SelectionKey> handleIterator = readyHandles.iterator();

                while (handleIterator.hasNext()) {
                    SelectionKey key = handleIterator.next();

                    if (key.isAcceptable()) {
                        EventHandler handler =
                                registeredHandlers.get(SelectionKey.OP_ACCEPT);
                        handler.handleEvent(key);
                        handleIterator.remove();
                    }

                    if (key.isReadable()) {
                        EventHandler handler =
                                registeredHandlers.get(SelectionKey.OP_READ);
                        handler.handleEvent(key);
                        handleIterator.remove();
                    }

                    if (key.isWritable()) {
                        EventHandler handler =
                                registeredHandlers.get(SelectionKey.OP_WRITE);
                        handler.handleEvent(key);
                        handleIterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void logger(String msg) {
        String log  = String.format("%s: %s", Thread.currentThread().getName(), msg);
        System.out.println(log);
    }

    public static void main(String [] args) {
        logger("Reactive server starting");
        try {
            ReactiveServer reactiveServer = new ReactiveServer();

            Thread eventLoopThread = new Thread(reactiveServer);
            eventLoopThread.setName("eventLoopThread");

            eventLoopThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
