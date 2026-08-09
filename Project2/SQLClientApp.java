import javax.swing.*;
import javax.swing.table.*;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class SQLClientApp extends JFrame {

    // ── Connection state ──────────────────────────────────────────────
    private Connection connection = null;
    private ResultSetTableModel tableModel = null;
    private String currentLoginUsername = "";

    // ── Top panel – connection details ────────────────────────────────
    private JComboBox<String> dbPropertiesComboBox;
    private JComboBox<String> userPropertiesComboBox;
    private JTextField        usernameText;
    private JPasswordField    passwordText;
    private JButton           connectButton;
    private JButton           disconnectButton;
    private JLabel            connectionStatusLabel;

    // ── Middle panel – SQL command ────────────────────────────────────
    private JTextArea textCommand;
    private JButton   executeButton;
    private JButton   clearCommandButton;

    // ── Bottom panel – results ────────────────────────────────────────
    private JTable  resultTable;
    private JButton clearResultButton;
    private JButton closeButton;

    // ─────────────────────────────────────────────────────────────────
    public SQLClientApp() {
        super("SQL CLIENT APPLICATION");
        buildGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(true);
        setVisible(true);
    }

    // ── Build entire GUI ─────────────────────────────────────────────
    private void buildGUI() {
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(Color.DARK_GRAY);

        add(buildConnectionPanel(), BorderLayout.NORTH);
        add(buildCommandPanel(),    BorderLayout.CENTER);
        add(buildResultPanel(),     BorderLayout.SOUTH);
    }

    // ── Connection panel (top) ────────────────────────────────────────
    private JPanel buildConnectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.DARK_GRAY);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.CYAN),
            "Connection Details",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 13),
            Color.CYAN));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;

        String[] dbItems   = {"project2.properties", "bikedb.properties"};
        String[] userItems = {"root.properties", "client1.properties", "client2.properties"};

        dbPropertiesComboBox   = new JComboBox<>(dbItems);
        userPropertiesComboBox = new JComboBox<>(userItems);
        usernameText = new JTextField(20);
        usernameText.setBackground(Color.BLACK);
        usernameText.setForeground(Color.WHITE);
        usernameText.setFont(new Font("Arial", Font.BOLD, 14));

        passwordText = new JPasswordField(20);
        passwordText.setBackground(Color.BLACK);
        passwordText.setForeground(Color.WHITE);

        connectButton    = makeButton("Connect to Database",      Color.BLUE,  Color.WHITE);
        disconnectButton = makeButton("Disconnect From Database",  Color.RED,   Color.WHITE);

        connectionStatusLabel = new JLabel("NO CONNECTION ESTABLISHED");
        connectionStatusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        connectionStatusLabel.setForeground(Color.RED);
        connectionStatusLabel.setOpaque(true);
        connectionStatusLabel.setBackground(Color.BLACK);

        // Row 0 – DB URL Properties
        gc.gridx = 0; gc.gridy = 0;
        panel.add(makeLabel("DB URL Properties"), gc);
        gc.gridx = 1;
        panel.add(dbPropertiesComboBox, gc);
        gc.gridx = 2; gc.gridwidth = 1;
        panel.add(connectButton, gc);

        // Row 1 – User Properties
        gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 1;
        panel.add(makeLabel("User Properties"), gc);
        gc.gridx = 1;
        panel.add(userPropertiesComboBox, gc);
        gc.gridx = 2;
        panel.add(disconnectButton, gc);

        // Row 2 – Username
        gc.gridx = 0; gc.gridy = 2;
        panel.add(makeLabel("Username"), gc);
        gc.gridx = 1;
        panel.add(usernameText, gc);
        gc.gridx = 2;
        panel.add(makeLabel("CONNECTION STATUS"), gc);

        // Row 3 – Password
        gc.gridx = 0; gc.gridy = 3;
        panel.add(makeLabel("Password"), gc);
        gc.gridx = 1;
        panel.add(passwordText, gc);
        gc.gridx = 2;
        panel.add(connectionStatusLabel, gc);

        // ── Connect button action ─────────────────────────────────────
        connectButton.addActionListener(e -> {
            try {
                // Close any existing connection first
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    tableModel = null;
                    resultTable.setModel(new DefaultTableModel());
                }

                // Read the DB properties file
                String dbFile   = (String) dbPropertiesComboBox.getSelectedItem();
                Properties dbProps = loadProperties(dbFile);

                // Read the user properties file
                String userFile = (String) userPropertiesComboBox.getSelectedItem();
                Properties userProps = loadProperties(userFile);

                // Get credentials entered by user
                String enteredUser = usernameText.getText().trim();
                String enteredPass = String.valueOf(passwordText.getPassword());

                // Validate against properties file
                String fileUser = userProps.getProperty("MYSQL_DB_USERNAME", "").trim();
                String filePass = userProps.getProperty("MYSQL_DB_PASSWORD", "").trim();

                if (!enteredUser.equals(fileUser) || !enteredPass.equals(filePass)) {
                    connectionStatusLabel.setText("NO CONNECTION - Credentials Mismatch!");
                    connectionStatusLabel.setForeground(Color.RED);
                    connectionStatusLabel.setBackground(Color.BLACK);
                    connection = null;
                    return;
                }

                // Establish connection using DB properties URL + entered credentials
                MysqlDataSource ds = new MysqlDataSource();
                ds.setURL(dbProps.getProperty("MYSQL_DB_URL"));
                ds.setUser(enteredUser);
                ds.setPassword(enteredPass);

                connection = ds.getConnection();
                currentLoginUsername = enteredUser + "@localhost";

                connectionStatusLabel.setText(dbProps.getProperty("MYSQL_DB_URL"));
                connectionStatusLabel.setForeground(Color.BLACK);
                connectionStatusLabel.setBackground(Color.GREEN);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Properties File Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ── Disconnect button action ──────────────────────────────────
        disconnectButton.addActionListener(e -> {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                connection = null;
                tableModel = null;
                resultTable.setModel(new DefaultTableModel());
                textCommand.setText("");
                connectionStatusLabel.setText("NO CONNECTION ESTABLISHED");
                connectionStatusLabel.setForeground(Color.RED);
                connectionStatusLabel.setBackground(Color.BLACK);
                currentLoginUsername = "";
            }
        });

        return panel;
    }

    // ── SQL command panel (middle) ────────────────────────────────────
    private JPanel buildCommandPanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBackground(Color.DARK_GRAY);

        JLabel cmdLabel = new JLabel("SQL Command Input Window", SwingConstants.CENTER);
        cmdLabel.setFont(new Font("Arial", Font.BOLD, 13));
        cmdLabel.setForeground(Color.CYAN);
        panel.add(cmdLabel, BorderLayout.NORTH);

        textCommand = new JTextArea(6, 60);
        textCommand.setFont(new Font("Courier New", Font.BOLD, 14));
        textCommand.setLineWrap(true);
        textCommand.setWrapStyleWord(true);
        panel.add(new JScrollPane(textCommand), BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 4));
        btnRow.setBackground(Color.DARK_GRAY);

        executeButton      = makeButton("Execute SQL Command", Color.GREEN,  Color.BLACK);
        clearCommandButton = makeButton("Clear SQL Command",   Color.YELLOW, Color.BLACK);

        btnRow.add(executeButton);
        btnRow.add(clearCommandButton);
        panel.add(btnRow, BorderLayout.SOUTH);

        // ── Execute button action ─────────────────────────────────────
        executeButton.addActionListener(e -> {
            if (connection == null) {
                JOptionPane.showMessageDialog(this,
                    "No database connection. Please connect first.",
                    "No Connection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = textCommand.getText().trim();
            if (sql.isEmpty()) return;

            try {
                tableModel = new ResultSetTableModel(connection);

                String sqlUpper = sql.toUpperCase().replaceAll("\\s+", " ").trim();
                boolean isQuery = sqlUpper.startsWith("SELECT") ||
                                  sqlUpper.startsWith("(SELECT") ||
                                  sqlUpper.startsWith("( SELECT");
                if (isQuery) {
                    // Query – use executeQuery
                    tableModel.setQuery(sql);
                    resultTable.setModel(tableModel);

                    // Log the query to operationslog (invisible to user)
                    ResultSetTableModel.logOperation(currentLoginUsername, true);

                } else {
                    // Non-query (INSERT, UPDATE, DELETE, DDL)
                    int rows = tableModel.setUpdate(sql);
                    resultTable.setModel(new DefaultTableModel()); // clear table

                    JOptionPane.showMessageDialog(this,
                        "Successful Update..." + rows + " rows updated.",
                        "Successful Update", JOptionPane.INFORMATION_MESSAGE);

                    // Log the update to operationslog
                    ResultSetTableModel.logOperation(currentLoginUsername, false);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ── Clear command button action ───────────────────────────────
        clearCommandButton.addActionListener(e -> textCommand.setText(""));

        return panel;
    }

    // ── Results panel (bottom) ────────────────────────────────────────
    private JPanel buildResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBackground(Color.DARK_GRAY);
        panel.setPreferredSize(new Dimension(900, 320));

        JLabel resLabel = new JLabel("SQL Execution Result Window", SwingConstants.CENTER);
        resLabel.setFont(new Font("Arial", Font.BOLD, 13));
        resLabel.setForeground(Color.CYAN);
        panel.add(resLabel, BorderLayout.NORTH);

        resultTable = new JTable(new DefaultTableModel());
        resultTable.setEnabled(false);
        resultTable.setGridColor(Color.WHITE);
        resultTable.setBackground(Color.BLACK);
        resultTable.setForeground(Color.CYAN);
        resultTable.setFont(new Font("Arial", Font.BOLD, 12));
        resultTable.getTableHeader().setBackground(Color.DARK_GRAY);
        resultTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(880, 240));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        btnRow.setBackground(Color.DARK_GRAY);

        clearResultButton = makeButton("Clear Result Window", Color.YELLOW, Color.BLACK);
        closeButton       = makeButton("Close Application",   Color.RED,    Color.WHITE);

        btnRow.add(clearResultButton);
        btnRow.add(Box.createHorizontalStrut(400));
        btnRow.add(closeButton);
        panel.add(btnRow, BorderLayout.SOUTH);

        // ── Clear result button action ────────────────────────────────
        clearResultButton.addActionListener(e -> resultTable.setModel(new DefaultTableModel()));

        // ── Close application button action ──────────────────────────
        closeButton.addActionListener(e -> {
            try {
                if (connection != null && !connection.isClosed())
                    connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                System.exit(0);
            }
        });

        return panel;
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        return lbl;
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setBorderPainted(true);
        return btn;
    }

    private Properties loadProperties(String filename) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fin = new FileInputStream(filename)) {
            props.load(fin);
        }
        return props;
    }

    // ── Main ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SQLClientApp::new);
    }
}