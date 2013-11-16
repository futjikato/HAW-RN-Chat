package de.haw.chat.peer2peer;

import de.haw.chat.application.ActionListener;
import de.haw.chat.application.TaskAction;
import de.haw.chat.application.TaskWorker;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.peer2peer
 */
public class P2PManager extends TaskWorker {

    public static final int UDPSERVER_PORT = 50001;

    private UdpServer server;

    public P2PManager() {
        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.FETCHUSER_PROCESS;
            }

            @Override
            protected void onAction(HashMap<String, String> params) {
                if(params != null) {
                    Set<String> keys = params.keySet();
                    ClientManager clientManager = ClientManager.getInstance();
                    clientManager.clear();

                    for(String key : keys) {
                        try {
                            InetAddress address = InetAddress.getByName(key);
                            clientManager.add(address);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.CUSER_NEWMESSAGE;
            }

            @Override
            protected void onAction(HashMap<String, String> params) {
                if(params != null) {
                    if(params.containsKey("username") && params.containsKey("message")) {
                        String message = String.format("%s: %s\n", params.containsKey("username"), params.containsKey("message"));
                        ClientManager.getInstance().broadcast(message);
                    }
                }
            }
        });

        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.QUIT;
            }

            @Override
            protected void onAction(HashMap<String, String> params) {
                // stop udp server
                if(server != null) {
                    server.interrupt();
                    server.close();
                }
                // stop this manager
                interrupt();
            }
        });
    }

    @Override
    protected void initialize() {
        server = new UdpServer(UDPSERVER_PORT);
        server.start();
    }
}
