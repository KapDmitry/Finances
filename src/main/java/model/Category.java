package model;

import java.io.Serializable;

abstract public class Category implements Serializable {
    protected String name;
    protected double currentAmount;

    public Category(String name) {
        this.name = name;
        this.currentAmount = 0;
    }

    public String getName() {
        return name;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void addAmount(double amount) {
        currentAmount += amount;
    }
}
