package de.haw.chat.peer2peer;

import de.haw.chat.application.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
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

                    Collection<InetAddress> removals = clientManager.getAddressList();

                    for(String key : keys) {
                        try {
                            InetAddress address = InetAddress.getByName(key);
                            if(clientManager.contains(address)) {
                                removals.remove(address);
                            } else {
                                clientManager.add(address, params.get(key));
                                P2PManager.this.publishNewRemoteUserTask(params.get(key));
                            }
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }

                    for(InetAddress address : removals) {
                        P2PManager.this.publishLeftRemoteUserTask(clientManager.getUsernameByAddress(address));
                        clientManager.remove(address);
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
                        String message = String.format("%s: %s\n", params.get("username"), params.get("message"));
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

    private void publishNewRemoteUserTask(final String username) {
        Manager.getInstance().publishTask(new Task() {
            @Override
            public TaskAction getAction() {
                return TaskAction.RUSER_ENTERED;
            }

            @Override
            public HashMap<String, String> getParameters() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("username", username);

                return params;
            }
        });
    }

    private void publishLeftRemoteUserTask(final String username) {
        Manager.getInstance().publishTask(new Task() {
            @Override
            public TaskAction getAction() {
                return TaskAction.RUSER_LEFT;
            }

            @Override
            public HashMap<String, String> getParameters() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("username", username);

                return params;
            }
        });
    }

    @Override
    protected void initialize() {
        server = new UdpServer(UDPSERVER_PORT);
        server.start();
    }
}
