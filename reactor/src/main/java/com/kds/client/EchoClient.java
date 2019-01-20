package com.kds.client;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoClient {

    final static Logger logger = Logger.getLogger(EchoClient.class);

    private static final String HOST = "localhost";
    private static final int PORT = 7070;
    int numT = 20;
    ExecutorService executorService = Executors.newFixedThreadPool(numT);

    public static void main(String [] args) throws InterruptedException {

        EchoClient client = new EchoClient();
        client.runClient();
    }

    public void runClient() {
        while (numT > 0 ) {
            executorService.submit(new Worker());
            numT--;
        }
        System.out.println("runClient: finished");
    }

    public class Worker implements Runnable {

        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try (Socket clientSocket = new Socket(HOST, PORT)) {

                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.writeBytes("Hello : " + i + '\n');
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String modifiedSentence = inFromServer.readLine();
                    System.out.println("Response from server: " + modifiedSentence);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
