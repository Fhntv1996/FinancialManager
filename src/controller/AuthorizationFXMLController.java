package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

/*
 * Класс по управлению окном для авторизации пользователя.
 */
public class AuthorizationFXMLController {
    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    private Controller controller;

    @FXML
    void initialize() {
        controller = Controller.getInstance();
    }

    @FXML
    private void loginButtonPushed() {
        String login = loginField.getText();
        String password = passwordField.getText();

        boolean inputIsCorrect = controller.checkLoginAndPassword(login, password);

        if (inputIsCorrect) {
            controller.closeWindow((Stage) loginField.getScene().getWindow());
            controller.setApplicationUser(login);
            controller.openMainApplicationWindow();
        } else {
            controller.showAlertMessage(Alert.AlertType.ERROR, "Неверный логин и пароль");
        }
    }

    @FXML
    private void registrationButtonPushed() {
        controller.openRegistrationWindow(getStage());
    }

    /*
     * Возвращает ссылку на окно авторизации.
     */
    private Stage getStage() {
        return (Stage) loginField.getScene().getWindow();
    }
}
