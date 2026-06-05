package petadoption;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class WorkerMenu {
    private final Connection conn;
    private final Scanner scanner;

    public WorkerMenu(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            System.out.println();
            System.out.println("===== Worker Menu =====");
            System.out.println("1. Add worker");
            System.out.println("2. Search workers by role");
            System.out.println("3. Update worker salary");
            System.out.println("4. Add worker schedule");
            System.out.println("5. Show schedules by date");
            System.out.println("6. Delete worker schedule");
            System.out.println("7. Delete worker");
            System.out.println("0. Back");

            int choice = readInt("Choose menu: ");

            switch (choice) {
                case 1 -> addWorker();
                case 2 -> searchWorkersByRole();
                case 3 -> updateWorkerSalary();
                case 4 -> addWorkerSchedule();
                case 5 -> showSchedulesByDate();
                case 6 -> deleteWorkerSchedule();
                case 7 -> deleteWorker();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addWorker() {
        try {
            System.out.print("First name: ");
            String firstName = scanner.nextLine();

            System.out.print("Last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Phone: ");
            String phone = scanner.nextLine();

            System.out.print("Role (volunteer/vet/coordinator/admin/caretaker): ");
            String role = scanner.nextLine();

            System.out.print("Employment type (full_time/part_time/volunteer): ");
            String employmentType = scanner.nextLine();

            int shelterId = readInt("Shelter ID: ");

            System.out.print("Hire date (YYYY-MM-DD): ");
            String hireDate = scanner.nextLine();

            BigDecimal salary = readBigDecimal("Salary: ");

            String sql = """
                    INSERT INTO workers
                    (first_name, last_name, email, phone, role, employment_type, shelter_id, hire_date, salary)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, role);
            pstmt.setString(6, employmentType);
            pstmt.setInt(7, shelterId);
            pstmt.setDate(8, Date.valueOf(hireDate));
            pstmt.setBigDecimal(9, salary);

            int rows = pstmt.executeUpdate();
            System.out.println(rows + " worker inserted.");

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while adding worker: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
        }
    }

    private void searchWorkersByRole() {
        try {
            System.out.print("Role to search (volunteer/vet/coordinator/admin/caretaker): ");
            String role = scanner.nextLine();

            String sql = """
                    SELECT worker_id, first_name, last_name, role, employment_type,
                           salary, hire_date, shelter_name, shelter_city
                    FROM v_worker_salary
                    WHERE role = ?
                    ORDER BY last_name, first_name
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, role);

            ResultSet rs = pstmt.executeQuery();
            TablePrinter.print(rs);

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while searching workers: " + e.getMessage());
        }
    }

    private void updateWorkerSalary() {
        try {
            int workerId = readInt("Worker ID: ");
            BigDecimal newSalary = readBigDecimal("New salary: ");

            System.out.print("Reason: ");
            String reason = scanner.nextLine();

            String selectSql = """
                    SELECT salary
                    FROM workers
                    WHERE worker_id = ?
                    FOR UPDATE
                    """;

            String updateSql = """
                    UPDATE workers
                    SET salary = ?
                    WHERE worker_id = ?
                    """;

            String historySql = """
                    INSERT INTO salary_history
                    (worker_id, old_salary, new_salary, change_date, reason)
                    VALUES (?, ?, ?, CURDATE(), ?)
                    """;

            conn.setAutoCommit(false);

            try {
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                selectStmt.setInt(1, workerId);
                ResultSet rs = selectStmt.executeQuery();

                if (!rs.next()) {
                    conn.rollback();
                    System.out.println("Worker not found.");
                    rs.close();
                    selectStmt.close();
                    return;
                }

                BigDecimal oldSalary = rs.getBigDecimal("salary");

                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setBigDecimal(1, newSalary);
                updateStmt.setInt(2, workerId);
                updateStmt.executeUpdate();

                PreparedStatement historyStmt = conn.prepareStatement(historySql);
                historyStmt.setInt(1, workerId);
                historyStmt.setBigDecimal(2, oldSalary);
                historyStmt.setBigDecimal(3, newSalary);
                historyStmt.setString(4, reason);
                historyStmt.executeUpdate();

                conn.commit();

                rs.close();
                selectStmt.close();
                updateStmt.close();
                historyStmt.close();

                System.out.println("Worker salary updated successfully.");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Salary update failed. Transaction rolled back: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Database error while updating salary: " + e.getMessage());
        }
    }

    private void addWorkerSchedule() {
        try {
            int workerId = readInt("Worker ID: ");

            System.out.print("Work date (YYYY-MM-DD): ");
            String workDate = scanner.nextLine();

            System.out.print("Shift start (HH:MM:SS): ");
            String shiftStart = scanner.nextLine();

            System.out.print("Shift end (HH:MM:SS): ");
            String shiftEnd = scanner.nextLine();

            String sql = """
                    INSERT INTO schedules
                    (worker_id, work_date, shift_start, shift_end)
                    VALUES (?, ?, ?, ?)
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, workerId);
            pstmt.setString(2, workDate);
            pstmt.setString(3, shiftStart);
            pstmt.setString(4, shiftEnd);

            int rows = pstmt.executeUpdate();
            System.out.println(rows + " schedule inserted.");

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while adding schedule: " + e.getMessage());
        }
    }

    private void showSchedulesByDate() {
        try {
            System.out.print("Work date (YYYY-MM-DD): ");
            String workDate = scanner.nextLine();

            String sql = """
                    SELECT s.schedule_id, s.work_date, s.shift_start, s.shift_end,
                           w.worker_id, w.first_name, w.last_name, w.role
                    FROM schedules s
                    JOIN workers w ON s.worker_id = w.worker_id
                    WHERE s.work_date = ?
                    ORDER BY s.shift_start
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, workDate);

            ResultSet rs = pstmt.executeQuery();
            TablePrinter.print(rs);

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while showing schedules: " + e.getMessage());
        }
    }

    private void deleteWorkerSchedule() {
        try {
            int scheduleId = readInt("Schedule ID to delete: ");

            String sql = """
                    DELETE FROM schedules
                    WHERE schedule_id = ?
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, scheduleId);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Schedule deleted.");
            } else {
                System.out.println("No schedule found with that ID.");
            }

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while deleting schedule: " + e.getMessage());
        }
    }

    private void deleteWorker() {
        try {
            int workerId = readInt("Worker ID to delete: ");

            String sql = """
                    DELETE FROM workers
                    WHERE worker_id = ?
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, workerId);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Worker deleted.");
            } else {
                System.out.println("No worker found with that ID.");
            }

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while deleting worker. This worker may be used in another table: " + e.getMessage());
        }
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }

    private BigDecimal readBigDecimal(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}