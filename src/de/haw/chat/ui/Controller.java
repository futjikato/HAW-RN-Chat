package de.haw.chat.ui;

import de.haw.chat.application.Manager;
import de.haw.chat.application.Task;
import de.haw.chat.application.TaskAction;
import de.haw.chat.message.MessageNode;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.HashMap;

public class Controller {

    @FXML public Button sendBtn;

    @FXML public GridPane labelContainer;

    @FXML public TextField inTextarea;

    @FXML public ScrollPane scrollPane;

    @FXML public ListView userList;

    protected int messageindex = 0;

    protected ChatQuestion currentQuestion;

    private String username = "Me";

    public void clickSendBtn() {
        final String message = getAndClearUserChatMessage();

        if(currentQuestion != null) {
            // save reference
            ChatQuestion tmp_question = currentQuestion;
            // set to null now and not after processing because that may set an new question
            currentQuestion = null;
            // now process answer
            tmp_question.processAnswer(message);
        } else {
            // add message
            MessageNode node = new MessageNode(username, message);
            node.setSelfMessage();
            addChatMessage(node);

            // inform via manager
            Manager.getInstance().publishTask(new Task() {
                @Override
                public TaskAction getAction() {
                    return TaskAction.CUSER_NEWMESSAGE;
                }

                @Override
                public HashMap<String, String> getParameters() {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("username", username);
                    params.put("message", message);

                    return params;
                }
            });
        }
    }

    public void addChatMessage(MessageNode newChatMessage) {
        labelContainer.add(newChatMessage.getRoot(), 0, messageindex++);
    }

    protected String getAndClearUserChatMessage() {
        String message = inTextarea.getText();
        inTextarea.setText("");

        return message;
    }

    public void ask(ChatQuestion question) {
        printSystem(question.getQuestion());
        currentQuestion = question;
    }

    public void askUsername() {
        ask(new ChatQuestion() {
            @Override
            public String getQuestion() {
                return "Enter your username";
            }

            @Override
            public void processAnswer(final String userAnswer) {
                Manager.getInstance().publishTask(new Task() {
                    @Override
                    public TaskAction getAction() {
                        return TaskAction.CUSER_REQUESTNAMECHANGE;
                    }

                    @Override
                    public HashMap<String, String> getParameters() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("username", userAnswer);

                        return params;
                    }
                });
            }
        });
    }

    public void printSuccess(String message) {
        printSystem("Success: " + message);
    }

    public void printError(String message) {
        printSystem("Error: " + message);
    }

    public void printSystem(String message) {
        MessageNode node = new MessageNode("System", message);
        node.setSystemMessage();
        addChatMessage(node);
    }

    public void setUsername(String username) {
        this.username = username;
        printSystem(String.format("Username changed to %s", username));
    }

    public void askServer() {
        ask(new ChatQuestion() {
            @Override
            public String getQuestion() {
                return "Enter chat server address";
            }

            @Override
            public void processAnswer(final String userAnswer) {
                Manager.getInstance().publishTask(new Task() {
                    @Override
                    public TaskAction getAction() {
                        return TaskAction.CONNECT_REQUEST;
                    }

                    @Override
                    public HashMap<String, String> getParameters() {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("host", userAnswer);

                        return params;
                    }
                });
            }
        });
    }

    public String getUsername() {
        return username;
    }
}
