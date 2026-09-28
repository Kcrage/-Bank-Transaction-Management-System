package com.bank.dao;

import com.bank.db.DBConnection;
import com.bank.model.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

public class AccountDAO {

    public int createAccount(String accountName, BigDecimal initialBalance) throws SQLException {
        String sql = "INSERT INTO accounts (account_name, balance) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, accountName);
            ps.setBigDecimal(2, initialBalance);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("Account created but no ID was returned.");
            }
        }
    }

    public Optional<Account> findById(int accountId) throws SQLException {
        String sql = "SELECT account_id, account_name, balance, created_at FROM accounts WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Account(
                        rs.getInt("account_id"),
                        rs.getString("account_name"),
                        rs.getBigDecimal("balance"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public void updateBalance(Connection conn, int accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }
}
