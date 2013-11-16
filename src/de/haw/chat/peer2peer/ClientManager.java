package de.haw.chat.peer2peer;

import de.haw.chat.message.MessageNode;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.peer2peer
 */
public class ClientManager {

    private static ClientManager instance;

    private Set<InetAddress> clients;

    private ClientManager() {
        clients = new HashSet<InetAddress>();
    }

    public static ClientManager getInstance() {
        if(instance == null) {
            instance = new ClientManager();
        }

        return instance;
    }

    public void broadcast(String data) {
        for(InetAddress inetAddress : clients) {
            byte[] bytes = data.getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetAddress, P2PManager.UDPSERVER_PORT);

            UdpSender.getInstance().send(packet);
        }
    }

    public void add(InetAddress address) {
        clients.add(address);
    }

    public void remove(InetAddress address) {
        clients.remove(address);
    }

    public void clear() {
        clients.clear();
    }

    public boolean contains(InetAddress address) {
        return clients.contains(address);
    }
}
