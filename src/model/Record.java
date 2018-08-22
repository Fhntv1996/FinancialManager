package model;

import java.math.BigDecimal;

public class Record {
    private String accountID;
    private int recordID;//Получаем из базы данных(autoincrement)
    private String operation;//withdrawal, deposit
    private String date;
    private BigDecimal amount;
    private String description;
    private String category;

    public Record(String accountID, String operation, String date, String amount, String description, String category) {
        this.accountID = accountID;
        this.operation = operation;
        this.date = date;
        this.amount = new BigDecimal(amount);
        this.description = description;
        this.category = category;
    }

    public Record() {
        this("accountID", "operation", "date", "0", "description", "category");
    }

    public Record(String accountID) {
        this(accountID, "operation", "date", "0", "description", "category");
    }


    public static String getWithdrawalOperation() {
        return "Снятие";
    }

    public static String getDepositOperation() {
        return "Начисление";
    }


    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }


    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }


    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getAmount() {
        return amount.toString();
    }

    public void setAmount(String amount) {
        this.amount = new BigDecimal(amount);
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
