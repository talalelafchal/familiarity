package com.company;

import com.neovisionaries.ws.client.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Main extends Thread {

    private WebSocket webSocket;

    public static void main(String[] args) {
        new Main().start();
    }

    @Override
    public void run() {
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    private void l(String s) {
        System.out.println(s);
    }

    // Add listeners to the websocket.
    WebSocketAdapter websocketAdapter = new WebSocketAdapter() {
        @Override
        public void onTextMessage(WebSocket websocket, String message) throws Exception {
            // Received a text message.
            l("String message from server: " + message);
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            l("Websocket connected");

            // Send hello to the server in binary
            byte[] b = "Hello".getBytes();
            websocket.sendBinary(b);
            
            // Send hello to the server as a string
            websocket.sendText("Hello");
        }

        @Override
        public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
            super.onBinaryMessage(websocket, binary);
            l("Binary Message from server: " + new String(binary));
        }

        @Override
        public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            super.onPingFrame(websocket, frame);
            l("Got a ping from server");
        }

        @Override
        public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            super.onPongFrame(websocket, frame);
            l("Got pong from server");
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            super.onError(websocket, cause);
            cause.printStackTrace();
        }
    };

    // Start the connection. Should be called in a new thread
    public void connect() throws IOException, WebSocketException {
        WebSocketFactory factory = new WebSocketFactory();
        webSocket = factory.createSocket(generatePrimusUrl());
        webSocket.addListener(websocketAdapter);
        webSocket.setPingPayloadGenerator(new PayloadGenerator() {
            @Override
            public byte[] generate() {
                return ("primus::ping::" + new Date().toString()).getBytes();
            }
        });
        webSocket.setPingInterval(10 * 1000);
        webSocket.connect();
    }

    private String generatePrimusUrl() {
        return "ws://localhost:10001/primus";
    }
}
