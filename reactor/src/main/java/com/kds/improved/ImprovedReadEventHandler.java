package com.kds.improved;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ImprovedReadEventHandler implements Runnable, ImprovedEventHandler {

    private Selector selector;
    private SelectionKey selectionKey;
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);

    public ImprovedReadEventHandler(Selector selector, SelectionKey selectionKey) {
        this.selector = selector;
        this.selectionKey = selectionKey;
    }

    public synchronized void handleEvent() throws Exception {
        logger("Read event handler invoked.");
        // get the channel form the selection key
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        if (socketChannel.isConnected()) {
            int numOfBytes = socketChannel.read(readBuffer);

        /*buffer.flip();

        byte [] bytes = new byte[buffer.limit()];
        buffer.get(bytes);

        logger("Received message : " + new String(bytes));
        buffer.flip();*/
            //logger("#bytes read into 'readBuf' buffer = " + numOfBytes);
            if (numOfBytes > 0) {

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
                selector.wakeup();
                //registerWriteOp.selector().wakeup();
            }
        }

        logger("Read event handler completed.");
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
