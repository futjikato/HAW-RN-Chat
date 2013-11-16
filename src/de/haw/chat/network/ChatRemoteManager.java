package de.haw.chat.network;

import de.haw.chat.Main;
import de.haw.chat.application.*;

import java.util.HashMap;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.network
 */
public final class ChatRemoteManager extends TaskWorker {

    protected static ChatRemoteManager instance;

    protected ServerConnection connection;

    private ChatRemoteManager() {

        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.QUIT;
            }

            @Override
            protected void onAction(HashMap<String, String> params) {
                if(connection != null) {
                    connection.close();
                }

                interrupt();
            }
        });

        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.CONNECT_REQUEST;
            }

            @Override
            protected void onAction(HashMap<String, String> params) {
                if(params != null && params.containsKey("host")) {
                    try {
                        connection = new ServerConnection(params.get("host"), Main.TCP_SERVERPORT);

                        Manager.getInstance().publishTask(new Task() {
                            @Override
                            public TaskAction getAction() {
                                return TaskAction.CONNECT_SUCCESS;
                            }

                            @Override
                            public HashMap<String, String> getParameters() {
                                return null;
                            }
                        });
                    } catch (NetworkException e) {
                        e.printStackTrace();

                        Manager.getInstance().publishTask(new Task() {
                            @Override
                            public TaskAction getAction() {
                                return TaskAction.CONNECT_FAILED;
                            }

                            @Override
                            public HashMap<String, String> getParameters() {
                                return null;
                            }
                        });
                    }
                }
            }
        });

        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.CUSER_REQUESTNAMECHANGE;
            }

            @Override
            protected void onAction(final HashMap<String, String> params) {
                if(params != null && params.containsKey("username")) {
                    // try to auth at server with given username
                    if(connection.auth(params.get("username"))) {

                        // on success publish CUSER_NAMECHANGE
                        Manager.getInstance().publishTask(new Task() {
                            @Override
                            public TaskAction getAction() {
                                return TaskAction.CUSER_NAMECHANGE;
                            }

                            @Override
                            public HashMap<String, String> getParameters() {
                                return params;
                            }
                        });
                    } else {

                        // on error send CUSER_REQUESTNAMECHANGEFAILED task
                        Manager.getInstance().publishTask(new Task() {
                            @Override
                            public TaskAction getAction() {
                                return TaskAction.CUSER_REQUESTNAMECHANGEFAILED;
                            }

                            @Override
                            public HashMap<String, String> getParameters() {
                                return params;
                            }
                        });
                    }
                }
            }
        });

        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.FETCHUSER_REQUEST;
            }

            @Override
            protected void onAction(HashMap<String, String> params) {
                try {
                    String response = connection.requestUserList();
                    final HashMap<String, String> userList = InfoParser.parseInfoResponse(response);

                    Manager.getInstance().publishTask(new Task() {
                        @Override
                        public TaskAction getAction() {
                            return TaskAction.FETCHUSER_PROCESS;
                        }

                        @Override
                        public HashMap<String, String> getParameters() {
                            return userList;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static ChatRemoteManager getInstance() {
        if(instance == null) {
            instance = new ChatRemoteManager();
        }

        return instance;
    }
}
