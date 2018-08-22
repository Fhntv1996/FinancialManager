package controller;

import model.*;
import model.exceptions.AccountAlreadyExists;
import model.exceptions.UserAlreadyExists;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/*
 * Класс для связи приложения с базой данных.
 */
public class DatabaseController implements DataStore {
    private Connection connection;

    DatabaseController(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User getUser(String login) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement("select * from users where login = ?");
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            String password = null;
            while (rs.next()) {
                password = rs.getString("password");
            }
            if (password == null) {
                return null;
            }
            return new User(login, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(stmt, rs);
        }
        return null;
    }

    @Override
    public Set<String> getUserLogins() {
        TreeSet<String> names = new TreeSet<>();

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery("select * from users");
            while (rs.next()) {
                names.add(rs.getString("login"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(stmt, rs);
        }
        return names;
    }

    @Override
    public Set<Account> getAccounts(User owner) {
        Set<Account> accounts = new TreeSet<>(new Account.AccountComparator());

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement("select * from accounts where owner = ?");
            stmt.setString(1, owner.getLogin());

            rs = stmt.executeQuery();
            while (rs.next()) {
                Account account = new Account();
                account.setOwner(owner.getLogin());
                account.setCreationDate(rs.getString("date"));
                account.setAccountID(rs.getString("account_id"));
                account.setDescription(rs.getString("description"));
//                account.setBalance(rs.getDouble("balance"));
                account.setBalance(rs.getString("balance"));

                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(stmt, rs);
        }
        return accounts;
    }

    @Override
    public Set<String> getAccountsID(User owner) {
        Set<String> accountsID = new TreeSet<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement("select * from accounts where owner = ?");
            stmt.setString(1, owner.getLogin());

            rs = stmt.executeQuery();
            while (rs.next()) {
                accountsID.add(rs.getString("account_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(stmt, rs);
        }
        return accountsID;
    }

    @Override
    public List<Record> getRecords(Account account) {
        List<Record> records = new LinkedList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement("select * from records where account_id = ?");
            stmt.setString(1, account.getAccountID());

            rs = stmt.executeQuery();
            while (rs.next()) {
                Record record = new Record();
                record.setAccountID(account.getAccountID());
                record.setRecordID(rs.getInt("record_id"));
                record.setOperation(rs.getString("operation"));
                record.setDate(rs.getString("date"));
                record.setAmount(rs.getString("amount"));
                record.setDescription(rs.getString("description"));
                record.setCategory(rs.getString("category"));

                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(stmt, rs);
        }
        return records;
    }

    @Override
    public void addUser(User user) throws UserAlreadyExists {
        PreparedStatement stmt = null;
        try {
            if (getUser(user.getLogin()) != null) {
                throw new UserAlreadyExists();
            }
            stmt = connection.prepareStatement("insert into users (login, password) values (?, ?)");
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(stmt, null);
        }
    }

    @Override
    public void changePassword(String login, String newPassword) {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("update users set password = ? where login = ?");
            stmt.setString(1, newPassword);
            stmt.setString(2, login);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(stmt, null);
        }
    }

    @Override
    public void addAccount(User user, Account account) throws AccountAlreadyExists {
        PreparedStatement stmt = null;
        try {
            if (getAccountsID(user).contains(account.getAccountID())) {
                throw new AccountAlreadyExists();
            }
            stmt = connection.prepareStatement("insert into accounts (owner, date, account_id, description, balance)" +
                    " values (?, ?, ?, ?, ?)");
            int index = 0;
            stmt.setString(++index, user.getLogin());
            stmt.setString(++index, account.getCreationDate());
            stmt.setString(++index, account.getAccountID());
            stmt.setString(++index, account.getDescription());
            stmt.setString(++index, account.getBalance());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(stmt, null);
        }
    }

    /*
     * Метод получает на вход счет и запись из этого счета.
     * Метод добавляет запись в таблицу records и меняет баланс соответствующего счета в таблице accounts.
     */
    @Override
    public void addRecord(Account account, Record record) {
        try {
            PreparedStatement stmt = connection.prepareStatement("insert into records (account_id, date, operation, amount," +
                    "description, category) values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            int index = 0;
            stmt.setString(++index, account.getAccountID());
            stmt.setString(++index, record.getDate());
            stmt.setString(++index, record.getOperation());
            stmt.setString(++index, record.getAmount());
            stmt.setString(++index, record.getDescription());
            stmt.setString(++index, record.getCategory());
            stmt.executeUpdate();

            ResultSet resultSet = stmt.getGeneratedKeys();
            record.setRecordID(resultSet.getInt(1));
            closeResources(stmt, resultSet);

            PreparedStatement statement = connection.prepareStatement("update accounts set balance = ? where account_id = ?");
            if (record.getOperation().equals(Record.getWithdrawalOperation())) {
                statement.setString(1, substraction(account.getBalance(), record.getAmount()));
            }
            if (record.getOperation().equals(Record.getDepositOperation())) {
                statement.setString(1, summ(account.getBalance(), record.getAmount()));
            }
            statement.setString(2, account.getAccountID());

            statement.executeUpdate();
            closeResources(statement, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User removeUser(String login) {
        User user = getUser(login);
        if (user == null) {
            return null;
        }

        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("delete from users where login = ?");
            stmt.setString(1, login);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(stmt, null);
        }
        return user;
    }


    @Override
    public Account removeAccount(User owner, Account account) {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("delete from accounts where account_id = ?");
            stmt.setString(1, account.getAccountID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(stmt, null);
        }
        return account;
    }

    /*
     * Метод получает на вход счет и запись из этого счета.
     * Метод удаляет запись из таблицы records и меняет баланс соответствующего счета в таблице accounts.
     */
    @Override
    public Record removeRecord(Account account, Record record) {
        try {
            PreparedStatement stmt = connection.prepareStatement("delete from records where record_id = ?");
            stmt.setInt(1, record.getRecordID());
            stmt.executeUpdate();
            closeResources(stmt, null);

            PreparedStatement statement = connection.prepareStatement("update accounts set balance = ? where account_id = ?");
            if (record.getOperation().equals(Record.getWithdrawalOperation())) {
                statement.setString(1, summ(account.getBalance(), record.getAmount()));
            }
            if (record.getOperation().equals(Record.getDepositOperation())) {
                statement.setString(1, substraction(account.getBalance(), record.getAmount()));
            }
            statement.setString(2, account.getAccountID());
            statement.executeUpdate();
            closeResources(statement, null);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return record;
    }

    /*
     * Метод получает на вход счет.
     * Сначала метод подсчитывает, сколько всего денег на этот счет было начислено и снято с него.
     * Затем он удаляет все записи из данного счета из таблицы records и
     * возвращает баланс счета в таблице accounts к начальной сумме.
     *
     */
    @Override
    public void removeAllRecordsFromAccount(Account account) {
        try {
            PreparedStatement stmt = connection.prepareStatement("select operation, sum(amount) as  amount " +
                    "                                                  from records " +
                    "                                                  where account_id = ?" +
                    "                                                  group by operation");
            stmt.setString(1, account.getAccountID());
            ResultSet rs = stmt.executeQuery();
            double withdrawalAmount = 0;
            double depositAmount = 0;
            while (rs.next()) {
                if (rs.getString("operation").equals(Record.getDepositOperation())) {
                    depositAmount = rs.getDouble("amount");
                }
                if (rs.getString("operation").equals(Record.getWithdrawalOperation())) {
                    withdrawalAmount = rs.getDouble("amount");
                }
            }
            closeResources(stmt, rs);

            PreparedStatement stmt2 = connection.prepareStatement("update accounts set balance = ? where account_id = ?");
            stmt2.setString(1, new BigDecimal(account.getBalance()).add(new BigDecimal(withdrawalAmount).
                    subtract(new BigDecimal(depositAmount))).toString());//FIXME
            stmt2.setString(2, account.getAccountID());
            stmt2.executeUpdate();
            closeResources(stmt2, null);

            PreparedStatement stmt3 = connection.prepareStatement("delete from records where account_id = ?");
            stmt3.setString(1, account.getAccountID());
            stmt3.executeUpdate();
            closeResources(stmt3, null);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод проверяет, есть пользователь с таким логином и паролем.
    boolean authorization(String login, String password) {
        User user = getUser(login);
        return user != null && user.getPassword().equals(password);
    }

    private void closeResources(Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String substraction(String balance, String amount) {
        return new BigDecimal(balance).subtract(new BigDecimal(amount)).toString();
    }

    private String summ(String balance, String amount) {
        return new BigDecimal(balance).subtract(new BigDecimal(amount)).toString();
    }

}
