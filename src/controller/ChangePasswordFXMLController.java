package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*
 * Класс по управлению окном для изменения пароля.
 */
public class ChangePasswordFXMLController {
    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField repeatedPasswordField;

    private Controller controller;

    @FXML
    void initialize() {
        controller = Controller.getInstance();
    }

    @FXML
    private void okButtonPushed() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String repeatedPassword = repeatedPasswordField.getText();

        String login = controller.getApplicationUser().getLogin();
        boolean inputIsCorrect = controller.checkLoginAndPassword(login, oldPassword);

        if (inputIsCorrect) {
            if (!newPassword.equals(repeatedPassword)) {
                controller.showAlertMessage(Alert.AlertType.ERROR, "Пароли не совпадают!");
                return;
            }
            controller.changePassword(login, newPassword);
            controller.showAlertMessage(Alert.AlertType.INFORMATION, "Пароль успешно изменен");
            controller.closeWindow((Stage) oldPasswordField.getScene().getWindow());
        }
    }

    @FXML
    private void cancelButtonPushed() {
        Stage stage = (Stage) oldPasswordField.getScene().getWindow();
        stage.close();
    }
}
