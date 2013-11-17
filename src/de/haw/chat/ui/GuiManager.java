package de.haw.chat.ui;

import de.haw.chat.application.ActionListener;
import de.haw.chat.application.Manager;
import de.haw.chat.application.TaskAction;
import de.haw.chat.application.TaskWorker;
import de.haw.chat.message.MessageNode;
import javafx.application.Platform;

import java.util.HashMap;


/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.ui
 */
public class GuiManager extends TaskWorker {

    private static GuiManager instance;

    private Controller guiController;

    private GuiApplication guiApplication;

    private GuiManager() {

        // create gui controller
        guiController = new Controller();

        // on username change event set new username in gui
        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.CUSER_NAMECHANGE;
            }

            @Override
            protected void onAction(final HashMap<String, String> params) {
                if(params != null && params.containsKey("username")) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            guiController.setUsername(params.get("username"));
                        }
                    });
                }
            }
        });

        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.CUSER_REQUESTNAMECHANGEFAILED;
            }

            @Override
            protected void onAction(HashMap<String, String> params) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        guiController.printError("Name is already in use. Please try again.");
                        guiController.askUsername();
                    }
                });
            }
        });

        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.CONNECT_SUCCESS;
            }

            @Override
            protected void onAction(HashMap<String, String> params) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        guiController.printSuccess("Connected to server.");
                        guiController.askUsername();
                    }
                });
            }
        });

        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.CONNECT_FAILED;
            }

            @Override
            protected void onAction(HashMap<String, String> params) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        guiController.printError("Connection to server failed. Please try again.");
                        guiController.askServer();
                    }
                });
            }
        });

        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.RUSER_NEWMESSAGE;
            }

            @Override
            protected void onAction(HashMap<String, String> params) {
                if(params != null) {
                    if(params.containsKey("username") && params.containsKey("message")) {
                        // create new chat node
                        final MessageNode node = new MessageNode(params.get("username"), params.get("message"));
                        node.setRemoteMessage();

                        // add within java fx thread
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                guiController.addChatMessage(node);
                            }
                        });
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
                interrupt();
            }
        });
    }

    public static GuiManager getInstance() {
        if(instance == null) {
            instance = new GuiManager();
        }

        return instance;
    }

    @Override
    protected void initialize() {
        guiApplication = new GuiApplication();
        GuiApplication.setGuiController(guiController);

        Thread thread = new Thread(guiApplication);
        thread.start();
    }
}
