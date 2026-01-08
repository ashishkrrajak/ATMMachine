package atmmachine.components;

import java.util.HashMap;
import java.util.Map;

public class CashDispenser {
    private Map<Integer, Integer> cashInventory; // denomination -> count
    private static final int[] DENOMINATIONS = {100, 50, 20, 10};

    public CashDispenser() {
        cashInventory = new HashMap<>();
        initializeCash();
    }

    private void initializeCash() {
        cashInventory.put(100, 100);
        cashInventory.put(50, 200);
        cashInventory.put(20, 500);
        cashInventory.put(10, 500);
    }

    public boolean canDispense(double amount) {
        if (amount <= 0 || amount % 10 != 0) {
            return false;
        }

        int remaining = (int) amount;
        Map<Integer, Integer> tempInventory = new HashMap<>(cashInventory);

        for (int denom : DENOMINATIONS) {
            int available = tempInventory.get(denom);
            int needed = remaining / denom;
            int used = Math.min(needed, available);
            remaining -= used * denom;
        }

        return remaining == 0;
    }

    public Map<Integer, Integer> dispense(double amount) {
        if (!canDispense(amount)) {
            System.out.println("Cannot dispense exact amount.");
            return null;
        }

        Map<Integer, Integer> dispensed = new HashMap<>();
        int remaining = (int) amount;

        for (int denom : DENOMINATIONS) {
            int available = cashInventory.get(denom);
            int needed = remaining / denom;
            int used = Math.min(needed, available);

            if (used > 0) {
                dispensed.put(denom, used);
                cashInventory.put(denom, available - used);
                remaining -= used * denom;
            }
        }

        System.out.println("\n*** DISPENSING CASH ***");
        for (Map.Entry<Integer, Integer> entry : dispensed.entrySet()) {
            System.out.println("  $" + entry.getKey() + " x " + entry.getValue());
        }
        System.out.println("  Total: $" + String.format("%.2f", amount));

        return dispensed;
    }

    public void addCash(int denomination, int count) {
        cashInventory.merge(denomination, count, Integer::sum);
    }

    public double getTotalCash() {
        return cashInventory.entrySet().stream()
            .mapToDouble(e -> e.getKey() * e.getValue())
            .sum();
    }

    public void displayInventory() {
        System.out.println("\n=== Cash Dispenser Inventory ===");
        for (int denom : DENOMINATIONS) {
            System.out.println("$" + denom + ": " + cashInventory.get(denom) + " notes");
        }
        System.out.println("Total: $" + String.format("%.2f", getTotalCash()));
    }
}
