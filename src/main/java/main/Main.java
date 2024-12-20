package main;

import service.FinanceManager;

public class Main {
    public static void main(String[] args) {
        FinanceManager fm = new FinanceManager();
        fm.start();
    }
}