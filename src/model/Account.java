package model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Account {

    private String owner;
    private String accountID;
    private String description;
    private BigDecimal balance;
    private String creationDate;
    private LinkedList<Record> records;

    public Account(String owner, String accountID, String description, String balance) {
        this.owner = owner;
        this.accountID = accountID;
        this.description = description;
        this.balance = new BigDecimal(balance);

        initCreationDate();
    }

    public Account() {
        this("owner", "accountID", "description", "0.0");
    }

    public Account(String owner, String accountID) {
        this(owner, accountID, "description", "0.0");
    }

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


    public String getBalance() {
        return balance.toString();
    }

    public void setBalance(String balance) {
        this.balance = new BigDecimal(balance);
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
            balance = balance.subtract(new BigDecimal(record.getAmount()));
        }

        if (record.getOperation().equals(Record.getDepositOperation())) {
            balance = balance.add(new BigDecimal(record.getAmount()));
        }
    }

    /*
     * Метод удаляет запись из счет и обновляет баланс счета.
     */
    public void removeRecord(Record record) {
        if (record.getOperation().equals(Record.getWithdrawalOperation())) {
            setBalance(new BigDecimal(getBalance()).add(new BigDecimal(record.getAmount())).toString());
        }
        if (record.getOperation().equals(Record.getDepositOperation())) {
            setBalance(new BigDecimal(getBalance()).subtract(new BigDecimal(record.getAmount())).toString());
        }
        records.remove(record);
    }

    /*
     * Метод удаляет все записи из счета и возвращает баланс счета к начальной сумме.
     */
    public void removeAllRecords() {
        BigDecimal amount = new BigDecimal("0");
        for (Record record : records) {
            if (record.getOperation().equals(Record.getWithdrawalOperation())) {
                balance = balance.add(new BigDecimal(record.getAmount()));
            }
            if (record.getOperation().equals(Record.getDepositOperation())) {
                balance = balance.subtract(new BigDecimal(record.getAmount()));
            }
        }
        records.clear();
        balance = balance.add(amount);
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
