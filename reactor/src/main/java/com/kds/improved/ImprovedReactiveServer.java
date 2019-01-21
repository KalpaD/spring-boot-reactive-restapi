package com.kds.improved;

import com.kds.reactive.ReactiveServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImprovedReactiveServer implements Runnable {

    //private Map<Integer, ImprovedEventHandler> registeredHandlers = new ConcurrentHashMap<>();
    private ExecutorService executorService;
    private static final int PORT = 8888;
    private Selector selector;

    public ImprovedReactiveServer() throws IOException {

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
        //registeredHandlers.put(SelectionKey.OP_ACCEPT, new ImprovedAcceptEventHandler(selector, selectionKey));
        //registeredHandlers.put(SelectionKey.OP_READ, new ImprovedReadEventHandler(selector, selectionKey));
        // write event handler does not need to selector, since it does not register any event interest.
        //registeredHandlers.put(SelectionKey.OP_WRITE, new ImprovedWriteEventHandler(selectionKey));

        executorService = Executors.newFixedThreadPool(4);
        logger("worker pool size : " + Runtime.getRuntime().availableProcessors());
    }

    public void run() {
        try {
            while (true) { // Loop indefinitely
                logger("reactive server waiting..");
                selector.select();

                Set<SelectionKey> readyHandles = selector.selectedKeys();
                Iterator<SelectionKey> handleIterator = readyHandles.iterator();

                while (handleIterator.hasNext()) {
                    SelectionKey key = handleIterator.next();
                    handleIterator.remove();
                    if (key.isValid() && key.isAcceptable()) {
                        new ImprovedAcceptEventHandler(selector, key).handleEvent();

                    }
                   // logger("key.isReadable() : " + key.isReadable());
                    if (key.isValid() && key.isReadable()) {
                        executorService.execute(new ImprovedReadEventHandler(selector, key));

                    }
                   // logger("key.isWritable() : " + key.isWritable());
                    if (key.isValid() && key.isWritable()) {
                        executorService.execute(new ImprovedWriteEventHandler(selector, key));
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
            ImprovedReactiveServer reactiveServer = new ImprovedReactiveServer();

            Thread eventLoopThread = new Thread(reactiveServer);
            eventLoopThread.setName("eventLoopThread");

            eventLoopThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
