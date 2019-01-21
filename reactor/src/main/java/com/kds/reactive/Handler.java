package com.kds.reactive;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Handler implements Runnable {
    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;

    private static final int READ_BUF_SIZE = 1024;
    private static final int WRiTE_BUF_SIZE = 1024;
    private ByteBuffer readBuf = ByteBuffer.allocate(READ_BUF_SIZE);
    private ByteBuffer writeBuf = ByteBuffer.allocate(WRiTE_BUF_SIZE);

    public Handler(Selector selector, SocketChannel socketChannel) throws IOException {
        logger("Handler constructor invoked");
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);

        // Register socketChannel with _selector listening on OP_READ events.
        // Callback: Handler, selected when the connection is established and ready for READ
        selectionKey = this.socketChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(this);
        selector.wakeup(); // let blocking select() return
        logger("Handler constructor finished ");
    }

    public void run() {
        logger("Handler run invoked");
        try {
            if (selectionKey.isReadable()) {
                read();
            }
            else if (selectionKey.isWritable()) {
                write();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Process data by echoing input to output
    synchronized void process() {
        logger("Handler process() invoked");
        readBuf.flip();
        byte[] bytes = new byte[readBuf.remaining()];
        readBuf.get(bytes, 0, bytes.length);
        System.out.println("process(): " + new String(bytes, Charset.forName("ISO-8859-1")));

        writeBuf = ByteBuffer.wrap(bytes);

        // Set the key's interest to WRITE operation
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        selectionKey.selector().wakeup();
        logger("Handler process() finished");
    }

    synchronized void read() throws IOException {
        try {
            logger("Handler read() invoked");
            int numBytes = socketChannel.read(readBuf);
            System.out.println("read(): #bytes read into 'readBuf' buffer = " + numBytes);

            if (numBytes == -1) {
                selectionKey.cancel();
                socketChannel.close();
                System.out.println("read(): client connection might have been dropped!");
            }
            else {
                logger("Handler before getWorkerPool().execute() invoked");
                ReactiveEchoServer.getWorkerPool().execute(new Runnable() {
                    public void run() {
                        process();
                    }
                });
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }

    void write() throws IOException {
        int numBytes = 0;

        try {
            logger("Handler write() invoked");
            numBytes = socketChannel.write(writeBuf);
            System.out.println("write(): #bytes read from 'writeBuf' buffer = " + numBytes);

            if (numBytes > 0) {
                readBuf.clear();
                writeBuf.clear();

                // Set the key's interest-set back to READ operation
                selectionKey.interestOps(SelectionKey.OP_READ);
                selectionKey.selector().wakeup();
                logger("Handler write() finished");
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private static void logger(String msg) {
        String log  = String.format("%s: %s", Thread.currentThread().getName(), msg);
        System.out.println(log);
    }
}
