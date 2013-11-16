package de.haw.chat.peer2peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.peer2peer
 */
public class UdpSender {

    private static UdpSender instance;

    private DatagramSocket senderSocket;

    private UdpSender() {
        try {
            senderSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static UdpSender getInstance() {
        if(instance == null) {
            instance = new UdpSender();
        }

        return instance;
    }

    public void send(DatagramPacket packet) {
        try {
            senderSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
