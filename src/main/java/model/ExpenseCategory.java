package model;

public class ExpenseCategory extends Category {
    private double budget;

    public ExpenseCategory(String name, double budget) {
        super(name);
        this.budget = budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getBudget() {
        return budget;
    }

    public double getRemainingBudget() {
        return budget - currentAmount;
    }
}
