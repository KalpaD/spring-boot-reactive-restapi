package com.kds.reactive;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ReadEventHandler implements EventHandler {

    private Selector selector;
    private SocketChannel socketChannel;
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);

    public ReadEventHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void handleEvent(SelectionKey key) throws Exception {
        logger("Read event handler invoked.");
        // get the channel form the selection key
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.read(readBuffer);
        /*buffer.flip();

        byte [] bytes = new byte[buffer.limit()];
        buffer.get(bytes);

        logger("Received message : " + new String(bytes));
        buffer.flip();*/
        readBuffer.flip();
        byte[] bytes = new byte[readBuffer.remaining()];
        readBuffer.get(bytes, 0, bytes.length);

        logger("Received message : " + new String(bytes));

        ByteBuffer writeBuffer = ByteBuffer.wrap(bytes);
        readBuffer.flip();

        // register interest in OP_WRITE event on this socket channel, with the newly read message.
        SelectionKey registerWriteOp = socketChannel.register(selector, SelectionKey.OP_WRITE);
        // and attach the byte buffer as an attachment to this event.
        // we can retrieve this in the write handler.
        registerWriteOp.attach(writeBuffer);
        logger("Read event handler completed.");
    }

    private static void logger(String msg) {
        String log  = String.format("%s: %s", Thread.currentThread().getName(), msg);
        System.out.println(log);
    }
}
