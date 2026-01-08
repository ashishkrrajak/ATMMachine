package atmmachine.services;

import atmmachine.models.BankAccount;
import java.util.HashMap;
import java.util.Map;

public class BankService {
    private Map<String, BankAccount> accounts;

    public BankService() {
        this.accounts = new HashMap<>();
    }

    public void addAccount(BankAccount account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public BankAccount getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public boolean accountExists(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }
}
