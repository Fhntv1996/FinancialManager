package controller;

import model.DbHelper;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    static private DatabaseController dbController;

    @BeforeAll
    static void  setUp() {
        DbHelper dbHelper = DbHelper.getInstance();
        Connection connection = dbHelper.getConnection();
        dbController = new DatabaseController(connection);
        System.out.println("sout");
    }

    @Test
    void addUser() {
//        dbController.addUser(new User("user1"));
    }

    @Test
    void changePassword() {
    }

    @Test
    void addAccount() {
    }

    @Test
    void addRecord() {
    }

    @Test
    void getUser() {

    }

    @Test
    void getUserLogins() {
    }

    @Test
    void getAccounts() {
    }

    @Test
    void getAccountsID() {
    }

    @Test
    void getRecords() {
    }

    @Test
    void removeUser() {
    }

    @Test
    void removeAccount() {
    }

    @Test
    void removeRecord() {
    }

    @Test
    void removeAllRecordsFromAccount() {
    }

    @Test
    void authorization() {
    }

}