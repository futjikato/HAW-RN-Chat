package de.haw.chat.peer2peer;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.*;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.peer2peer
 */
public class ClientManager {

    private static ClientManager instance;

    private HashMap<InetAddress, String> clients;

    private ClientManager() {
        clients = new HashMap<InetAddress, String>();
    }

    public static ClientManager getInstance() {
        if(instance == null) {
            instance = new ClientManager();
        }

        return instance;
    }

    public void broadcast(String data) {
        Set<InetAddress> addressSet = clients.keySet();
        for(InetAddress inetAddress : addressSet) {
            byte[] bytes = data.getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetAddress, P2PManager.UDPSERVER_PORT);

            UdpSender.getInstance().send(packet);
        }
    }

    public void add(InetAddress address, String username) {
        clients.put(address, username);
    }

    public void remove(InetAddress address) {
        clients.remove(address);
    }

    public Collection<InetAddress> getAddressList() {
        Collection<InetAddress> copy = new ArrayList<InetAddress>();
        copy.addAll(clients.keySet());

        return copy;
    }

    public boolean contains(InetAddress address) {
        return clients.containsKey(address);
    }

    public String getUsernameByAddress(InetAddress address) {
        return clients.get(address);
    }
}
