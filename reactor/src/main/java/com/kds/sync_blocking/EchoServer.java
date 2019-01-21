package com.kds.sync_blocking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {

    private static final int PORT = 8888;
    private static final int BUFFER_SZ = 1024;

    private static class Handler implements Runnable {

        Socket socket;

        Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println(String.format("%s: %s", Thread.currentThread().getName(), "Handler task submitted"));
            InputStream in = null;
            try {
                in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                int read = 0;
                byte[] buf = new byte[BUFFER_SZ];
                while ((read = in.read(buf)) != -1) {
                    out.write(buf, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(String.format("%s: %s", Thread.currentThread().getName(), "Handler task finished"));
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(String.format("%s: %s", Thread.currentThread().getName(), "Server Starting on port : " + PORT));
            ServerSocket server = new ServerSocket(PORT);
            ExecutorService executorService = Executors.newCachedThreadPool();

            while (true) {
                Socket socket = server.accept();
                executorService.submit(new Handler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
