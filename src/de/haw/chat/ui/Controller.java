package de.haw.chat.ui;

import de.haw.chat.application.Manager;
import de.haw.chat.application.Task;
import de.haw.chat.application.TaskAction;
import de.haw.chat.message.MessageNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import java.util.Date;
import java.util.HashMap;

public class Controller {

    @FXML public Button sendBtn;

    @FXML public GridPane labelContainer;

    @FXML public TextArea inTextarea;

    protected int messageindex = 0;

    protected ChatQuestion currentQuestion;

    public void clickSendBtn() {
        String message = getAndClearUserChatMessage();

        if(currentQuestion != null) {
            currentQuestion.processAnswer(message);
            currentQuestion = null;
        } else {
            addChatMessage(new MessageNode("YOU", message));
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
        printInfo(question.getQuestion());
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
        addChatMessage(new MessageNode("System", "SUCCESS: " + message));
    }

    public void printError(String message) {
        addChatMessage(new MessageNode("System", "ERROR: " + message));
    }

    public void printInfo(String message) {
        addChatMessage(new MessageNode("System", message));
    }

    public void setUsername(String username) {
        printInfo(String.format("Username changed to %s", username));
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
}
