package de.haw.chatserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chatserver
 */
public final class Worker implements Runnable {

    private BufferedReader reader;

    private DataOutputStream writer;

    private Socket socket;

    private InetAddress address;

    private boolean mapped = false;

    public Worker(Socket socket) throws IOException {
        this.socket = socket;

        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.writer = new DataOutputStream(this.socket.getOutputStream());
    }

    @Override
    public void run() {
        address = socket.getInetAddress();

        while(!Thread.currentThread().isInterrupted()) {
            String command = readLine();

            if(command.toUpperCase().equals("BYE")) {
                sendLine("BYE");
                try {
                    UserManager.getInstance().remove(socket.getInetAddress());
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.currentThread().interrupt();
            }

            if(command.equals("INFO")) {
                if(!mapped) {
                    sendLine("ERROR You need to authenticate yourself first.");
                } else {
                    sendLine(UserManager.getInstance().getUserListAsString());
                }
            }

            if(command.length() > 5 && command.substring(0, 3).equals("NEW")) {
                String username = command.substring(4);
                if(username.contains(" ")) {
                    sendLine("ERROR Username cannot contain any whitespaces.");
                } else {
                    try {
                        UserManager.getInstance().add(address, username);
                        sendLine("OK");
                    } catch (Exception e) {
                        sendLine("ERROR " + e.getMessage());
                    }
                }
            }
        }
    }

    private String readLine() {
        try {
            String line = reader.readLine();
            System.out.println("[" + address.toString() + "] " + line);
            return line;
        } catch (IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return "";
        }
    }

    private void sendLine(String message) {
        if(message.length() < 1)
            return;

        if(!message.substring(message.length() - 1, message.length()).equals('\n')) {
            message = String.format("%s\n", message);
        }

        System.out.print("[" + address.toString() + "] " + message);

        try {
            writer.writeBytes(message);
        } catch (IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
