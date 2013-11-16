package de.haw.chat.ui;

import de.haw.chat.application.Manager;
import de.haw.chat.application.Task;
import de.haw.chat.application.TaskAction;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.HashMap;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.ui
 */
public class GuiApplication extends Application implements Runnable {

    private static Controller guiController;

    public static void setGuiController(Controller guiController) {
        GuiApplication.guiController = guiController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(GuiApplication.guiController);
        loader.setLocation(getClass().getResource("chat.fxml"));

        Object root = loader.load();
        if(!(root instanceof Parent)) {
            throw new Exception("Object must be Parent");
        }

        primaryStage.setTitle("HAW RN Chat");
        primaryStage.setScene(new Scene((Parent) root, 600, 600));
        primaryStage.show();

        primaryStage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Manager.getInstance().publishTask(new Task() {
                    @Override
                    public TaskAction getAction() {
                        return TaskAction.QUIT;
                    }

                    @Override
                    public HashMap<String, String> getParameters() {
                        return null;
                    }
                });
            }
        });

        GuiApplication.guiController.askServer();
    }

    @Override
    public void run() {
        launch();
    }
}
