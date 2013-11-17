package de.haw.chat.message;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.message
 */
public class MessageNode {

    @FXML public Label labelUsername;

    @FXML public Label labelDatetime;

    @FXML public Label labelMessage;

    protected BorderPane root;

    public MessageNode(String username, String message, Date messageTime) {
        loadFXML();
        setData(username, messageTime, message);
    }

    public MessageNode(String username, String message) {
        this(username, message, new Date());
    }

    private void setData(String username, Date messageTime, String message) {
        labelUsername.setText(username);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        labelDatetime.setText(sdf.format(messageTime));

        labelMessage.setText(message);
    }

    public void setSystemMessage() {
        root.getStyleClass().add("systemmessage");
    }

    public void setSelfMessage() {
        root.getStyleClass().add("selfmessage");
    }

    public void setRemoteMessage() {
        root.getStyleClass().add("remotemessage");
    }

    public BorderPane getRoot() {
        return root;
    }

    private void loadFXML() {
        URL fxml = getClass().getResource("MessageNode.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setController(this);
        fxmlLoader.setLocation(fxml);

        try {
            Object tmp  = fxmlLoader.load();
            if(!(tmp instanceof BorderPane)) {
                throw new RuntimeException("Invalid MessageNode base class.");
            }
            root = (BorderPane) tmp;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
