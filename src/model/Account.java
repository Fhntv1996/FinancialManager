package model;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Account {

    private String owner;
    private String accountID;
    private String description;
    private double balance; //В рамках приложения над деньгами производятся лишь операции сложения и вычитания(c точностью до
    private String creationDate;//двух знаков после запятой),поэтому используется double вместо BigDecimal
    private LinkedList<Record> records;

    public Account(String owner, String accountID, String description, double balance) {
        this.owner = owner;
        this.accountID = accountID;
        this.description = description;
        this.balance = balance;

        initCreationDate();
    }

    public Account() {
        this("owner", "accountID", "description", 0.0);
    }

    public Account(String owner, String accountID) {
        this(owner, accountID, "description", 0.0);
    }

    /*
     * Метод определяет сортировку в списке счетов.
     */
    static public class AccountComparator implements Comparator<Account> {
        @Override
        public int compare(Account o1, Account o2) {
            return (o1.getDescription().compareTo(o2.getDescription()));
        }
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }


    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }


    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = (LinkedList<Record>) records;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }


    /*
     * Метод добавляет запись в счет и обновляет баланс счета.
     */
    public void addRecord(Record record) {
        if (records == null) {//Случай создания первой записи в новом счете.
            records = new LinkedList<>();
        }
        records.add(record);

        if (record.getOperation().equals(Record.getWithdrawalOperation())) {
            this.balance -= record.getAmount();
        }

        if (record.getOperation().equals(Record.getDepositOperation())) {
            this.balance += record.getAmount();
        }
    }

    /*
     * Метод удаляет запись из счет и обновляет баланс счета.
     */
    public void removeRecord(Record record) {
        if (record.getOperation().equals(Record.getWithdrawalOperation())) {
            setBalance(getBalance() + record.getAmount());
        }
        if (record.getOperation().equals(Record.getDepositOperation())) {
            setBalance(getBalance() - record.getAmount());
        }
        records.remove(record);
    }

    /*
     * Метод удаляет все записи из счета и возвращает баланс счета к начальной сумме.
     */
    public void removeAllRecords() {
        double amount = 0;
        for (Record record : records) {
            if (record.getOperation().equals(Record.getWithdrawalOperation())) {//FIXME nullException непонятно из-зи чего
                amount += record.getAmount();
            }
            if (record.getOperation().equals(Record.getDepositOperation())) {
                amount -= record.getAmount();
            }
        }
        records.clear();
        balance += amount;
    }

    /*
     * Метод устанавливает дату создания счета.
     */
    private void initCreationDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        creationDate = dateFormat.format(date);
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String date) {
        creationDate = date;
    }

}
