package de.haw.chat.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.network
 */
public class ServerConnection {

    private Socket socket;

    private BufferedReader reader;

    private DataOutputStream writer;

    private boolean authed = false;

    public ServerConnection(String host, int port) throws NetworkException {
        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(host, port), 2000);
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.writer = new DataOutputStream(this.socket.getOutputStream());
        } catch (SocketException e) {
            throw new NetworkException(e);
        } catch (UnknownHostException e) {
            throw new NetworkException(e);
        } catch (IOException e) {
            throw new NetworkException(e);
        }
    }

    /**
     * Tries to auth with the given username.
     * Returns true on success, false otherwise.
     *
     * If the client has already authed with a valid username this method will always
     * return false and not auth again.
     *
     * @param username
     * @return success
     */
    public boolean auth(String username) {
        if(authed) {
            return false;
        }

        send(ClientRequest.NEW, new String[] {username});

        String response = read();

        return response.toUpperCase().equals("OK");
    }

    /**
     * Notify server about ending the session
     */
    public void close() {
        send(ClientRequest.BYE, new String[]{});
        String response = read();
        if(!response.toUpperCase().equals("BYE")) {
            log("Invalid server response for BYE.");
        }
    }

    public String requestUserList() {
        send(ClientRequest.INFO, new String[] {});
        return read();
    }

    private void send(ClientRequest request, String[] params) {
        String message = request.getCommand();
        for(String param : params) {
            message += " " + param;
        }

        log("WRITE: " + message);

        // append newline at the end if not already there
        if(!message.substring(message.length() - 1, message.length()).equals('\n')) {
            message = String.format("%s\n", message);
        }

        try {
            writer.writeBytes(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String read() {
        String response;
        try {
            response =  this.reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            response = String.format("ERROR %s", e.getMessage());
        }

        log("READ: " + response);

        return response;
    }

    private void log(String message) {
        System.out.println(message);
    }
}
