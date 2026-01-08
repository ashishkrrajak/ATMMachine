package atmmachine.components;

public class DepositSlot {
    private double depositedAmount;

    public void acceptCash(double amount) {
        this.depositedAmount = amount;
        System.out.println("Cash deposited: $" + String.format("%.2f", amount));
    }

    public void acceptCheck(double amount) {
        this.depositedAmount = amount;
        System.out.println("Check deposited: $" + String.format("%.2f", amount));
    }

    public double getDepositedAmount() { return depositedAmount; }

    public void reset() {
        this.depositedAmount = 0;
    }
}
