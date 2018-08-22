package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.exceptions.AccountAlreadyExists;

/*
 * Класс по управлению окном для создания нового счета.
 */
public class NewAccountFXMLController {
    @FXML
    private Label loginLabel;

    @FXML
    private TextField accountIDField;

    @FXML
    private TextField amountField;

    @FXML
    private TextField descriptionField;

    private Controller controller;

    @FXML void initialize() {
        controller = Controller.getInstance();
        loginLabel.setText(controller.getApplicationUser().getLogin());
    }

    @FXML
    void cancelButtonPushed() {
        controller.closeWindow((Stage)loginLabel.getScene().getWindow());
    }


    @FXML
    private void okButtonPushed() {
        try {
            if (!allRequiredFieldsFilledCorrectly()) {
                return;
            }
            controller.addAccount(accountIDField.getText(), amountField.getText(),
                    descriptionField.getText());
            controller.closeWindow((Stage) loginLabel.getScene().getWindow());
        } catch (AccountAlreadyExists e) {
            controller.showAlertMessage(Alert.AlertType.ERROR, "Счет с таким номером уже существует.");
        }

    }

    private boolean allRequiredFieldsFilledCorrectly() {
        if (!accountIDField.getText().matches("\\d+")) {
            controller.showAlertMessage(Alert.AlertType.ERROR, "Введите корректный номер счета");
            return false;
        }
        if (!(amountField.getText().matches("\\d+") || amountField.getText().matches("\\d+[.,]\\d\\d"))) {
            controller.showAlertMessage(Alert.AlertType.ERROR, "Введите корректную сумму");
            return false;
        }
        if (descriptionField.getText().isEmpty()) {
            controller.showAlertMessage(Alert.AlertType.ERROR, "Введите название счета");
            return false;
        }
        return true;
    }


}
