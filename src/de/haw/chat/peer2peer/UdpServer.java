package de.haw.chat.peer2peer;

import de.haw.chat.application.Manager;
import de.haw.chat.application.Task;
import de.haw.chat.application.TaskAction;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.peer2peer
 */
public class UdpServer extends Thread {

    private DatagramSocket socket;

    public UdpServer(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public synchronized void close() {
        socket.close();
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            try {
                final DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                int         len     = packet.getLength();
                byte[]      data    = packet.getData();

                if(!ClientManager.getInstance().contains(address)) {
                    System.err.println("Chatmesage from unknown/unidentified user.");
                    // maybe force user list refresh ?
//                    Manager.getInstance().publishTask(new Task() {
//                        @Override
//                        public TaskAction getAction() {
//                            return TaskAction.FETCHUSER_REQUEST;
//                        }
//
//                        @Override
//                        public HashMap<String, String> getParameters() {
//                            return null;
//                        }
//                    });

                    // or just add it to the list
//                    ClientManager.getInstance().add(address);
                }

                String message = new String(data, 0, len);

                int splitPoint = message.indexOf(':');
                final String username = message.substring(0, splitPoint);
                final String chatMessage = message.substring(splitPoint + 1);

                Manager.getInstance().publishTask(new Task() {
                    @Override
                    public TaskAction getAction() {
                        return TaskAction.RUSER_NEWMESSAGE;
                    }

                    @Override
                    public HashMap<String, String> getParameters() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("username", username);
                        params.put("message", chatMessage);

                        return params;
                    }
                });
            } catch (IOException e) {
                interrupt();
                e.printStackTrace();
            }
        }
    }
}
