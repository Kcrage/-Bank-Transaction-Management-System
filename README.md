# Bank Transaction Management System

A transactional banking application built with **Java 21**, **JDBC**, and **MySQL**, designed using the **Data Access Object (DAO)** pattern to decouple core business logic from persistence.

---

## Features

- **Account Management**: Create accounts and inspect real-time balances.
- **Deposit & Withdrawal**: Safe updates with balance validation.
- **Atomic Fund Transfers**: Programmatic JDBC transaction control (`commit`/`rollback`) ensuring ACID compliance and zero partial balance states.
- **Transaction History**: Audit trail tracking all deposits, withdrawals, and account-to-account transfers.

---

## Tech Stack

| Technology | Role |
|---|---|
| Java 21 | Core programming language |
| JDBC | Database connectivity & transaction control |
| MySQL | Relational database |
| Maven | Dependency & build management |

---

## Architecture & Project Structure

```
bank-management/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/bank/
    │   │   ├── BankApp.java              # Application entry point
    │   │   ├── dao/
    │   │   │   ├── AccountDAO.java       # Data access layer for accounts
    │   │   │   └── TransactionDAO.java   # Data access layer for transactions
    │   │   ├── db/
    │   │   │   └── DBConnection.java     # Connection provider
    │   │   ├── menu/
    │   │   │   └── BankMenu.java         # CLI interaction & business workflows
    │   │   └── model/
    │   │       ├── Account.java          # Account domain model
    │   │       └── Transaction.java      # Transaction domain model
    │   └── resources/
    │       └── db_setup.sql              # Database schema initialization
```

---

## Database Schema

### `accounts`
| Column | Type | Constraints |
|---|---|---|
| `account_id` | INT | PRIMARY KEY, AUTO_INCREMENT |
| `account_name` | VARCHAR(100) | NOT NULL |
| `balance` | DECIMAL(15, 2) | NOT NULL, CHECK (balance >= 0) |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

### `transactions`
| Column | Type | Constraints |
|---|---|---|
| `transaction_id` | INT | PRIMARY KEY, AUTO_INCREMENT |
| `account_id` | INT | FOREIGN KEY (accounts.account_id) ON DELETE CASCADE |
| `transaction_type`| ENUM | DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT |
| `amount` | DECIMAL(15, 2) | NOT NULL |
| `description` | VARCHAR(255) | |
| `transaction_date`| TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

---

## Setup & Running

### 1. Database Setup
Execute the initialization script in MySQL:
```bash
mysql -u root -p < src/main/resources/db_setup.sql
```

### 2. Configure Credentials
Set the following environment variables (or update defaults in `DBConnection.java`):
- `DB_URL` (default: `jdbc:mysql://localhost:3306/bank_system`)
- `DB_USER` (default: `root`)
- `DB_PASSWORD` (default: `your_password`)

### 3. Build & Run
Run via IntelliJ IDEA or Maven:
```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.bank.BankApp"
```

