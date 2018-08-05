package model;

import model.exceptions.AccountAlreadyExists;
import model.exceptions.UserAlreadyExists;

import java.util.List;
import java.util.Set;

public interface DataStore {
    // Если нет пользователя, возвращает null.
    User getUser(String login);

    // Если нет пользователей, возвращает пустую коллекцию(не null)
    Set<String> getUserLogins();

    // Если нет счетов, возвращает пустую коллекцию(не null)
    Set<Account> getAccounts(User owner);

    Set<String> getAccountsID(User owner);

    // Если нет записей, возвращает пустую коллекцию(не null)
    List<Record> getRecords(Account account);

    void addUser(User user) throws UserAlreadyExists;

    void changePassword(String login, String newPassword);

    void addAccount(User user, Account account) throws AccountAlreadyExists;

    void addRecord(Account account, Record record);

    // Возвращает удаленного пользователя или null, если такого пользователя нет.
    User removeUser(String name);//FIXME не реализован

    // Возвращает удаленный счет или null, если такого счета нет.
    Account removeAccount(User owner, Account account);

    // Возвращает удаленную запись или null, если такой записи нет.
    Record removeRecord(Account from, Record record);

    void removeAllRecordsFromAccount(Account account);
}
