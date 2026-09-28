package com.bank.dao;

import com.bank.db.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void recordTransaction(Connection conn, int accountId,
                                  String type, BigDecimal amount,
                                  String description) throws SQLException {

        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, description) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, type);
            ps.setBigDecimal(3, amount);
            ps.setString(4, description);
            ps.executeUpdate();
        }
    }

    public List<String> getTransactionHistory(int accountId) throws SQLException {
        String sql = "SELECT transaction_id, transaction_type, amount, description, transaction_date " +
                     "FROM transactions " +
                     "WHERE account_id = ? " +
                     "ORDER BY transaction_date DESC";

        List<String> history = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String line = String.format(
                        "  [%d] %-14s  ₹%-10.2f  %-35s  %s",
                        rs.getInt("transaction_id"),
                        rs.getString("transaction_type"),
                        rs.getBigDecimal("amount"),
                        rs.getString("description"),
                        rs.getTimestamp("transaction_date").toLocalDateTime()
                    );
                    history.add(line);
                }
            }
        }

        return history;
    }
}
