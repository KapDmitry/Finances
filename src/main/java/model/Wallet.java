package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet implements Serializable {
    private List<Transaction> transactions;
    private Map<String, Category> categories;
    private double totalBalance;

    public Wallet() {
        transactions = new ArrayList<>();
        categories = new HashMap<>();
        totalBalance = 0;
    }

    public double getTotalBalanceRaw() {
        return totalBalance;
    }

    public class CategoryNotFoundException extends Exception {
        public CategoryNotFoundException(String message) {
            super(message);
        }
    }

    public class InvalidCategoryTypeException extends Exception {
        public InvalidCategoryTypeException(String message) {
            super(message);
        }
    }

    public void addTransaction(Transaction transaction) throws CategoryNotFoundException {
        String categoryName = transaction.getName();
        Category category = categories.get(categoryName);

        if (category == null) {
            throw new CategoryNotFoundException("Category not found: " + categoryName);
        }

        category.addAmount(transaction.getAmount());

        if (transaction.isExpense()) {
            totalBalance -= transaction.getAmount();
        } else {
            totalBalance += transaction.getAmount();
        }

        transactions.add(transaction);
    }

    public void addCategory(Category category) {
        categories.put(category.getName(), category);
    }

    public Category getCategoryByName(String categoryName) throws CategoryNotFoundException {
        Category category = categories.get(categoryName);
        if (category == null) {
            throw new CategoryNotFoundException("Category not found: " + categoryName);
        }
        return category;
    }

    public String getTotalBalance() {
        return "Total Balance: " + totalBalance;
    }

    public String getTotalIncome() {
        double totalIncome = 0;
        for (Category category : categories.values()) {
            if (category instanceof IncomeCategory) {
                totalIncome += category.getCurrentAmount();
            }
        }
        return "Total Income: " + totalIncome;
    }

    public String getTotalExpenses() {
        double totalExpenses = 0;
        for (Category category : categories.values()) {
            if (category instanceof ExpenseCategory) {
                totalExpenses += category.getCurrentAmount();
            }
        }
        return "Total Expenses: " + totalExpenses;
    }

    public String getIncomeByCategories() {
        StringBuilder result = new StringBuilder("Income by categories:\n");
        for (Category category : categories.values()) {
            if (category instanceof IncomeCategory) {
                result.append(category.getName())
                        .append(": ")
                        .append(category.getCurrentAmount())
                        .append("\n");
            }
        }
        return result.toString();
    }

    public String getIncomeByCategory(String categoryName) throws CategoryNotFoundException, InvalidCategoryTypeException {
        Category category = categories.get(categoryName);

        if (category == null) {
            throw new CategoryNotFoundException("Category not found: " + categoryName);
        }

        if (!(category instanceof IncomeCategory)) {
            throw new InvalidCategoryTypeException("Category " + categoryName + " is not an income category.");
        }

        return "Income in category " + categoryName + ": " + category.getCurrentAmount();
    }

    public String getExpensesByCategories() {
        StringBuilder result = new StringBuilder("Expenses by categories:\n");
        for (Category category : categories.values()) {
            if (category instanceof ExpenseCategory) {
                ExpenseCategory expenseCategory = (ExpenseCategory) category;
                result.append(expenseCategory.getName())
                        .append(": Spent ")
                        .append(expenseCategory.getCurrentAmount());
                if (expenseCategory.getBudget() > 0) {
                    result.append(", Budget: ")
                            .append(expenseCategory.getBudget())
                            .append(", Remaining: ")
                            .append(expenseCategory.getRemainingBudget());
                }
                result.append("\n");
            }
        }
        return result.toString();
    }

    public String getExpenseByCategory(String categoryName) throws CategoryNotFoundException, InvalidCategoryTypeException {
        Category category = categories.get(categoryName);

        if (category == null) {
            throw new CategoryNotFoundException("Category not found: " + categoryName);
        }

        if (!(category instanceof ExpenseCategory)) {
            throw new InvalidCategoryTypeException("Category " + categoryName + " is not an expense category.");
        }

        ExpenseCategory expenseCategory = (ExpenseCategory) category;
        StringBuilder result = new StringBuilder("Category: " + expenseCategory.getName() + ", Spent: " + expenseCategory.getCurrentAmount());

        if (expenseCategory.getBudget() > 0) {
            result.append(", Budget: ")
                    .append(expenseCategory.getBudget())
                    .append(", Remaining: ")
                    .append(expenseCategory.getRemainingBudget());
        }

        return result.toString();
    }
}
