# 🐱 Cat Adoption Center System

A text-based Java terminal application connected to a MySQL database for managing a cat adoption center. The system supports cat listings, adoptions, adopter management, worker schedules, fee tracking, and historical reporting.

---

## 📋 Features

- **Cat Management** — Add, search, and delete available cats
- **Fee Management** — Update adoption fees with full price history tracking (REQ13)
- **Adopter Management** — Full CRUD + demographic history snapshots (REQ14)
- **Adoption Transactions** — Create and view adoption records with price snapshots
- **Worker Management** — Manage workers, salaries, and schedules
- **Reports** — Salary reports, adoption summaries, demographic sales analysis

---

## 🛠️ Requirements

- Java 21 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

---

## 🗄️ Database Setup

Run the following in MySQL **in order**:

```sql
CREATE DATABASE cat_adoption_db;
SOURCE /path/to/sql/createschema.sql;
SOURCE /path/to/sql/initdata.sql;
```

To reset:
```sql
SOURCE /path/to/sql/dropschema.sql;
SOURCE /path/to/sql/createschema.sql;
SOURCE /path/to/sql/initdata.sql;
```

---

## ⚙️ Configuration

Open `src/main/java/petadoption/DBConnection.java` and set your credentials:

```java
String url      = "jdbc:mysql://localhost:3306/cat_adoption_db";
String user     = "root";
String password = "your_password";
```

---

## ▶️ How to Run

**Using Maven:**
```bash
mvn compile
mvn exec:java -Dexec.mainClass="petadoption.Main"
```

**Using JAR:**
```bash
java -cp pet-adoption-db-1.0-SNAPSHOT.jar petadoption.Main
```

---

## 📁 Project Structure

```
src/main/java/petadoption/
├── Main.java           # Entry point, main menu
├── DBConnection.java   # MySQL connection
├── CatMenu.java        # Cat management
├── FeeMenu.java        # Fee management (REQ13)
├── AdopterMenu.java    # Adopter management (REQ14)
├── AdoptionMenu.java   # Adoption transactions
├── WorkerMenu.java     # Worker management
├── ReportMenu.java     # Reports and analysis
├── TablePrinter.java   # Formats query results
└── InputHelper.java    # Shared input utilities

sql/
├── createschema.sql    # Creates tables, indexes, views
├── initdata.sql        # Sample data
└── dropschema.sql      # Drops all tables
```

---

## 🗂️ Database Schema

| Table | Description |
|-------|-------------|
| `cat` | Cat information and status |
| `adopter` | Adopter personal info |
| `shelter` | Shelter locations |
| `fee_schedule` | Adoption fee history (REQ13) |
| `adoption_transaction` | Adoption visit records |
| `adoption_basket_items` | Cats per transaction with price snapshot (REQ13) |
| `total_adoption_fees` | Computed totals per transaction |
| `medical_records` | Cat medical history |
| `euthanization_records` | Euthanization records |
| `salary_history` | Worker salary change history |
| `schedules` | Worker shift schedules |
| `workers` | Worker information |
| `adopter_history` | Demographic snapshots (REQ14) |

**Views:**
- `v_available_cats` — Available cats with current fee and medical visit count
- `v_worker_salary` — Paid workers joined with shelter info

---

## 👥 Team Members

| Name | Responsibility |
|------|---------------|

## 📚 Tech Stack

- **Language:** Java 21
- **Database:** MySQL 9.6
- **Connectivity:** JDBC (mysql-connector-j 9.3.0)
- **Build Tool:** Maven
- **IDE:** VS Code
