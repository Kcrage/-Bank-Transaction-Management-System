package com.bank;

import com.bank.db.DBConnection;
import com.bank.menu.BankMenu;

import java.sql.SQLException;
import java.util.Scanner;

public class BankApp {

    public static void main(String[] args) {
        printBanner();

        try (var conn = DBConnection.getConnection()) {
            System.out.println("✅ Connected to database successfully.\n");
        } catch (SQLException e) {
            System.err.println("❌ Cannot connect to database: " + e.getMessage());
            System.err.println("   → Make sure MySQL is running.");
            System.err.println("   → Make sure db_setup.sql has been executed.");
            System.err.println("   → Check the database credentials in DBConnection.java.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        BankMenu menu   = new BankMenu(scanner);
        boolean running = true;

        while (running) {
            menu.printMenu();
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> menu.createAccount();
                case "2" -> menu.checkBalance();
                case "3" -> menu.deposit();
                case "4" -> menu.withdraw();
                case "5" -> menu.transfer();
                case "6" -> menu.viewHistory();
                case "7" -> {
                    System.out.println("\n👋 Goodbye! Thank you for using the Bank System.");
                    running = false;
                }
                default -> System.out.println("❌ Invalid choice. Please enter a number from 1 to 7.\n");
            }
        }

        scanner.close();
    }

    private static void printBanner() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║     Bank Transaction Management System       ║");
        System.out.println("║     Java 21 + JDBC + MySQL                   ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
    }
}
