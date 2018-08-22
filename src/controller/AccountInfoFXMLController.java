package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/*
 * Класс по управлению окном для показа подробной информации о счете.
 */
public class AccountInfoFXMLController {
    @FXML
    private Label loginLabel;

    @FXML
    private Label creationDateLabel;

    @FXML
    private Label accountIDLabel;

    @FXML
    private Label startAmountLabel;

    @FXML
    private Label descriptionLabel;

    private Controller controller;

    @FXML
    void initialize() {
        controller = Controller.getInstance();
    }

    @FXML
    void closeButtonPushed() {
        controller.closeWindow((Stage)loginLabel.getScene().getWindow());
    }

    void setData(String creationDate, String accountID, String startAmount, String description) {
        loginLabel.setText(controller.getApplicationUser().getLogin());
        creationDateLabel.setText(creationDate);
        accountIDLabel.setText(accountID);
        startAmountLabel.setText(startAmount);
        descriptionLabel.setText(description);
    }
}
