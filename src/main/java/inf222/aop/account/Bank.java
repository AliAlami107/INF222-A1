package inf222.aop.account;

import inf222.aop.account.annotation.Transfer;

// NB! Do not make changes to this file.
public class Bank {

    /**
     * We do the conversion by first converting {@code amount} which is in the
     * currency{@code from} to NOK and then converting that value to the desired
     * {@code to} currency.
     * 
     * @param amount Amount that will be transfered in {@code from} currency.
     * @param from   The currency we are transferring from.
     * @param to     The currency we are transferring to.
     * @return The amount to be transferred in {@code to} currency.
     */
    private Double convertCurrency(Double amount, Currency from, Currency to) {
        return (amount * from.toNOK) / to.toNOK;
    }

    /**
     * Perform an international transfer
     * 
     * @param fromIAcc from International Account
     * @param toIAcc   to International Account
     * @param amount   Amount to be transferred
     * @return true if the transfer was successfull, false otherwise
     */
    @Transfer(logErrors = true, internationalTransfer = true)
    public boolean internationalTransfer(Account fromIAcc, Account toIAcc, Double amount) {
        if (amount < 0 || !fromIAcc.withdraw(amount))
            return false;

        toIAcc.deposit(convertCurrency(amount, fromIAcc.getCurrency(), toIAcc.getCurrency()));
        return true;
    }

    /**
     * Perform an domestic transfer
     * 
     * @param fromIAcc from Domestic Account
     * @param toIAcc   to Domestic Account
     * @param amount   Amount to be transferred
     * @return true if the transfer was successfull, false otherwise
     */
    @Transfer(logErrors = true, LogTransferAbove = 100_000)
    public boolean domesticTransfer(Account fromDAcc, Account toDAcc, Double amount) {
        if (amount < 0 || !fromDAcc.withdraw(amount))
            return false;

        toDAcc.deposit(amount);
        return true;
    }

    /**
     * NB! You should not add annotations to this method.
     * 
     * Attempt to withdraw money from accounts. If the first account is lacking
     * funds, we will attempt to withdraw the rest from the second account.
     * 
     * @param ac1    Account to first withdraw money from.
     * @param ac2    Accound to withdraw money from if the first account is lacking
     *               funds.
     * @param amount Amount to be withdrawn.
     * @return true if the transfer was successfull, false otherwise.
     */
    public boolean withdrawFromMultipleAccounts(Account ac1, Account ac2, Double amount) {
        if (amount < 0 || (ac1.getBalance() + ac2.getBalance()) < amount) {
            return false;
        }
        double fromAc1 = (amount > ac1.getBalance()) ? ac1.getBalance() : amount;
        double fromAc2 = (amount - ac1.getBalance() > 0) ? amount - ac1.getBalance() : 0;

        return ac1.withdraw(fromAc1) && ac2.withdraw(fromAc2);
    }
}
