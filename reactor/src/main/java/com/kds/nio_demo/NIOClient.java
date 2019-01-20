package com.kds.nio_demo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
    private static final int PORT = 9999;
    private static final int BUFFER_SIZE = 1024;
    private static String[] messages = {"Message_1 ", "Message_2 ", "Message_3 ", "Message_4 ", "Message_5"};

    public static void main(String[] args) {
        logger("Starting NIOClient");
        try {
            InetAddress hostIP = InetAddress.getLocalHost();
            InetSocketAddress inetAddress = new InetSocketAddress(hostIP, PORT);
            SocketChannel socketChannel = SocketChannel.open(inetAddress);

            logger(String.format("Trying to connect to %s:%d...", inetAddress.getHostName(), inetAddress.getPort()));

            for (String msg : messages) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
                byteBuffer.put(msg.getBytes());
                byteBuffer.flip();
                int bytesWritten = socketChannel.write(byteBuffer);
                logger(String.format("Sending Message...: %s\nbytesWritten...: %d", msg, bytesWritten));
                Thread.sleep(5000);
            }
            logger("Closing Client connection...");
            socketChannel.close();
        } catch (IOException | InterruptedException e) {
            logger(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void logger(String msg) {
        System.out.println(msg);
    }
}
