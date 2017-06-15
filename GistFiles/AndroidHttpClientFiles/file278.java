package com.thoughtworks;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.lang.String;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class App {


    public static void main(String[] args) throws SocketException {

        Queue<String> queue = new LinkedBlockingQueue<>();
        HttpClient httpclient = HttpClients.createDefault();
        DatagramSocket socket = new DatagramSocket(6889);

        Runnable client = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String json = queue.poll();
                    if (json == null || json.length() <= 0) {
                        try {
                            sleep(1000);
                            continue;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    HttpPost post = new HttpPost("https://android.googleapis.com/gcm/send");
                    post.setHeader("Authorization", "key=AIzaSyCABSTd47XeIH");
                    post.setHeader("Content-Type", "application/json");
                    System.out.println("posting " + json);
                    try {
                        HttpEntity entity = new StringEntity(json);
                        post.setEntity(entity);

                        httpclient.execute(post);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread t = new Thread(client);
        t.start();

        while (true) {
            try {
                byte[] buf = new byte[4096];
                DatagramPacket received = new DatagramPacket(buf, buf.length);
                socket.receive(received);
                String data = new String(received.getData());
                String command = data.split("\\s")[0];

                if ("PING".equals(command)) {
                    byte[] sendData = "PONG".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, received.getAddress(), received.getPort());
                    socket.send(sendPacket);
                } else if ("SEND".equals(command)) {
                    String message = data.replace(command, "").trim();
                    Pattern p = Pattern.compile("([a-zA-Z0-9_-]*) \"([^\"]*)\"");
                    Matcher matcher = p.matcher(message);

                    if (matcher.matches()) {
                        String json = "{\"registration_ids\" : \"" + matcher.group(1) + "\", \"data\" : { \"alert\" : \"" + matcher.group(2) + "\"}}";
                        System.out.println(json);
                        queue.add(json);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}