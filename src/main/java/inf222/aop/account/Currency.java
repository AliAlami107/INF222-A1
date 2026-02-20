package inf222.aop.account;

// NB! Do not make changes to this file.
public enum Currency {
    GBP(14.0d),
    USD(11.0d),
    DKK(1.5d),
    NOK(1d),
    JPY(0.07d);

    public final double toNOK;

    private Currency(double toNOK) {
        this.toNOK = toNOK;
    }
}
