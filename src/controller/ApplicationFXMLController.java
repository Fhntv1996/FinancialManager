package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Account;
import model.Record;

import java.util.List;
import java.util.Set;

/*
 * Класс по управлению главным окном приложения.
 */
public class ApplicationFXMLController {
    @FXML
    private Label currentBalanceLabel;

    @FXML
    private Label loginLabel;

    @FXML
    private ListView<Account> accountsListView;

    private ObservableList<Account> accountsList;

    @FXML
    private TableView<Record> recordsTableView;

    private ObservableList<Record> recordsTableData;

    @FXML
    private TableColumn<Record, String> tableDateCol;

    @FXML
    private TableColumn<Record, Double> tableAmountCol;

    @FXML
    private TableColumn<Record, String> tableOperationCol;

    @FXML
    private TableColumn<Record, String> tableCategoryCol;

    @FXML
    private TableColumn<Record, Integer> tableAccountCol;

    @FXML
    private TableColumn<Record, String> tableDescriptionCol;

    private Controller controller;

    @FXML
    void initialize() {
        controller = Controller.getInstance();

        setLoginLabel(controller.getApplicationUser().getLogin());

        initAccountsListView();//Инициализирует список счетов.
        initRecordsTableView();//Инициализирует таблицу записей.

        setAccountsListView();//Заполняет список счетов.
        setRecordsTableView(accountsListView.getFocusModel().getFocusedItem());//Заполняет таблицу записей для выбранного счета.
    }

    /*
     * Метод вызывается при нажатии кнопки "Добавить счет".
     */
    @FXML
    private void newAccountButtonPushed() {
        controller.openNewAccountWindow(getStage());
        addAccountToListView();
    }

    /*
     * Метод вызывается при нажатии кнопки "Добавить запись".
     */
    @FXML
    private void newRecordButtonPushed() {
        if (!accountsList.isEmpty()) {
            controller.openNewRecordWindow(getStage(), accountsListView);
            Account currentAccount = accountsListView.getSelectionModel().getSelectedItem();
            setRecordsTableView(currentAccount);
            setCurrentBalanceLabel(currentAccount.getBalance());
        }
    }

    /*
     * Метод вызывается при нажатии кнопки "Удалить запись".
     */
    @FXML
    private void deleteRecordButtonPushed() {
        if (!recordsTableData.isEmpty()) {
            Account accountToDeleteFrom = accountsListView.getSelectionModel().getSelectedItem();
            Record recordToDelete = recordsTableView.getSelectionModel().getSelectedItem();
            if (recordToDelete == null) {
                return;
            }

            recordsTableData.remove(recordToDelete);
            controller.removeRecord(accountToDeleteFrom, recordToDelete);
            setCurrentBalanceLabel(accountToDeleteFrom.getBalance());
        }
    }

    /*
     * Метод вызывается при нажатии кнопки "Удалить все записи".
     */
    @FXML
    private void deleteAllRecordsButtonPushed() {
        if (recordsTableData.isEmpty()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Вы уверены, что хотите удалить все записи из данного счета?");
        alert.setHeaderText(null);
        ButtonType okButton = new ButtonType("Да", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);

        alert.showAndWait().ifPresent(type -> {
            if (type == okButton) {
                Account accountToDeleteFrom = accountsListView.getSelectionModel().getSelectedItem();
                recordsTableData.clear();
                controller.removeAllRecordsFromAccount(accountToDeleteFrom);
                setCurrentBalanceLabel(accountToDeleteFrom.getBalance());
            }
        });
    }

    /*
     * Метод вызывается при выборе пункта меню "Сменить пользователя".
     */
    @FXML
    private void changeUserMenuItemPushed() {
        controller.openLoginWindow(getStage());
    }

    /*
     * Метод вызывается при выборе пункта меню "Изменить пароль".
     */
    @FXML
    private void changePasswordMenuItemPushed() {
        controller.openChangePasswordWindow(getStage());
    }

    /*
     * Метод вызывается при выборе пункта меню "О программе".
     */
    @FXML
    private void aboutProgramMenuItemPushed() { controller.openAboutProgramFile(); }

    private void setLoginLabel(String login) {
        loginLabel.setText(login);
    }

    /*
     * Метод инициализирует таблицу записей.
     */
    private void initRecordsTableView() {
        recordsTableData = FXCollections.observableArrayList();
        recordsTableView.setItems(recordsTableData);

        tableDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        tableOperationCol.setCellValueFactory(new PropertyValueFactory<>("operation"));
        tableCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        tableAccountCol.setCellValueFactory(new PropertyValueFactory<>("accountID"));
        tableDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        //Создаем всплывающую подсказку.
        class ToolTipCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell <S, T>> {
            @Override
            public TableCell<S, T> call(TableColumn<S, T> param) {
                return new TableCell<S, T>(){
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item==null || item.toString().equals("")){//Проверяем, пуста ли ячейка.
                            setTooltip(null);
                            setText(null);
                        }else {
                            setTooltip(new Tooltip(item.toString()));
                            setText(item.toString());
                        }
                    }
                };
            }
        }

        tableDescriptionCol.setCellFactory(new ToolTipCellFactory<>());//Устанавливаем всплывающюю подсказку на поле description.
        tableAccountCol.setCellFactory(new ToolTipCellFactory<>());//Устанавливаем всплывающюю подсказку на поле account.
    }

    /*
     * Метод заполняет таблицу записей.
     */
    private void setRecordsTableView(Account account) {
        recordsTableData.clear();

        if (account == null) {
            return;
        }

        List<Record> records = account.getRecords();
        if (records != null && !records.isEmpty()) {
            recordsTableData.addAll(records);
        }
    }

    /*
     * Метод инициализирует список счетов.
     */
    private void initAccountsListView() {
        accountsListView.setCellFactory(lv -> new ListCell<Account>() {
            @Override
            protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? null : item.getDescription());
            }
        });

        ContextMenu contextMenu = new ContextMenu();

        /*
         * Метод создает контекстное меню при нажатии правой кнопки мыши на счете.
         * Сначала проверяет, была ли кнопка нажата именно на счете, а не на пустой клетке.
         * Затем добавляет два пункта меню: "Удалить счет" и "Показать счет".
         */
        accountsListView.setOnContextMenuRequested(menuEvent -> {
            EventTarget target = menuEvent.getTarget();
            if (!(target instanceof ListCell || target instanceof Text)) {
                return;
            }
            try {
                ListCell cell = (ListCell)target;
                if (cell.isEmpty()) {
                    return;
                }
            } catch (ClassCastException e) {
                Text cell = (Text)target;
                if (cell.equals(new Text(""))) {
                    return;
                }
            }

            Account currentAccount = accountsListView.getSelectionModel().getSelectedItem();

            MenuItem removeAccount = new MenuItem("Удалить счет");
            removeAccount.setOnAction(actionEvent -> {
                accountsList.remove(currentAccount);
                controller.removeAccount(currentAccount);
                accountsListView.getSelectionModel().selectLast();
                setRecordsTableView(accountsListView.getFocusModel().getFocusedItem());
            });

            MenuItem showAccount = new MenuItem("Показать счет");
            showAccount.setOnAction(event -> controller.openAccountInfoWindow(getStage(), currentAccount));
            contextMenu.getItems().clear();
            contextMenu.getItems().addAll(removeAccount, showAccount);
            contextMenu.show(accountsListView, menuEvent.getScreenX(), menuEvent.getScreenY());
        });

        //Скрываем контекстное меню при нажатии левой кнопки мыши.
        accountsListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && contextMenu.isShowing()) {
                contextMenu.hide();
            }
        });

        //При выборе счета из списка устанавливаем соответствующую таблицу записей и текущий баланс.
        accountsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldAccount, newAccount) -> {
            if (newAccount != null) {
                setRecordsTableView(newAccount);
                setCurrentBalanceLabel(newAccount.getBalance());
            } else {
                setCurrentBalanceLabel(0.0);
            }
        });

        accountsList = FXCollections.observableArrayList();
        accountsListView.setItems(accountsList);
    }

    /*
     * Метод очищает список счетов и заполняет его счетами пользователя.
     */
    private void setAccountsListView() {
        accountsList.clear();

        Set<Account> accounts = controller.getApplicationUser().getAccounts();
        if (accounts != null && !accounts.isEmpty()) {
            accountsList.addAll(accounts);
        }

        accountsListView.getSelectionModel().selectFirst();
    }

    /*
     * Метод добавляет новый счет в список счетов.
     */
    private void addAccountToListView() {
        for (Account account : controller.getApplicationUser().getAccounts()) {
            if (!accountsList.contains(account)) {
                accountsList.add(account);
                accountsListView.getSelectionModel().select(account);
                return;
            }
        }
    }


    private void setCurrentBalanceLabel(Double currentBalance) {
        currentBalanceLabel.setText(Double.toString(currentBalance));
    }

    /*
     * Метод возвращает ссылку на основное окно приложения.
     */
    private Stage getStage() {
        return (Stage) loginLabel.getScene().getWindow();
    }
}
