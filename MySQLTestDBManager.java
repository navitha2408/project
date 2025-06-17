import java.sql.*;
import java.util.*;

public class MySQLTestDBManager {

    private static final String URL = "jdbc:mysql://localhost:3306/aits?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // change if needed
    private static final String PASSWORD = "Navitha@7955"; // change to your password

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            while (true) {
                System.out.println("\n Tables in 'aits':");
                listTables(conn);

                System.out.println("\nChoose an operation:");
                System.out.println("1. Create Table");
                System.out.println("2. Insert into Table");
                System.out.println("3. Update Table");
                System.out.println("4. Delete from Table");
                System.out.println("5. Exit");
                System.out.print("Enter choice (1-5): ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        createTableWithRetry(conn, scanner);
                        break;
                    case "2":
                        do {
                            insertIntoTable(conn, scanner);
                        } while (askRepeat(scanner));
                        break;
                    case "3":
                        do {
                            updateTable(conn, scanner);
                        } while (askRepeat(scanner));
                        break;
                    case "4":
                        do {
                            deleteFromTable(conn, scanner);
                        } while (askRepeat(scanner));
                        break;
                    case "5":
                        System.out.println(" Exiting.");
                        return;
                    default:
                        System.out.println(" Invalid choice.");
                }
            }
        } catch (SQLException e) {
            System.out.println(" Connection failed: " + e.getMessage());
        }
    }

    private static void listTables(Connection conn) throws SQLException {
        DatabaseMetaData dbMeta = conn.getMetaData();
        ResultSet tables = dbMeta.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"});

        int count = 0;
        while (tables.next()) {
            System.out.println(" - " + tables.getString("TABLE_NAME"));
            count++;
        }
        if (count == 0) System.out.println(" No tables found in 'aits'.");
    }

    private static void createTableWithRetry(Connection conn, Scanner scanner) {
        while (true) {
            System.out.print("Enter new table name: ");
            String tableName = scanner.nextLine();

            System.out.println(" Use valid MySQL types like: INT, VARCHAR(n), DATE");
            System.out.print("Enter column definitions (e.g., id INT PRIMARY KEY, name VARCHAR(50)): ");
            String columns = scanner.nextLine();

            String sql = "CREATE TABLE " + tableName + " (" + columns + ")";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println(" Table '" + tableName + "' created successfully.");
                break;
            } catch (SQLException e) {
                System.out.println(" SQL Error: " + e.getMessage());
                System.out.println(" Please re-enter the correct column definitions.");
            }
        }
    }

    private static void insertIntoTable(Connection conn, Scanner scanner) {
        try {
            System.out.print("Enter table name to insert into: ");
            String table = scanner.nextLine();

            String query = "SELECT * FROM " + table + " LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            List<String> cols = new ArrayList<>();
            List<String> vals = new ArrayList<>();

            for (int i = 1; i <= colCount; i++) {
                System.out.print("Enter value for " + meta.getColumnName(i) + ": ");
                cols.add(meta.getColumnName(i));
                vals.add(scanner.nextLine());
            }

            StringBuilder sql = new StringBuilder("INSERT INTO " + table + " (");
            sql.append(String.join(", ", cols)).append(") VALUES (");
            sql.append("?,".repeat(vals.size()));
            sql.setLength(sql.length() - 1);
            sql.append(")");

            PreparedStatement insertStmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < vals.size(); i++) {
                insertStmt.setString(i + 1, vals.get(i));
            }
            insertStmt.executeUpdate();
            System.out.println(" Record inserted successfully.");

        } catch (SQLException e) {
            System.out.println(" Insert failed: " + e.getMessage());
        }
    }

    private static void updateTable(Connection conn, Scanner scanner) {
        try {
            System.out.print("Enter table name to update: ");
            String table = scanner.nextLine();
            System.out.print("Enter column to update: ");
            String column = scanner.nextLine();
            System.out.print("Enter new value: ");
            String newVal = scanner.nextLine();
            System.out.print("Enter condition (e.g., id=1): ");
            String cond = scanner.nextLine();

            String sql = "UPDATE " + table + " SET " + column + " = ? WHERE " + cond;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newVal);
            int rows = stmt.executeUpdate();
            System.out.println(" " + rows + " row(s) updated.");
        } catch (SQLException e) {
            System.out.println(" Update failed: " + e.getMessage());
        }
    }

    private static void deleteFromTable(Connection conn, Scanner scanner) {
        try {
            System.out.print("Enter table name to delete from: ");
            String table = scanner.nextLine();
            System.out.print("Enter condition (e.g., id=1): ");
            String cond = scanner.nextLine();

            String sql = "DELETE FROM " + table + " WHERE " + cond;
            Statement stmt = conn.createStatement();
            int rows = stmt.executeUpdate(sql);
            System.out.println(" " + rows + " row(s) deleted.");
        } catch (SQLException e) {
            System.out.println(" Delete failed: " + e.getMessage());
        }
    }

    private static boolean askRepeat(Scanner scanner) {
        System.out.print(" Do you want to perform another operation of this type? (yes/no): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.startsWith("y");
    }
}