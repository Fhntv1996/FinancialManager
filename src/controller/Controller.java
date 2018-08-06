package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Account;
import model.DbHelper;
import model.Record;
import model.User;
import model.exceptions.AccountAlreadyExists;
import model.exceptions.UserAlreadyExists;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;

/*
 * Класс обеспечивает связь графического интерфейса с данными приложения.
 */
public class Controller {

    private DatabaseController dbController;

    private User applicationUser;//При авторизации пользователя все данные этого пользователя из базы данных помещаются в applicationUser.

    private static Controller instance;//Singleton

    private Controller() {
        DbHelper dbHelper = DbHelper.getInstance();
        Connection connection = dbHelper.getConnection();
        dbController = new DatabaseController(connection);
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    /*
     * Открытие окна авторизации
     */
    public void openLoginWindow(Stage parentStage) {
        FXMLLoader loader = getAuthorizationFXMLLoader();

        Stage loginStage = getModalStage(parentStage);
        initStage(loader, loginStage);

        closeWindow(parentStage);
        loginStage.showAndWait();
    }

    /*
     * Открытие окна регистрации
     */
    void openRegistrationWindow(Stage loginStage) {
        FXMLLoader loader = getRegistrationFXMLLoader();

        Stage registrationStage = getModalStage(loginStage);
        initStage(loader, registrationStage);
        registrationStage.show();
    }

    /*
     * Открытие главного окна приложения
     */
    void openMainApplicationWindow() {
        FXMLLoader loader = getApplicationFXMLLoader();
        Stage mainStage = new Stage();
        mainStage.setTitle("Финансовый менеджер");
        mainStage.setResizable(false);

        initStage(loader, mainStage);
        mainStage.show();
    }

    /*
     * Открытие окна добавления нового счета
     */
    void openNewAccountWindow(Stage mainStage) {
        FXMLLoader loader = getNewAccountFXMLLoader();

        Stage accountStage = getModalStage(mainStage);
        initStage(loader, accountStage);
        accountStage.showAndWait();
    }

    /*
     * Открытие окна добавления новой записи
     */
    void openNewRecordWindow(Stage mainStage, ListView<Account> accountsListView) {
        FXMLLoader loader = getNewRecordFXMLLoader();

        Stage recordStage = getModalStage(mainStage);
        initStage(loader, recordStage);

        //Передаем  в окно добавления новой записи список счетов, текущий счет и категории пользователя.
        NewRecordFXMLController fxmlController = loader.getController();
        fxmlController.setAccountChoiceBox(accountsListView.getFocusModel().getFocusedItem(),
                accountsListView.getItems());
        fxmlController.setCategoryComboBox(applicationUser.getCategories());

        recordStage.showAndWait();
    }

    /*
     * Открытие окна с информацией о счете
     */
    void openAccountInfoWindow(Stage mainStage, Account currentAccount) {
        FXMLLoader loader = getAccountInfoFXMLLoader();

        Stage accountInfoStage = getModalStage(mainStage);
        initStage(loader, accountInfoStage);

        //Передаем в окно с информацией о счете всю информацию о выбранном счете.
        AccountInfoFXMLController fxmlController = loader.getController();
        fxmlController.setData(currentAccount.getCreationDate(), currentAccount.getAccountID(), currentAccount.getBalance(),
                currentAccount.getDescription());

        accountInfoStage.showAndWait();
    }

    /*
     * Открытие окна для изменения пароля
     */
    void openChangePasswordWindow(Stage mainStage) {
        FXMLLoader loader = getChangePasswordFXMLLoader();

        Stage changePasswordStage = getModalStage(mainStage);
        initStage(loader, changePasswordStage);

        changePasswordStage.showAndWait();
    }

    /*
     * Открытие окна "О программе".
     */
    void openAboutProgramWindow(Stage mainStage) {
        FXMLLoader loader = getAboutProgramFXMLLoader();

        Stage aboutProgramStage = getModalStage(mainStage);
        initStage(loader, aboutProgramStage);

        Path path = FileSystems.getDefault().getPath("src", "view", "aboutProgramWindow", "О программе.txt");
        try {
            String aboutProgram = new String(Files.readAllBytes(path));

            AboutProgramFXMLController fxmlController = loader.getController();
            fxmlController.setText(aboutProgram);
        } catch (IOException e) {
            showAlertMessage(Alert.AlertType.ERROR, "Файл с описанием программы не найден.");
            return;
        }

        aboutProgramStage.showAndWait();
    }

    /*
     * Метод получает на вход логин пользователя.
     * По нему из базы данных получает хэш пароля, список счетов, для каждого счета - список записей.
     * Все эти данные помещаются в applicationUser.
     */
    void setApplicationUser(String login) {
        User user = dbController.getUser(login);
        user.setAccounts(dbController.getAccounts(user));
        user.setAccountsID(dbController.getAccountsID(user));

        for (Account account : user.getAccounts()) {
            account.setRecords(dbController.getRecords(account));
            for (Record record : account.getRecords()) {
                user.addCategory(record.getCategory());//Собираются категории со всех записей данного пользователя
            }
        }

        applicationUser = user;
    }

    User getApplicationUser() {
        return applicationUser;
    }

    /*
     * Метод проверяет существование пользователя с таким логином и паролем.
     */
    boolean checkLoginAndPassword(String login, String password) {
        String passwordHash = getPasswordHash(password);
        return dbController.authorization(login, passwordHash);
    }

    void addUser(String login, String password) throws UserAlreadyExists {
        String passwordHash = getPasswordHash(password);
        dbController.addUser(new User(login, passwordHash));
    }

    void changePassword(String login, String newPassword) {
        String passwordHash = getPasswordHash(newPassword);
        dbController.changePassword(login, passwordHash);
    }

    /*
     * Метод получает на вход пароль и возвращает хэш пароля(используется алгоритм SHA-256)
     */
    private String getPasswordHash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * При добавлении или удалении счета(или записи) актуальное состояние поддерживается как в базе дынных, так и в applicationUser
     */
    void addAccount(String accountID, String amount, String description) throws AccountAlreadyExists {
        amount = amount.replace(',', '.');//В окне добавления счета можно вводить сумму как с '.', так и с ','.

        Account account = new Account(getApplicationUser().getLogin(), accountID, description, Double.parseDouble(amount));
        dbController.addAccount(getApplicationUser(), account);
        getApplicationUser().addAccount(account);
    }

    /*
     * Метод получает на вход счет.
     * Сначала удаляются все записи из этого счета, затем сам счет.
     */
    void removeAccount(Account account) {
        List<Record> accountRecords = account.getRecords();
        if (accountRecords != null) {
            for (Record record : new LinkedHashSet<>(accountRecords)) {
                removeRecord(account, record);
            }
        }
        applicationUser.removeAccount(account);
        dbController.removeAccount(applicationUser, account);
    }

    void addRecord(String date, String amount, String accountID, String operation, String category, String description) {
        amount = amount.replace(',', '.');//В окне добавления записи можно вводить сумму как с '.', так и с ','.

        Account currentAccount = getApplicationUser().getAccount(accountID);//Получаем счет по его ID
        Record record = new Record(accountID, operation, date, Double.parseDouble(amount), description, category);
        dbController.addRecord(currentAccount, record);
        currentAccount.addRecord(record);
        applicationUser.addCategory(record.getCategory());//Добавляем категорию, если ее еще нет в списке категорий данного пользователя
    }

    void removeRecord(Account account, Record record) {
        dbController.removeRecord(account, record);
        account.removeRecord(record);
    }

    void removeAllRecordsFromAccount(Account account) {
        dbController.removeAllRecordsFromAccount(account);
        account.removeAllRecords();
    }

    void showAlertMessage(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    void closeWindow(Stage stage) {
        stage.close();
    }


    private FXMLLoader getApplicationFXMLLoader() {
        return new FXMLLoader(ApplicationFXMLController.class
                .getResource("/view/applicationWindow/application.fxml"));
    }

    private FXMLLoader getNewAccountFXMLLoader() {
        return new FXMLLoader(NewAccountFXMLController.class
                .getResource("/view/newAccountWindow/newAccount.fxml"));
    }

    private FXMLLoader getNewRecordFXMLLoader() {
        return new FXMLLoader(NewRecordFXMLController.class
                .getResource("/view/newRecordWindow/newRecord.fxml"));
    }

    private FXMLLoader getRegistrationFXMLLoader() {
        return new FXMLLoader(RegistrationFXMLController.class
                .getResource("/view/registrationWindow/registration.fxml"));
    }

    private FXMLLoader getAccountInfoFXMLLoader() {
        return new FXMLLoader(AccountInfoFXMLController.class
                .getResource("/view/accountInfoWindow/accountInfo.fxml"));
    }

    private FXMLLoader getAuthorizationFXMLLoader() {
        return new FXMLLoader(AuthorizationFXMLController.class
                .getResource("/view/authorizationWindow/authorization.fxml"));
    }

    private FXMLLoader getChangePasswordFXMLLoader() {
        return new FXMLLoader(ChangePasswordFXMLController.class
                .getResource("/view/changePasswordWindow/changePassword.fxml"));
    }

    private FXMLLoader getAboutProgramFXMLLoader() {
        return new FXMLLoader(AboutProgramFXMLController.class
                .getResource("/view/aboutProgramWindow/aboutProgram.fxml"));
    }

    /*
     * Метод получает на вход ссылку на родительское окно.
     * Возвращает новое окно.
     * Родительское окно дожидается закрытия этого окна перед продолжением работы.
     */
    private Stage getModalStage(Stage parentStage) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        stage.setTitle("Финансовый менеджер");
        stage.setResizable(false);
        return stage;
    }

    /*
     * Метод получает на вход загрузчик FXML файла и ссылку на окно.
     * Метод FXMLLoader.load() возвращает объект типа Parent, который мы можем передать в конструктор объекта Scene,
     * и таким образом, наше окно получит интерфейс.
     */
    private void initStage(FXMLLoader loader, Stage stage) {
        try {
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
