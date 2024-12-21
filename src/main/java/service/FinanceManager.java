package service;

import model.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FinanceManager {
    private Map<String, User> users = new HashMap<>();
    private User currentUser;
    private Scanner scanner = new Scanner(System.in);
    private final String DATA_FILE = "finance_manager_data.ser";
    private PrintStream output = System.out;

    public FinanceManager() {
        loadData();
    }


    public void start() {
        while (true) {
            printNotifications();
            printMenu();
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "1": registerUser(); break;
                case "2": loginUser(); break;
                case "3": addExpense(); break;
                case "4": addIncome(); break;
                case "5": setBudget(); break;
                case "6": getTotalIncome(); break;
                case "7": getTotalExpenses(); break;
                case "8": getIncomeByCategory(); break;
                case "9": getExpensesByCategory(); break;
                case "10": getFullExpenseInfo(); break;
                case "11": getFullIncomeInfo(); break;
                case "12": getIncomeByCategories(); break;
                case "13": getExpensesByCategories(); break;
                case "14": configureOutputToFile(); break; // Вывод в файл
                case "15": resetOutputToConsole(); break;
                case "16":
                    System.out.println("Exiting...");
                    saveData();
                    return;
                default: System.out.println("Invalid command. Try again."); break;
            }
        }
    }

    private void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(users);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            users = (Map<String, User>) in.readObject();
            System.out.println("Data loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("No data file found, starting with an empty system.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    private void printMenu() {
        System.out.println("\nPlease choose a command:");
        System.out.println("1. Register a new user");
        System.out.println("2. Log in");
        System.out.println("3. Add a new expense");
        System.out.println("4. Add a new income");
        System.out.println("5. Set budget for a category");
        System.out.println("6. Get total income");
        System.out.println("7. Get total expenses");
        System.out.println("8. Get income by category");
        System.out.println("9. Get expense by category");
        System.out.println("10. Get full expense info");
        System.out.println("11. Get full income info");
        System.out.println("12. Get incomes by categories");
        System.out.println("13. Get expenses by categories");
        System.out.println("14. Redirect wallet info output to a file");
        System.out.println("15. Redirect wallet info output back to console");
        System.out.println("16. Exit");
    }

    private void configureOutputToFile() {
        output.println("Enter file name to redirect output:");
        String fileName = scanner.nextLine().trim();

        try {
            output = new PrintStream(new FileOutputStream(fileName, true), true);
            System.out.println("Output redirected to file: " + fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Error redirecting output: " + e.getMessage());
            output = System.out;
        }
    }

    private void resetOutputToConsole() {
        output = System.out;
        output.println("Output redirected back to console.");
    }

    private void printNotifications() {
        if (currentUser != null) {
            String notification = currentUser.getLastNotification();
            if (notification != null) {
                output.println(notification);
            }

            checkNegativeBalance();

            notification = currentUser.getLastNotification();
            if (notification != null) {
                output.println(notification);
            }
        }
    }

    private void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty.");
            return;
        }

        if (users.containsKey(username)) {
            System.out.println("User already exists!");
            return;
        }

        User newUser = new User(username, password);
        users.put(username, newUser);
        currentUser = newUser;
        System.out.println("User registered and logged in successfully!");
    }

    private void loginUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        User user = users.get(username);
        if (user != null && user.authenticate(password)) {
            currentUser = user;
            System.out.println("Logged in as " + username);
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private void addExpense() {
        if (currentUser == null) {
            System.out.println("You need to log in first.");
            return;
        }

        System.out.print("Enter category name: ");
        String categoryName = scanner.nextLine().trim();
        System.out.print("Enter amount: ");
        double amount = getValidAmount();

        if (categoryName.isEmpty()) {
            System.out.println("Category name cannot be empty.");
            return;
        }

        Transaction transaction = new Transaction(categoryName, amount, true);
        addTransactionToWallet(transaction);
        checkBudgetExceedance(transaction);
    }

    private void checkBudgetExceedance(Transaction transaction) {
        try {
            Category category = currentUser.getWallet().getCategoryByName(transaction.getName());

            if (category instanceof ExpenseCategory) {
                double remainingBudget = ((ExpenseCategory) category).getBudget() - category.getCurrentAmount();
                if (remainingBudget < 0) {
                    currentUser.addNotification("Expense exceeds budget for category: " + category.getName());
                }
            }
        } catch (Wallet.CategoryNotFoundException e) {
        }
    }

    private void checkNegativeBalance() {
        if (currentUser != null && currentUser.getWallet().getTotalBalanceRaw() < 0) {
            currentUser.addNotification("Warning: Your total balance is negative.");
        }
    }

    private void addIncome() {
        if (currentUser == null) {
            System.out.println("You need to log in first.");
            return;
        }

        System.out.print("Enter category name: ");
        String categoryName = scanner.nextLine().trim();
        System.out.print("Enter amount: ");
        double amount = getValidAmount();

        if (categoryName.isEmpty()) {
            System.out.println("Category name cannot be empty.");
            return;
        }

        Transaction transaction = new Transaction(categoryName, amount, false);
        addTransactionToWallet(transaction);
    }

    private void setBudget() {
        if (currentUser == null) {
            System.out.println("You need to log in first.");
            return;
        }

        System.out.print("Enter category name: ");
        String categoryName = scanner.nextLine().trim();
        System.out.print("Enter budget amount: ");
        double budget = getValidAmount();

        if (categoryName.isEmpty()) {
            System.out.println("Category name cannot be empty.");
            return;
        }

        try {
            Category category = currentUser.getWallet().getCategoryByName(categoryName);
            if (category instanceof IncomeCategory) {
                System.out.println("Error: You can't set budget with income category");
                return;
            }
            ((ExpenseCategory) category).setBudget(budget);
            System.out.println("Budget set successfully!");
        } catch (Wallet.CategoryNotFoundException e) {
            Category category = new ExpenseCategory(categoryName, budget);
            currentUser.getWallet().addCategory(category);
            System.out.println("Category not found. New expense category created and budget set successfully!");
        }
    }

    private void getTotalIncome() {
        if (currentUser != null) {
            output.println(currentUser.getWallet().getTotalIncome());
        } else {
            System.out.println("You need to log in first.");
        }
    }

    private void getTotalExpenses() {
        if (currentUser != null) {
            output.println(currentUser.getWallet().getTotalExpenses());
        } else {
            System.out.println("You need to log in first.");
        }
    }

    private void getIncomeByCategory() {
        if (currentUser == null) {
            System.out.println("You need to log in first.");
            return;
        }

        System.out.print("Enter category name: ");
        String categoryName = scanner.nextLine().trim();
        try {
            output.println(currentUser.getWallet().getIncomeByCategory(categoryName));
        } catch (Wallet.CategoryNotFoundException | Wallet.InvalidCategoryTypeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getIncomeByCategories() {
        if (currentUser == null) {
            System.out.println("You need to log in first.");
            return;
        }

        System.out.print("Enter category names (semicolon-separated): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("Category names cannot be empty.");
            return;
        }

        String[] categoryNames = input.split(";");

        for (String categoryName : categoryNames) {
            try {
                String income = currentUser.getWallet().getIncomeByCategory(categoryName);
                output.println("Income for category '" + categoryName + "': " + income);
            } catch (Wallet.CategoryNotFoundException | Wallet.InvalidCategoryTypeException e) {
                output.println("Error for category '" + categoryName + "': " + e.getMessage());
            }
        }
    }

    private void getExpensesByCategory() {
        if (currentUser == null) {
            System.out.println("You need to log in first.");
            return;
        }

        System.out.print("Enter category name: ");
        String categoryName = scanner.nextLine().trim();
        try {
            output.println(currentUser.getWallet().getExpenseByCategory(categoryName));
        } catch (Wallet.CategoryNotFoundException | Wallet.InvalidCategoryTypeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getExpensesByCategories() {
        if (currentUser == null) {
            System.out.println("You need to log in first.");
            return;
        }

        System.out.print("Enter category names (semicolon-separated): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("Category names cannot be empty.");
            return;
        }

        String[] categoryNames = input.split(";");

        for (String categoryName : categoryNames) {
            try {
                String expenses = currentUser.getWallet().getExpenseByCategory(categoryName);
                output.println("Expenses for category '" + categoryName + "': " + expenses);
            } catch (Wallet.CategoryNotFoundException | Wallet.InvalidCategoryTypeException e) {
                output.println("Error for category '" + categoryName + "': " + e.getMessage());
            }
        }
    }

    private void getFullExpenseInfo() {
        if (currentUser != null) {
            output.println(currentUser.getWallet().getExpensesByCategories());
        } else {
            System.out.println("You need to log in first.");
        }
    }

    private void getFullIncomeInfo() {
        if (currentUser != null) {
            output.println(currentUser.getWallet().getIncomeByCategories());
        } else {
            System.out.println("You need to log in first.");
        }
    }

    private void addTransactionToWallet(Transaction transaction) {
        if (currentUser == null) {
            System.out.println("You need to log in first.");
            return;
        }

        Wallet wallet = currentUser.getWallet();
        try {
            Category category = wallet.getCategoryByName(transaction.getName());


            if (transaction.isExpense() && !(category instanceof ExpenseCategory)) {
                System.out.println("Error: Transaction type (Expense) does not match category type (Income).");
                return;
            }

            if (!transaction.isExpense() && !(category instanceof IncomeCategory)) {
                System.out.println("Error: Transaction type (Income) does not match category type (Expense).");
                return;
            }

            wallet.addTransaction(transaction);
            System.out.println("Transaction added successfully!");
        } catch (Wallet.CategoryNotFoundException e) {
            Category category = transaction.isExpense() ?
                    new ExpenseCategory(transaction.getName(), 0) : new IncomeCategory(transaction.getName());

            wallet.addCategory(category);

            try {
                wallet.addTransaction(transaction);
                System.out.println("Transaction added successfully!");
            } catch (Wallet.CategoryNotFoundException ee) {
                System.out.println("Error adding transaction: " + ee.getMessage());
            }
        }
    }

    private double getValidAmount() {
        while (true) {
            try {
                double amount = Double.parseDouble(scanner.nextLine().trim());
                if (amount < 0) {
                    System.out.println("Amount must be positive. Try again.");
                } else {
                    return amount;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a valid number.");
            }
        }
    }
}
