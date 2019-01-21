package com.kds.improved;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ImprovedWriteEventHandler implements Runnable, ImprovedEventHandler {

    private SelectionKey selectionKey;
    private Selector selector;

    public ImprovedWriteEventHandler(Selector selector, SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
        this.selector = selector;
    }

    public synchronized void handleEvent() throws Exception {
        logger("Write event handler invoked.");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        if (socketChannel != null && socketChannel.isConnected()) {
            // recall the byte buffer we set to this selection key in the read event handler
            ByteBuffer messageBuffer = (ByteBuffer) selectionKey.attachment();
            if (messageBuffer != null) {
                int numofbytes = socketChannel.write(messageBuffer);
                messageBuffer.clear();
                if (numofbytes > 0) {
                    socketChannel.close();
                    //socketChannel.register(selector, SelectionKey.OP_READ);
                    //selector.wakeup();
                }
            }
        }
        //socketChannel.close();


        logger("Write event handler completed.");
    }

    private static void logger(String msg) {
        String log  = String.format("%s: %s", Thread.currentThread().getName(), msg);
        System.out.println(log);
    }

    @Override
    public void run() {
        try {
            handleEvent();
        } catch (Exception e) {
           // e.printStackTrace();
        }
    }
}
