package inf222.aop.account;

// NB! Do not make changes to this file.
public class Account {

    private final String name;
    private Double balance;
    private final Currency currency;

    public Account(String name, Double balance, Currency currency) {
        this.name = name;
        this.balance = balance;
        this.currency = currency;
    }

    public String getAccountName() {
        return this.name;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public Double getBalance() {
        return this.balance;
    }

    public boolean withdraw(Double amount) {
        if (amount > balance)
            return false;
        balance -= amount;
        return true;
    }

    public void deposit(Double amount) {
        balance += amount;
    }

}
