package com.kds.reactive;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WriteEventHandler implements EventHandler {


    @Override
    public void handleEvent(SelectionKey key) throws Exception {
        logger("Write event handler invoked.");
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // recall the byte buffer we set to this selection key in the read event handler
        ByteBuffer messageBuffer  = (ByteBuffer) key.attachment();
        socketChannel.write(messageBuffer);
        socketChannel.close();

        logger("Write event handler completed.");
    }

    private static void logger(String msg) {
        String log  = String.format("%s: %s", Thread.currentThread().getName(), msg);
        System.out.println(log);
    }
}
