package de.haw.chatserver;

import java.io.IOException;

public class Main {

    private static final int SERVERPORT = 50000;

    public static void main(String[] args) {
        try {
            Server server = new Server(SERVERPORT);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
