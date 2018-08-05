package view;

import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage){
        Controller controller = Controller.getInstance();
        controller.openLoginWindow(stage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
