package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.exceptions.UserAlreadyExists;

/*
 * Класс по управлению окном для регистрации нового пользователя.
 */
public class RegistrationFXMLController {
    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatedPasswordField;

    private Controller controller;

    @FXML
    void initialize() {
        controller = Controller.getInstance();
    }

    @FXML
    private void okButtonPushed() {
        String login = loginField.getText();
        String password = passwordField.getText();
        String repeatedPassword = repeatedPasswordField.getText();

        if (login.isEmpty()) {
            controller.showAlertMessage(Alert.AlertType.ERROR, "Введите логин!");
            return;
        }

        if (!password.equals(repeatedPassword)) {
            controller.showAlertMessage(Alert.AlertType.ERROR, "Пароли не совпадают!");
            return;
        }
        try {
            controller.addUser(login, password);
            controller.showAlertMessage(Alert.AlertType.INFORMATION, "Регистрация прошла успешно!");
            controller.closeWindow((Stage) loginField.getScene().getWindow());
        } catch (UserAlreadyExists e) {
            controller.showAlertMessage(Alert.AlertType.ERROR, "Пользователь с таким именем уже существует!");
        }

    }

    @FXML
    private void cancelButtonPushed() {
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.close();
    }


}
