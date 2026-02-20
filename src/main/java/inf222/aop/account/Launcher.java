package inf222.aop.account;

public class Launcher {
    private static final Bank bank = new Bank();

    public static void main(String[] args) {
        Account ac1 = new Account("ac1", 100d, Currency.NOK);
        Account ac2 = new Account("ac2", 100d, Currency.USD);
        bank.internationalTransfer(ac1, ac2, 3d);

        Account ac3 = new Account("ac3", 1_000_000d, Currency.NOK);
        Account ac4 = new Account("ac4", 40_000d, Currency.NOK);
        bank.domesticTransfer(ac3, ac4, 160_000d);

        Account ac5 = new Account("ac5", 10_000d, Currency.NOK);
        Account ac6 = new Account("ac6", 40_000d, Currency.NOK);
        bank.domesticTransfer(ac5, ac6, 50_000d);

        Account ac7 = new Account("ac7", 10_000d, Currency.GBP);
        Account ac8 = new Account("ac8", 40_000d, Currency.DKK);
        bank.internationalTransfer(ac7, ac8, 50_000d);
    }

    // Should output (Note, the timestamps will vary):
    /*
     * 15:56:38.828 [inf222.aop.account.Launcher.main()] INFO
     * inf222.aop.account.Bank -- International transfer from ac1 to ac2, 3.0 NOK
     * converted to USD
     * 15:56:38.829 [inf222.aop.account.Launcher.main()] INFO
     * inf222.aop.account.Bank -- Transfer above 100000.0 from ac3 to ac4, amount:
     * 160000.0
     * 15:56:38.829 [inf222.aop.account.Launcher.main()] INFO
     * inf222.aop.account.Bank -- Error in transfer from ac5 to ac6, amount: 50000.0
     * NOK, method: domesticTransfer(fromDAcc, toDAcc, amount)
     * 15:56:38.829 [inf222.aop.account.Launcher.main()] INFO
     * inf222.aop.account.Bank -- International transfer from ac7 to ac8, 50000.0
     * GBP converted to DKK
     * 15:56:38.829 [inf222.aop.account.Launcher.main()] INFO
     * inf222.aop.account.Bank -- Error in transfer from ac7 to ac8, amount: 50000.0
     * GBP, method: internationalTransfer(fromIAcc, toIAcc, amount)
     */

}
