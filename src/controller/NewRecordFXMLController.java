package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Account;
import model.Record;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/*
 * Класс по управлению окном для создания новой записи.
 */
public class NewRecordFXMLController {
    @FXML
    private Label loginLabel;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField amountField;

    @FXML
    private ChoiceBox<Account> accountChoiceBox;

    @FXML
    private ChoiceBox<String> operationTypeChoiceBox;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextArea description;

    private Controller controller;

    private String pattern = "dd.MM.yyyy";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

    @FXML
    void initialize() {
        controller = Controller.getInstance();

        setLoginLabel(controller.getApplicationUser().getLogin());
        initDatePicker();//Инициализирует поле выбора даты.
        initAccountChoiceBox();//Инициализирует поле выбора аккаунта.
        setOperationTypeChoiceBox();//Заполняет поле выбора операции.
    }

    @FXML
    void cancelButtonPushed() {
        controller.closeWindow((Stage) loginLabel.getScene().getWindow());
    }

    @FXML
    private void okButtonPushed() {
        if (!allRequiredFieldsFilledCorrectly()) {
            return;
        }

        controller.addRecord(datePicker.getValue().format(dateFormatter), amountField.getText(), accountChoiceBox.getValue().getAccountID(),
                operationTypeChoiceBox.getValue(), categoryComboBox.getValue(), description.getText());
        controller.closeWindow((Stage) loginLabel.getScene().getWindow());
    }

    /*
     * Инициализирует поле выбора аккаунта.
     */
    private void initAccountChoiceBox() {
        accountChoiceBox.setConverter(new StringConverter<Account>() {
            @Override
            public String toString(Account account) {
                return account.getDescription();
            }

            @Override
            public Account fromString(String string) {
                return null;
            }
        });
    }

    /*
     * Метод инициализирует поле выбора даты в соответствии с dateFormatter.
     * Устанавливает текущую дату.
     */
    private void initDatePicker() {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return date.format(dateFormatter);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        datePicker.setValue(LocalDate.now());
    }

    private void setLoginLabel(String login) {
        loginLabel.setText(login);
    }

    /*
     * Метод добавляет в поле выбора счета счета пользователя.
     * По умолчанию устанавливает текущий счет.
     */
    void setAccountChoiceBox(Account focusedItem, ObservableList<Account> accounts) {
        accountChoiceBox.setItems(accounts);
        accountChoiceBox.setValue(focusedItem);
    }

    /*
     * Метод добавляет в поле выбора категории все категории, ранее добавленные данным пользователем.
     */
    void setCategoryComboBox(Set<String> categories) {
        if (categories != null) {
            categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        }
    }

    /*
     * Метод добавляет в поле выбора операции 2 возможных варианта: снятие и начисление.
     * По умолчанию устанавливает снятие.
     */
    private void setOperationTypeChoiceBox() {
        operationTypeChoiceBox.setItems(FXCollections.observableArrayList(Record.getWithdrawalOperation(), Record.getDepositOperation()));
        operationTypeChoiceBox.setValue(Record.getWithdrawalOperation());
    }

    /*
     * Метод проверяет на корректное заполнение поля суммы.
     */
    private boolean allRequiredFieldsFilledCorrectly() {
        if (!(amountField.getText().matches("\\d+") || amountField.getText().matches("\\d+[.,]\\d\\d"))) {
            controller.showAlertMessage(Alert.AlertType.ERROR, "Введите корректную сумму");
            return false;
        }
        return true;
    }


}
