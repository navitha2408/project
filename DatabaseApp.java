import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder; // NEW IMPORT
import javax.swing.table.DefaultTableCellRenderer; // NEW IMPORT
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DatabaseApp extends JFrame {

    private JRadioButton createRadio, insertRadio, updateRadio, selectRadio, deleteRadio,dropRadio,truncateRadio;
    private JPanel contentPanel;
    private ButtonGroup operationButtonGroup;

    private Connection connection;
    // --- MySQL Connection Details ---
    // IMPORTANT: Change 'localhost:3306' if your MySQL server is elsewhere or uses a different port.
    // IMPORTANT: Change 'testdb' to your actual database name in MySQL.
    private String DB_URL = "jdbc:mysql://localhost:3306/aits";
    private String DB_USER = "root"; // Your MySQL username
    private String DB_PASSWORD = "Navitha@7955"; // Your MySQL password

    // --- Define a Color Palette ---
    private final Color PRIMARY_BG = new Color(240, 240, 240); // Light Gray for general backgrounds
    private final Color SECONDARY_BG = new Color(220, 230, 240); // Slightly darker for panels
    private final Color HEADER_BG = new Color(60, 90, 120); // Dark Blue for headers
    private final Color HEADER_TEXT = Color.WHITE;
    private final Color BUTTON_PRIMARY = new Color(70, 130, 180); // Steel Blue
    private final Color BUTTON_HOVER = new Color(90, 150, 200); // Lighter Steel Blue
    private final Color BUTTON_TEXT = Color.WHITE;

    private final Color SUCCESS_COLOR = new Color(46, 204, 113); // Green
    private final Color WARNING_COLOR = new Color(255, 165, 0); // Orange
    private final Color ERROR_COLOR = new Color(231, 76, 60); // Red

    // Specific button colors for operations
    private final Color CREATE_BTN_COLOR = new Color(39, 174, 96); // Emerald Green
    private final Color INSERT_BTN_COLOR = new Color(52, 152, 219); // Peter River Blue
    private final Color UPDATE_BTN_COLOR = new Color(241, 196, 15); // Sunflower Yellow
    private final Color DELETE_BTN_COLOR = new Color(192, 57, 43); // Alizarin Red (darker)
    private final Color DELETE_SELECTED_BTN_COLOR = new Color(231, 76, 60); // Alizarin Red (lighter for table delete)
	private final Color DROP_BTN_COLOR = new Color(231, 76, 60);
	private final Color TRUNCATE_BTN_COLOR=new Color(241,196,15);
    
  
    public DatabaseApp() {
        setTitle("Universal MySQL Database Manager (Java)");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        connectToDatabase();
        createDefaultTable(); // Optional: create a sample table on startup

        setLayout(new BorderLayout());
        getContentPane().setBackground(PRIMARY_BG); // Set main content pane background

        // --- Radio Buttons Panel ---
        JPanel radioPanel = new JPanel();
        radioPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(HEADER_BG, 2), // Border color
            "Choose Operation", // Title text
            SwingConstants.CENTER, TitledBorder.TOP, // Position
            new Font("Arial", Font.BOLD, 14), // Font
            HEADER_BG // Text color for title
        ));
        radioPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Add horizontal padding
        radioPanel.setBackground(SECONDARY_BG); // Panel background

        operationButtonGroup = new ButtonGroup();

        createRadio = new JRadioButton("Create");
        insertRadio = new JRadioButton("Insert");
        updateRadio = new JRadioButton("Update");
        selectRadio = new JRadioButton("Select");
        deleteRadio = new JRadioButton("Delete");
		dropRadio   = new JRadioButton("Drop");
		truncateRadio=new JRadioButton("Truncate");

        // Pass the radioPanel directly to the method
        addRadioButtons(radioPanel, createRadio, insertRadio, updateRadio, selectRadio, deleteRadio,dropRadio);

        // Set initial selection and trigger action
        selectRadio.setSelected(true);
        selectRadio.addActionListener(e -> showOperationMenu()); // Add action listener here
        add(radioPanel, BorderLayout.NORTH); // Add radioPanel to the JFrame

        // --- Content Panel (for dynamic UI) ---
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout()); // Use BorderLayout for dynamic content
        contentPanel.setBorder(BorderFactory.createEtchedBorder(ERROR_COLOR, WARNING_COLOR)); // More visible border
        contentPanel.setBackground(PRIMARY_BG); // Content panel background
        add(contentPanel, BorderLayout.CENTER);

        showOperationMenu(); // Display initial content

        // Add a window listener to close the connection on exit
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (connection != null) {
                    try {
                        connection.close();
                        System.out.println("Database connection closed.");
                    } catch (SQLException e) {
                        System.err.println("Error closing database connection: " + e.getMessage());
                    }
                }
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private void addRadioButtons(JPanel panelToAddButtonsTo, JRadioButton... radios) {
        for (JRadioButton radio : radios) {
            radio.setFont(new Font("Arial", Font.BOLD, 14));
            radio.setBackground(SECONDARY_BG); // Set radio button background
            radio.setForeground(HEADER_BG); // Set text color
            radio.setFocusPainted(false); // Remove border around text when focused
            radio.addActionListener(e -> showOperationMenu());
            operationButtonGroup.add(radio);
            panelToAddButtonsTo.add(radio);
        }
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to MySQL database successfully.");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL JDBC driver not found. Ensure mysql-connector-j-x.x.x.jar is in your classpath.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to MySQL database:\n" + e.getMessage() + "\nCheck URL, user, password, and if MySQL server is running.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createDefaultTable() {
        String query = "CREATE TABLE IF NOT EXISTS students (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(255) NOT NULL," +
                        "age INT" +
                        ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(query);
            System.out.println("Default 'students' table ensured.");
        } catch (SQLException e) {
            System.err.println("Error creating default table: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error creating default 'students' table: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearContentPanel() {
        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showOperationMenu() {
        clearContentPanel();
        String selectedOperation = "";

        for (JRadioButton radio : new JRadioButton[]{createRadio, insertRadio, updateRadio, selectRadio, deleteRadio,dropRadio,truncateRadio}) {
            if (radio.isSelected()) {
                selectedOperation = radio.getText();
                break;
            }
        }

        switch (selectedOperation) {
            case "Create":
                createTableMenu();
                break;
            case "Insert":
                insertMenu();
                break;
            case "Update":
                updateMenu();
                break;
            case "Select":
                selectMenu();
                break;
            case "Delete":
                deleteSpecificMenu();
                break;
			case "Drop":
                dropMenu();       
                break;
            case "Truncate":
                truncateMenu();   
                break;	
        }
    }

    // --- Utility: Table Selection Dropdown ---
    private JPanel createTableSelectionPanel(ActionListener callback) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(SECONDARY_BG); // Background for table selection panel
        panel.add(new JLabel("Select Table:"));

        List<String> tableNames = getTableNames();
        if (tableNames.isEmpty()) {
            panel.add(new JLabel("No tables found. Create one first!"));
            return panel;
        }

        String[] tablesArray = tableNames.toArray(new String[0]);
        JComboBox<String> tableComboBox = new JComboBox<>(tablesArray);
        tableComboBox.setBackground(Color.WHITE); // White background for combo box
        tableComboBox.setForeground(BUTTON_PRIMARY); // Text color for combo box
        tableComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        tableComboBox.setSelectedItem(tablesArray[0]);
        tableComboBox.addActionListener(callback);
        panel.add(tableComboBox);

        SwingUtilities.invokeLater(() -> callback.actionPerformed(
                new ActionEvent(tableComboBox, ActionEvent.ACTION_PERFORMED, "initialSelection")
        ));

        return panel;
    }

    private List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        String catalog = null;
        try {
            String url = connection.getMetaData().getURL();
            if (url.contains("/")) {
                catalog = url.substring(url.lastIndexOf("/") + 1);
                if (catalog.contains("?")) {
                    catalog = catalog.substring(0, catalog.indexOf("?"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Could not extract catalog name from URL: " + e.getMessage());
        }

        try (ResultSet rs = connection.getMetaData().getTables(catalog, null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error getting table names: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return tableNames;
    }

    private List<String> getColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<>();
        try (ResultSet rs = connection.getMetaData().getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                columnNames.add(rs.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error getting column names for " + tableName + ": " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return columnNames;
    }

    /**
     * Helper method to get the primary key column name for a given table.
     * Returns null if no primary key is found or if an error occurs.
     */
    private String getPrimaryKeyColumnName(String tableName) {
        try (ResultSet rs = connection.getMetaData().getPrimaryKeys(null, null, tableName)) {
            if (rs.next()) {
                return rs.getString("COLUMN_NAME");
            }
        } catch (SQLException ex) {
            System.err.println("Error getting primary key metadata for " + tableName + ": " + ex.getMessage());
        }
        return null; // No primary key found or error
    }

    // --- Create Table UI ---
    private void createTableMenu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PRIMARY_BG); // Set panel background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create New Table");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger, bolder title
        titleLabel.setForeground(HEADER_BG); // Title color
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Table Name:"), gbc);
        JTextField tableNameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(tableNameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Column Definitions (e.g., id INT PRIMARY KEY, name VARCHAR(255)):"), gbc);
        gbc.gridy++;
        JTextArea columnsDefArea = new JTextArea(5, 40);
        columnsDefArea.setLineWrap(true);
        columnsDefArea.setWrapStyleWord(true);
        columnsDefArea.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Border for text area
        JScrollPane scrollPane = new JScrollPane(columnsDefArea);
        panel.add(scrollPane, gbc);

        // --- Foreign Key Section ---
        gbc.gridy++;
        JCheckBox addFkCheckbox = new JCheckBox("Add Foreign Key Constraint");
        addFkCheckbox.setBackground(PRIMARY_BG); // Checkbox background
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(addFkCheckbox, gbc);

        JPanel fkPanel = new JPanel(new GridBagLayout());
        fkPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BUTTON_PRIMARY), // Border for FK panel
            "Foreign Key Details",
            SwingConstants.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            BUTTON_PRIMARY
        ));
        fkPanel.setBackground(SECONDARY_BG); // FK panel background
        fkPanel.setVisible(false); // Hidden by default
        GridBagConstraints fkGbc = new GridBagConstraints();
        fkGbc.insets = new Insets(2, 5, 2, 5);
        fkGbc.fill = GridBagConstraints.HORIZONTAL;

        fkGbc.gridx = 0;
        fkGbc.gridy = 0;
        fkPanel.add(new JLabel("Referencing Column (in this new table):"), fkGbc);
        fkGbc.gridx = 1;
        JTextField referencingColumnField = new JTextField(15);
        fkPanel.add(referencingColumnField, fkGbc);

        fkGbc.gridy++;
        fkGbc.gridx = 0;
        fkPanel.add(new JLabel("Referenced Table:"), fkGbc);
        fkGbc.gridx = 1;
        JComboBox<String> referencedTableComboBox = new JComboBox<>(getTableNames().toArray(new String[0]));
        referencedTableComboBox.setBackground(Color.WHITE);
        referencedTableComboBox.setForeground(BUTTON_PRIMARY);
        fkPanel.add(referencedTableComboBox, fkGbc);

        fkGbc.gridy++;
        fkGbc.gridx = 0;
        fkPanel.add(new JLabel("Referenced Primary Key Column:"), fkGbc);
        fkGbc.gridx = 1;
        JTextField referencedPkColumnField = new JTextField(15);
        fkPanel.add(referencedPkColumnField, fkGbc);

        // Listener to auto-fill referenced primary key column
        referencedTableComboBox.addActionListener(e -> {
            String selectedTable = (String) referencedTableComboBox.getSelectedItem();
            if (selectedTable != null && !selectedTable.isEmpty()) {
                String pkCol = getPrimaryKeyColumnName(selectedTable);
                if (pkCol != null) {
                    referencedPkColumnField.setText(pkCol);
                } else {
                    referencedPkColumnField.setText("");
                    JOptionPane.showMessageDialog(this, "No primary key found for table '" + selectedTable + "'. Please enter manually.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                referencedPkColumnField.setText("");
            }
        });
        // Trigger initial selection to populate PK field if a table is pre-selected
        if (referencedTableComboBox.getItemCount() > 0) {
            referencedTableComboBox.setSelectedIndex(0);
        }


        addFkCheckbox.addActionListener(e -> fkPanel.setVisible(addFkCheckbox.isSelected()));

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(fkPanel, gbc);
        // --- End Foreign Key Section ---

        gbc.gridy++;
        JButton createButton = new JButton("Create Table");
        createButton.setFont(new Font("Arial", Font.BOLD, 14));
        createButton.setBackground(CREATE_BTN_COLOR); // Green for create
        createButton.setForeground(BUTTON_TEXT); // White text
        createButton.setFocusPainted(false); // Remove border on click
        createButton.addActionListener(e -> {
            String tableName = tableNameField.getText().trim();
            String columnsDef = columnsDefArea.getText().trim();

            if (tableName.isEmpty() || columnsDef.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Table name and column definitions cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!tableName.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                   JOptionPane.showMessageDialog(this, "Invalid table name. Use letters, numbers, and underscores, starting with a letter or underscore.", "Input Error", JOptionPane.WARNING_MESSAGE);
                   return;
            }

            try (Statement stmt = connection.createStatement()) {
                String createTableQuery = "CREATE TABLE " + tableName + " (" + columnsDef + ");";
                stmt.execute(createTableQuery);
                JOptionPane.showMessageDialog(this, "Table '" + tableName + "' created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // --- Handle Foreign Key Creation ---
                if (addFkCheckbox.isSelected()) {
                    String referencingCol = referencingColumnField.getText().trim();
                    String referencedTable = (String) referencedTableComboBox.getSelectedItem();
                    String referencedPkCol = referencedPkColumnField.getText().trim();

                    if (referencingCol.isEmpty() || referencedTable == null || referencedTable.isEmpty() || referencedPkCol.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Foreign Key details cannot be empty if checkbox is selected.", "Input Error", JOptionPane.WARNING_MESSAGE);
                        // Consider rolling back table creation or giving user an option to fix.
                        // For now, we'll just return and let the user try to fix.
                        return;
                    }

                    // Validate if the referencing column exists in the newly created table
                    List<String> newTableColumns = getColumnNames(tableName);
                    if (!newTableColumns.contains(referencingCol)) {
                        JOptionPane.showMessageDialog(this, "Referencing column '" + referencingCol + "' does not exist in the newly created table '" + tableName + "'.", "FK Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Validate if the referenced primary key column exists in the referenced table
                    List<String> refTableColumns = getColumnNames(referencedTable);
                     if (!refTableColumns.contains(referencedPkCol)) {
                        JOptionPane.showMessageDialog(this, "Referenced primary key column '" + referencedPkCol + "' does not exist in the referenced table '" + referencedTable + "'.", "FK Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }


                    String addFkQuery = "ALTER TABLE " + tableName +
                                        " ADD CONSTRAINT fk_" + tableName + "_" + referencingCol +
                                        " FOREIGN KEY (" + referencingCol + ")" +
                                        " REFERENCES " + referencedTable + "(" + referencedPkCol + ");";
                    try {
                        stmt.execute(addFkQuery);
                        JOptionPane.showMessageDialog(this, "Foreign Key added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException fkEx) {
                        JOptionPane.showMessageDialog(this, "Failed to add Foreign Key: " + fkEx.getMessage() + "\nEnsure data types match and referenced PK exists.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                // --- End Foreign Key Handling ---

                tableNameField.setText("");
                columnsDefArea.setText("");
                referencingColumnField.setText("");
                referencedPkColumnField.setText("");
                addFkCheckbox.setSelected(false);
                fkPanel.setVisible(false);
                showOperationMenu(); // Refresh menu to update table list
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to create table: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(createButton, gbc);

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // --- Insert Data UI ---
    private void insertMenu() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PRIMARY_BG); // Main panel background
        JLabel titleLabel = new JLabel("Insert Data into Table", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger, bolder title
        titleLabel.setForeground(HEADER_BG); // Title color
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel dynamicFieldsPanelContainer = new JPanel(new BorderLayout());
        dynamicFieldsPanelContainer.setBackground(PRIMARY_BG); // Container background
        JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Border for scroll pane
        dynamicFieldsPanelContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel dynamicFieldsPanel = new JPanel();
        dynamicFieldsPanel.setLayout(new BoxLayout(dynamicFieldsPanel, BoxLayout.Y_AXIS));
        dynamicFieldsPanel.setBackground(PRIMARY_BG); // Dynamic fields background
        scrollPane.setViewportView(dynamicFieldsPanel);

        ActionListener tableSelectionListener = e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            String tableName = (String) cb.getSelectedItem();
            if (tableName != null) {
                showInsertFields(tableName, dynamicFieldsPanel);
            }
        };

        JPanel tableSelectPanel = createTableSelectionPanel(tableSelectionListener);
        mainPanel.add(tableSelectPanel, BorderLayout.NORTH);

        mainPanel.add(dynamicFieldsPanelContainer, BorderLayout.CENTER);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showInsertFields(String tableName, JPanel dynamicFieldsPanel) {
        dynamicFieldsPanel.removeAll();
        dynamicFieldsPanel.revalidate();
        dynamicFieldsPanel.repaint();

        List<String> columnNames = getColumnNames(tableName);
        if (columnNames.isEmpty()) {
            dynamicFieldsPanel.add(new JLabel("No columns found for this table."));
            return;
        }

        List<JTextField> inputFields = new ArrayList<>();
        List<String> colsToInsert = new ArrayList<>();

        for (String colName : columnNames) {
            boolean isAutoIncrement = false;
            try (ResultSet rs = connection.getMetaData().getColumns(null, null, tableName, colName)) {
                if (rs.next()) {
                    if (rs.getString("IS_AUTOINCREMENT") != null && rs.getString("IS_AUTOINCREMENT").equalsIgnoreCase("YES")) {
                        isAutoIncrement = true;
                    }
                }
            } catch (SQLException ex) {
                System.err.println("Error checking auto-increment property: " + ex.getMessage());
            }

            if (isAutoIncrement) {
                System.out.println("Skipping auto-increment column for input: " + colName);
                continue;
            }

            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowPanel.setBackground(PRIMARY_BG); // Row panel background
            JLabel label = new JLabel(colName + ":");
            label.setPreferredSize(new Dimension(150, 25));
            JTextField textField = new JTextField(25);
            textField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Text field border
            rowPanel.add(label);
            rowPanel.add(textField);
            dynamicFieldsPanel.add(rowPanel);
            inputFields.add(textField);
            colsToInsert.add(colName);
        }

        JButton insertButton = new JButton("Insert Record");
        insertButton.setFont(new Font("Arial", Font.BOLD, 14));
        insertButton.setBackground(INSERT_BTN_COLOR); // Blue for insert
        insertButton.setForeground(BUTTON_TEXT);
        insertButton.setFocusPainted(false);
        insertButton.addActionListener(e -> {
            List<Object> values = new ArrayList<>();
            for (JTextField field : inputFields) {
                values.add(field.getText());
            }

            if (values.stream().allMatch(s -> (s instanceof String && ((String) s).isEmpty()))) {
                   JOptionPane.showMessageDialog(this, "Please enter values for insertion.", "Input Error", JOptionPane.WARNING_MESSAGE);
                   return;
            }

            String columns = String.join(", ", colsToInsert);
            String placeholders = String.join(", ", java.util.Collections.nCopies(values.size(), "?"));
            String query = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ");";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i) == null || (values.get(i) instanceof String && ((String) values.get(i)).isEmpty())) {
                        pstmt.setNull(i + 1, Types.VARCHAR); // Set as NULL for empty strings
                    } else {
                        pstmt.setObject(i + 1, values.get(i));
                    }
                }
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Record inserted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                for (JTextField field : inputFields) {
                    field.setText("");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to insert record: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(PRIMARY_BG); // Button panel background
        buttonPanel.add(insertButton);
        dynamicFieldsPanel.add(buttonPanel);
        dynamicFieldsPanel.revalidate();
        dynamicFieldsPanel.repaint();
    }

    // --- Update Data UI ---
    private void updateMenu() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PRIMARY_BG); // Main panel background
        JLabel titleLabel = new JLabel("Update Data in Table", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger, bolder title
        titleLabel.setForeground(HEADER_BG); // Title color
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(PRIMARY_BG); // Input panel background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField setClauseField = new JTextField(40);
        JTextField whereClauseField = new JTextField(40);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("SET clause (e.g., age = 21, name = 'JOHN DOE'):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(setClauseField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        inputPanel.add(new JLabel("WHERE clause (optional, e.g., id = 1 AND name = 'Alice'):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(whereClauseField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        // Important: Remind user to quote strings
        JLabel quoteReminder = new JLabel("<html><font color='" + ERROR_COLOR.getRed() + "," + ERROR_COLOR.getGreen() + "," + ERROR_COLOR.getBlue() + "'><b>IMPORTANT:</b> Quote string values in clauses (e.g., 'text').</font></html>");
        inputPanel.add(quoteReminder, gbc);


        gbc.gridy++;
        JButton updateButton = new JButton("Update Records");
        updateButton.setFont(new Font("Arial", Font.BOLD, 14));
        updateButton.setBackground(UPDATE_BTN_COLOR); // Yellow for update
        updateButton.setForeground(BUTTON_TEXT);
        updateButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        inputPanel.add(updateButton, gbc);

        updateButton.addActionListener(e -> {
            String tableName = null;
            // Find the selected table from the JComboBox in the mainPanel's top component
            // (Assuming tableSelectPanel is still the first component)
            if (mainPanel.getComponent(0) instanceof JPanel) {
                JPanel tableSelectPanel = (JPanel) mainPanel.getComponent(0);
                for (Component comp : tableSelectPanel.getComponents()) {
                    if (comp instanceof JComboBox) {
                        tableName = ((JComboBox<String>) comp).getSelectedItem().toString();
                        break;
                    }
                }
            }

            if (tableName == null) {
                JOptionPane.showMessageDialog(this, "Table not selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String setClause = setClauseField.getText().trim();
            String whereClause = whereClauseField.getText().trim();

            if (setClause.isEmpty()) {
                JOptionPane.showMessageDialog(this, "SET clause cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String query = "UPDATE " + tableName + " SET " + setClause;
            if (!whereClause.isEmpty()) {
                query += " WHERE " + whereClause;
            }
            query += ";";

            try (Statement stmt = connection.createStatement()) {
                int affectedRows = stmt.executeUpdate(query);
                JOptionPane.showMessageDialog(this, affectedRows + " record(s) updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                setClauseField.setText("");
                whereClauseField.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to update record(s): " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        ActionListener tableSelectionListener = e -> {}; // Dummy listener, actual logic depends on how you structure it
        JPanel tableSelectPanel = createTableSelectionPanel(tableSelectionListener);
        mainPanel.add(tableSelectPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // --- Select Data and Delete Multiple UI ---
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private List<Object> recordIds; // Stores the actual primary key values

    private void selectMenu() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PRIMARY_BG); // Main panel background
        JLabel titleLabel = new JLabel("View Records & Delete Multiple", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger, bolder title
        titleLabel.setForeground(HEADER_BG); // Title color
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        ActionListener tableSelectionListener = e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            String tableName = (String) cb.getSelectedItem();
            if (tableName != null) {
                showRecordsForSelection(tableName, mainPanel);
            }
        };

        JPanel tableSelectPanel = createTableSelectionPanel(tableSelectionListener);
        mainPanel.add(tableSelectPanel, BorderLayout.NORTH); // Keep table selection at top

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showRecordsForSelection(String tableName, JPanel mainPanel) {
        // Remove existing table and related components before adding new ones
        for (Component comp : mainPanel.getComponents()) {
            // Check if it's a JScrollPane (the table) or a JPanel that was part of previous table display
            if (comp instanceof JScrollPane || (comp instanceof JPanel && comp.getName() != null && comp.getName().equals("tableDisplayPanel"))) {
                mainPanel.remove(comp);
            }
        }
        // Remove any old table title as well, but NOT the main operation title or table selection panel
        Component[] mainPanelComps = mainPanel.getComponents();
        for(Component comp : mainPanelComps) {
            if (comp instanceof JLabel && ((JLabel)comp).getText().startsWith("Records in '")) {
                mainPanel.remove(comp);
                break;
            }
        }

        JLabel tableRecordsTitle = new JLabel("Records in '" + tableName + "':", SwingConstants.CENTER);
        tableRecordsTitle.setFont(new Font("Arial", Font.ITALIC, 14));
        tableRecordsTitle.setForeground(HEADER_BG); // Color for table records title
        mainPanel.add(tableRecordsTitle, BorderLayout.CENTER); // Add the title for the table (temporarily in center, will be moved by subsequent add)


        List<String> columnNames = getColumnNames(tableName);
        if (columnNames.isEmpty()) {
            mainPanel.add(new JLabel("No columns found for this table."), BorderLayout.SOUTH);
            mainPanel.revalidate();
            mainPanel.repaint();
            return;
        }

        Vector<Vector<Object>> data = new Vector<>();
        Vector<String> header = new Vector<>();
        recordIds = new ArrayList<>(); // Initialize recordIds here

        header.add("[Del]"); // Checkbox column
        for (String col : columnNames) {
            header.add(col.toUpperCase()); // Display column names in uppercase
        }

        final String primaryKeyColumnName = getPrimaryKeyColumnName(tableName); // Make PK name final
        final int idColumnIndex; // Declare as final here

        if (primaryKeyColumnName != null) {
            idColumnIndex = columnNames.indexOf(primaryKeyColumnName);
        } else {
            // If no explicit primary key found, check if there are columns to default to the first one
            // This ensures idColumnIndex gets a value in all paths for 'final'
            if (!columnNames.isEmpty()) {
                idColumnIndex = 0; // Fallback to first column if no explicit PK
                System.out.println("Using first column '" + columnNames.get(0) + "' as ID for deletion (no explicit primary key found).");
            } else {
                idColumnIndex = -1; // No columns at all
            }
        }

        if (idColumnIndex == -1) { // Check if we couldn't find a usable ID column
            JOptionPane.showMessageDialog(this, "No primary key identified for table '" + tableName + "'. Cannot enable direct deletion from table view.", "Warning", JOptionPane.WARNING_MESSAGE);
            // The delete button will be disabled below
        }


        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(false); // Checkbox for deletion
                for (int i = 0; i < columnNames.size(); i++) {
                    Object value = rs.getObject(i + 1);
                    row.add(value != null ? value.toString().toUpperCase() : "NULL");
                }
                data.add(row);
                // Store the actual primary key value for deletion
                if (idColumnIndex != -1 && idColumnIndex < columnNames.size()) { // Add bounds check
                    recordIds.add(rs.getObject(idColumnIndex + 1)); // JDBC ResultSet is 1-indexed
                } else {
                    recordIds.add(null); // No PK, cannot store ID
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching records: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (data.isEmpty()) {
            mainPanel.add(new JLabel("No records found in this table."), BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
            return;
        }

        tableModel = new DefaultTableModel(data, header) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 && idColumnIndex != -1; // Correctly using final idColumnIndex
            }
        };

        dataTable = new JTable(tableModel);
        dataTable.setFillsViewportHeight(true);
        dataTable.setFont(new Font("Arial", Font.PLAIN, 12));
        dataTable.setRowHeight(25);
        dataTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13)); // Header font size
        dataTable.getTableHeader().setBackground(HEADER_BG); // Header background
        dataTable.getTableHeader().setForeground(HEADER_TEXT); // Header text color
        dataTable.setGridColor(Color.LIGHT_GRAY); // Grid lines
        dataTable.setSelectionBackground(new Color(173, 216, 230)); // Light blue selection
        dataTable.setSelectionForeground(Color.BLACK); // Black text on selection

        // Set alternating row colors (optional, but nice)
        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 255)); // White and GhostWhite
                }
                return c;
            }
        });


        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Border for table scroll pane


        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(PRIMARY_BG); // Bottom panel background
        JButton deleteSelectedButton = new JButton("Delete Selected Records");
        deleteSelectedButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteSelectedButton.setBackground(DELETE_SELECTED_BTN_COLOR); // Red for delete selected
        deleteSelectedButton.setForeground(BUTTON_TEXT);
        deleteSelectedButton.setFocusPainted(false);
        deleteSelectedButton.setEnabled(idColumnIndex != -1); // Enable only if PK exists

        final String finalTableName = tableName; // tableName is already effectively final, just for clarity

        deleteSelectedButton.addActionListener(e -> {
            if (primaryKeyColumnName == null || primaryKeyColumnName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cannot delete: No primary key column identified for '" + finalTableName + "'.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Object> idsToDelete = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
                if (isSelected && recordIds.get(i) != null) {
                    idsToDelete.add(recordIds.get(i));
                }
            }

            if (idsToDelete.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No records selected for deletion.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                            "Are you sure you want to delete " + idsToDelete.size() + " selected record(s)? This action is irreversible.",
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                StringBuilder placeholders = new StringBuilder();
                for (int i = 0; i < idsToDelete.size(); i++) {
                    placeholders.append("?");
                    if (i < idsToDelete.size() - 1) {
                        placeholders.append(",");
                    }
                }
                String deleteQuery = "DELETE FROM " + finalTableName + " WHERE " + primaryKeyColumnName + " IN (" + placeholders.toString() + ");";

                try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
                    for (int i = 0; i < idsToDelete.size(); i++) {
                        pstmt.setObject(i + 1, idsToDelete.get(i));
                    }
                    int deletedCount = pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, deletedCount + " record(s) deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    showRecordsForSelection(finalTableName, mainPanel); // Refresh the table
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to delete records: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        bottomPanel.add(deleteSelectedButton);

        // Create a dedicated panel to hold the table and its title for better layout
        JPanel tableDisplayPanel = new JPanel(new BorderLayout());
        tableDisplayPanel.setName("tableDisplayPanel"); // Give it a name for easy removal
        tableDisplayPanel.setBackground(PRIMARY_BG);
        tableDisplayPanel.add(tableRecordsTitle, BorderLayout.NORTH); // Title at top of table display
        tableDisplayPanel.add(scrollPane, BorderLayout.CENTER); // Table in center of table display
        tableDisplayPanel.add(bottomPanel, BorderLayout.SOUTH); // Delete button at the bottom of table display

        mainPanel.add(tableDisplayPanel, BorderLayout.CENTER); // Add the entire table display to the main panel's center

        mainPanel.revalidate();
        mainPanel.repaint();
    }


    // --- Dedicated Delete Menu (Delete by WHERE clause) ---
    private void deleteSpecificMenu() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PRIMARY_BG); // Main panel background
        JLabel titleLabel = new JLabel("Delete Records by Criteria", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Larger, bolder title
        titleLabel.setForeground(HEADER_BG); // Title color
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(PRIMARY_BG); // Input panel background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField whereClauseField = new JTextField(40);
        whereClauseField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Text field border


        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("WHERE clause (e.g., age > 30 OR name = 'John Doe'):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(whereClauseField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel warningLabel = new JLabel("Leave blank to delete ALL records in the table (DANGEROUS!).");
        warningLabel.setForeground(WARNING_COLOR); // Orange for warning
        warningLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        inputPanel.add(warningLabel, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel quoteReminder = new JLabel("<html><font color='" + ERROR_COLOR.getRed() + "," + ERROR_COLOR.getGreen() + "," + ERROR_COLOR.getBlue() + "'><b>IMPORTANT:</b> Quote string values in WHERE clause (e.g., 'text').</font></html>");
        inputPanel.add(quoteReminder, gbc);


        gbc.gridy++;
        JButton deleteButton = new JButton("Delete Records");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setBackground(DELETE_BTN_COLOR); // Darker red for main delete button
        deleteButton.setForeground(BUTTON_TEXT);
        deleteButton.setFocusPainted(false);
        inputPanel.add(deleteButton, gbc);

        deleteButton.addActionListener(e -> {
            String tableName = null;
            // Find the selected table from the JComboBox in the mainPanel's top component
            if (mainPanel.getComponent(0) instanceof JPanel) {
                JPanel tableSelectPanel = (JPanel) mainPanel.getComponent(0);
                for (Component comp : tableSelectPanel.getComponents()) {
                    if (comp instanceof JComboBox) {
                        tableName = ((JComboBox<String>) comp).getSelectedItem().toString();
                        break;
                    }
                }
            }

            if (tableName == null) {
                JOptionPane.showMessageDialog(this, "Table not selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String whereClause = whereClauseField.getText().trim();
            String query = "DELETE FROM " + tableName;
            boolean deleteAll = false;

            if (!whereClause.isEmpty()) {
                query += " WHERE " + whereClause;
            } else {
                int confirmAll = JOptionPane.showConfirmDialog(this,
                                "You are about to delete ALL records from '" + tableName + "'. This is irreversible. Are you sure?",
                                "Confirm ALL Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirmAll != JOptionPane.YES_OPTION) {
                    return; // User cancelled
                }
                deleteAll = true;
            }
            query += ";";

            int confirm = JOptionPane.showConfirmDialog(this,
                            (deleteAll ? "This will delete ALL records." : "Are you sure you want to delete records matching the WHERE clause?"),
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Statement stmt = connection.createStatement()) {
                    int affectedRows = stmt.executeUpdate(query);
                    JOptionPane.showMessageDialog(this, affectedRows + " record(s) deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    whereClauseField.setText("");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to delete records: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        ActionListener tableSelectionListener = e -> {}; // Dummy listener for table combo
        JPanel tableSelectPanel = createTableSelectionPanel(tableSelectionListener);
        mainPanel.add(tableSelectPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
	private void dropMenu() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(PRIMARY_BG);

    JLabel titleLabel = new JLabel("Drop Table", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    titleLabel.setForeground(DROP_BTN_COLOR);
    mainPanel.add(titleLabel, BorderLayout.NORTH);

    ActionListener dropAction = e -> {
        JComboBox<String> cb = (JComboBox<String>) ((JButton) e.getSource()).getClientProperty("tableComboBox");
        String tableName = (String) cb.getSelectedItem();
        if (tableName != null) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to DROP the table '" + tableName + "'?\nThis action is irreversible!",
                    "Confirm Drop", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName);
                    JOptionPane.showMessageDialog(null, "✅ Table '" + tableName + "' dropped successfully.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "❌ Error: " + ex.getMessage());
                }
            }
        }
    };

    JPanel tableSelectPanel = createTableSelectionPanel(null);
    JComboBox<String> tableComboBox = (JComboBox<String>) tableSelectPanel.getComponent(1);

    JButton dropBtn = new JButton("Drop Table");
    dropBtn.setBackground(DROP_BTN_COLOR);
    dropBtn.setForeground(Color.WHITE);
    dropBtn.putClientProperty("tableComboBox", tableComboBox);
    dropBtn.addActionListener(dropAction);

    JPanel actionPanel = new JPanel();
    actionPanel.setBackground(PRIMARY_BG);
    actionPanel.add(dropBtn);

    mainPanel.add(tableSelectPanel, BorderLayout.CENTER);
    mainPanel.add(actionPanel, BorderLayout.SOUTH);

    contentPanel.add(mainPanel, BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
   }
   private void truncateMenu() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(PRIMARY_BG);

    JLabel titleLabel = new JLabel("Truncate Table", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    titleLabel.setForeground(TRUNCATE_BTN_COLOR);
    mainPanel.add(titleLabel, BorderLayout.NORTH);

    ActionListener truncateAction = e -> {
        JComboBox<String> cb = (JComboBox<String>) ((JButton) e.getSource()).getClientProperty("tableComboBox");
        String tableName = (String) cb.getSelectedItem();
        if (tableName != null) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to TRUNCATE the table '" + tableName + "'?\nAll rows will be deleted!",
                    "Confirm Truncate", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("TRUNCATE TABLE " + tableName);
                    JOptionPane.showMessageDialog(null, "✅ Table '" + tableName + "' truncated successfully.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "❌ Error: " + ex.getMessage());
                }
            }
        }
    };

    JPanel tableSelectPanel = createTableSelectionPanel(null);
    JComboBox<String> tableComboBox = (JComboBox<String>) tableSelectPanel.getComponent(1);

    JButton truncateBtn = new JButton("Truncate Table");
    truncateBtn.setBackground(TRUNCATE_BTN_COLOR);
    truncateBtn.setForeground(Color.BLACK);
    truncateBtn.putClientProperty("tableComboBox", tableComboBox);
    truncateBtn.addActionListener(truncateAction);

    JPanel actionPanel = new JPanel();
    actionPanel.setBackground(PRIMARY_BG);
    actionPanel.add(truncateBtn);

    mainPanel.add(tableSelectPanel, BorderLayout.CENTER);
    mainPanel.add(actionPanel, BorderLayout.SOUTH);

    contentPanel.add(mainPanel, BorderLayout.CENTER);
    contentPanel.revalidate();
    contentPanel.repaint();
   }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DatabaseApp::new);
    }
}