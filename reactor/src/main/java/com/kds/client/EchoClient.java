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
    private static final int PORT = 8888;
    int numT = 1;
    ExecutorService executorService = Executors.newFixedThreadPool(numT);

    public static void main(String [] args) throws InterruptedException {

        EchoClient client = new EchoClient();
        client.runClient();
        //client.runSingleClinet();
    }

    public void runClient() {
        while (numT > 0 ) {
            String id = "ClientId : " + numT;
            executorService.submit(new Worker(id));
            numT--;
        }
        System.out.println("runClient: finished");
    }

    public void runSingleClinet() {
        executorService.submit(new SingleWorker());
    }

    public class SingleWorker implements Runnable {

        @Override
        public void run() {

            Socket clientSocket = null;
            try  {
                clientSocket = new Socket(HOST, PORT);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                String message  = "Hello :" + 1 + "\n";
                outToServer.writeBytes(message);
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String modifiedSentence = inFromServer.readLine();
                System.out.println("Response from server: " + modifiedSentence);

                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class Worker implements Runnable {

        private String clientId;

        public Worker(String clinetId) {
            this.clientId = clinetId;
        }

        public void run() {
            for (int i = 0; i < 20; i++) {


                Socket clientSocket = null;
                try  {
                    clientSocket = new Socket(HOST, PORT);
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    String message  = "Hello :" + i +" from "+ clientId + "\n";
                    outToServer.writeBytes(message);
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String modifiedSentence = inFromServer.readLine();
                    System.out.println("Response from server: " + modifiedSentence);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
