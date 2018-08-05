package model;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class User {
    private String login;//Должен быть уникальным
    private String password;
    private Set<String> accountsID;
    private TreeSet<Account> accounts;
    private Set<String> categories;

    public User(String login, String password) {
        this.login = login;
        this.password = password;

        categories = new HashSet<>();
    }

    public User() {
        this("login", "password");
    }

    public User(String login) {
        this(login, "password");
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public TreeSet<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = (TreeSet<Account>)accounts;
    }


    public Set<String> getAccountsID() {
        return accountsID;
    }

    public void setAccountsID(Set<String> accountsID) {
        this.accountsID = accountsID;
    }


    public Account getAccount(String accountID) {
        for (Account account : accounts) {
            if (account.getAccountID().equals(accountID)) {
                return account;
            }
        }
        return null;
    }

    public void addAccount(Account account) {
        accounts.add(account);
        accountsID.add(account.getAccountID());
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        accountsID.remove(account.getAccountID());
    }


    public void addCategory(String category) {
        categories.add(category);
    }

    public Set<String> getCategories() {
        return categories;
    }
}
