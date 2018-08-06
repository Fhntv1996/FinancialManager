import controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

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
