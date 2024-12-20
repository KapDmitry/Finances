package model;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String name;
    private double amount;
    private boolean isExpense;

    public Transaction(String name, double amount, boolean isExpense) {
        this.name = name;
        this.amount = amount;
        this.isExpense = isExpense;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isExpense() {
        return isExpense;
    }
}
