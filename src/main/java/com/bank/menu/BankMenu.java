package com.bank.menu;

import com.bank.dao.AccountDAO;
import com.bank.dao.TransactionDAO;
import com.bank.db.DBConnection;
import com.bank.model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BankMenu {

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final Scanner scanner;

    public BankMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void printMenu() {
        System.out.println("┌─────────────────────────────────────────────┐");
        System.out.println("│                  MAIN MENU                  │");
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.println("│  1. Create Account                          │");
        System.out.println("│  2. Check Balance                           │");
        System.out.println("│  3. Deposit Money                           │");
        System.out.println("│  4. Withdraw Money                          │");
        System.out.println("│  5. Transfer Money                          │");
        System.out.println("│  6. View Transaction History                │");
        System.out.println("│  7. Exit                                    │");
        System.out.println("└─────────────────────────────────────────────┘");
    }

    public void createAccount() {
        System.out.println("\n── Create New Account ──────────────────────");
        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("⚠️ Name cannot be blank.\n");
            return;
        }

        BigDecimal initialDeposit = readAmount("Enter initial deposit (0 for none): ₹");
        if (initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("⚠️ Initial deposit cannot be negative.\n");
            return;
        }

        try {
            int newId = accountDAO.createAccount(name, initialDeposit);

            if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
                try (Connection conn = DBConnection.getConnection()) {
                    transactionDAO.recordTransaction(conn, newId,
                            "DEPOSIT", initialDeposit, "Initial deposit on account creation");
                }
            }

            System.out.println("✅ Account created!");
            System.out.println("   Account ID   : " + newId);
            System.out.println("   Account Name : " + name);
            System.out.printf("   Balance      : ₹%.2f%n%n", initialDeposit);

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage() + "\n");
        }
    }

    public void checkBalance() {
        System.out.println("\n── Check Balance ───────────────────────────");
        int id = readInt("Enter Account ID: ");

        try {
            Account account = requireAccount(id);
            System.out.println("┌─────────────────────────────────────────┐");
            System.out.printf("│ Account ID   : %-25d │%n", account.getAccountId());
            System.out.printf("│ Name         : %-25s │%n", account.getAccountName());
            System.out.printf("│ Balance      : ₹%-24.2f │%n", account.getBalance());
            System.out.printf("│ Created      : %-25s │%n", account.getCreatedAt().toLocalDate());
            System.out.println("└─────────────────────────────────────────┘\n");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage() + "\n");
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage() + "\n");
        }
    }

    public void deposit() {
        System.out.println("\n── Deposit Money ───────────────────────────");
        int id = readInt("Enter Account ID: ");
        BigDecimal amount = readAmount("Enter deposit amount: ₹");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("⚠️ Deposit amount must be greater than zero.\n");
            return;
        }

        try {
            Account account = requireAccount(id);
            BigDecimal newBal = account.getBalance().add(amount);

            try (Connection conn = DBConnection.getConnection()) {
                accountDAO.updateBalance(conn, id, newBal);
                transactionDAO.recordTransaction(conn, id,
                        "DEPOSIT", amount, "Deposit to account #" + id);
            }

            System.out.printf("✅ ₹%.2f deposited. New balance: ₹%.2f%n%n", amount, newBal);

        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage() + "\n");
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage() + "\n");
        }
    }

    public void withdraw() {
        System.out.println("\n── Withdraw Money ──────────────────────────");
        int id = readInt("Enter Account ID: ");
        BigDecimal amount = readAmount("Enter withdrawal amount: ₹");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("⚠️ Withdrawal amount must be greater than zero.\n");
            return;
        }

        try {
            Account account = requireAccount(id);

            if (account.getBalance().compareTo(amount) < 0) {
                System.out.printf("⚠️ Insufficient funds. Available: ₹%.2f, Requested: ₹%.2f%n%n",
                        account.getBalance(), amount);
                return;
            }

            BigDecimal newBal = account.getBalance().subtract(amount);

            try (Connection conn = DBConnection.getConnection()) {
                accountDAO.updateBalance(conn, id, newBal);
                transactionDAO.recordTransaction(conn, id,
                        "WITHDRAWAL", amount, "Withdrawal from account #" + id);
            }

            System.out.printf("✅ ₹%.2f withdrawn. New balance: ₹%.2f%n%n", amount, newBal);

        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage() + "\n");
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage() + "\n");
        }
    }

    public void transfer() {
        System.out.println("\n── Transfer Money ──────────────────────────");
        int fromId = readInt("Enter sender Account ID  : ");
        int toId   = readInt("Enter receiver Account ID: ");

        if (fromId == toId) {
            System.out.println("⚠️ Cannot transfer to the same account.\n");
            return;
        }

        BigDecimal amount = readAmount("Enter transfer amount: ₹");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("⚠️ Transfer amount must be greater than zero.\n");
            return;
        }

        Connection conn = null;
        try {
            Account sender   = requireAccount(fromId);
            Account receiver = requireAccount(toId);

            if (sender.getBalance().compareTo(amount) < 0) {
                System.out.printf("⚠️ Insufficient funds in Account #%d. Available: ₹%.2f%n%n",
                        fromId, sender.getBalance());
                return;
            }

            BigDecimal senderNew   = sender.getBalance().subtract(amount);
            BigDecimal receiverNew = receiver.getBalance().add(amount);

            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            accountDAO.updateBalance(conn, fromId, senderNew);
            accountDAO.updateBalance(conn, toId,   receiverNew);
            transactionDAO.recordTransaction(conn, fromId,
                    "TRANSFER_OUT", amount, "Transfer to Account #"   + toId);
            transactionDAO.recordTransaction(conn, toId,
                    "TRANSFER_IN",  amount, "Transfer from Account #" + fromId);

            conn.commit();

            System.out.printf("✅ ₹%.2f transferred from Account #%d to Account #%d%n",
                    amount, fromId, toId);
            System.out.printf("   Account #%d new balance: ₹%.2f%n",   fromId, senderNew);
            System.out.printf("   Account #%d new balance: ₹%.2f%n%n", toId,   receiverNew);

        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage() + "\n");
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("⚠️ Transfer failed and was rolled back. No money was moved.");
                } catch (SQLException rb) {
                    System.err.println("❌ Rollback failed: " + rb.getMessage());
                }
            }
            System.out.println("❌ Database error: " + e.getMessage() + "\n");
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignore) { }
            }
        }
    }

    public void viewHistory() {
        System.out.println("\n── Transaction History ─────────────────────");
        int id = readInt("Enter Account ID: ");

        try {
            Account account  = requireAccount(id);
            List<String> history = transactionDAO.getTransactionHistory(id);

            System.out.println("\nTransactions for: " + account.getAccountName()
                             + " (Account #" + id + ")");
            System.out.println("─".repeat(80));

            if (history.isEmpty()) {
                System.out.println("  No transactions found.");
            } else {
                System.out.printf("  %-6s %-16s %-12s %-35s %s%n",
                        "ID", "TYPE", "AMOUNT", "DESCRIPTION", "DATE");
                System.out.println("─".repeat(80));
                history.forEach(System.out::println);
            }

            System.out.println("─".repeat(80) + "\n");

        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage() + "\n");
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage() + "\n");
        }
    }

    private Account requireAccount(int accountId) throws SQLException {
        Optional<Account> found = accountDAO.findById(accountId);
        return found.orElseThrow(
            () -> new IllegalArgumentException("Account #" + accountId + " not found.")
        );
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Please enter a valid whole number.");
            }
        }
    }

    private BigDecimal readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Please enter a valid amount (e.g. 500 or 1000.50).");
            }
        }
    }
}

