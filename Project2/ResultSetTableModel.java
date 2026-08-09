import javax.swing.table.AbstractTableModel;
import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import com.mysql.cj.jdbc.MysqlDataSource;

// A TableModel that supplies ResultSet data to a JTable.
// IMPORTANT NOTE: ResultSet rows and columns are counted from 1 and Table
// rows and columns are counted from 0. When processing ResultSet rows or
// columns for use in a JTable, it is necessary to add 1 to the row or
// column number to manipulate the appropriate ResultSet column.

public class ResultSetTableModel extends AbstractTableModel {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int numberOfRows;

    // keep track of database connection status
    private boolean connectedToDatabase = false;

    // Constructor: accepts an already-established connection
    public ResultSetTableModel(Connection connect) throws SQLException {
        connection = connect;
        statement = connection.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        );
        connectedToDatabase = true;
    }

    // get class that represents column type
    @Override
    public Class getColumnClass(int column) throws IllegalStateException {
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        try {
            String className = metaData.getColumnClassName(column + 1);
            return Class.forName(className);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return Object.class;
    }

    // get number of columns in ResultSet
    @Override
    public int getColumnCount() throws IllegalStateException {
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        try {
            return metaData.getColumnCount();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return 0;
    }

    // get name of a particular column in ResultSet
    @Override
    public String getColumnName(int column) throws IllegalStateException {
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        try {
            return metaData.getColumnName(column + 1);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return "";
    }

    // return number of rows in ResultSet
    @Override
    public int getRowCount() throws IllegalStateException {
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        return numberOfRows;
    }

    // obtain value in particular row and column
    @Override
    public Object getValueAt(int row, int column) throws IllegalStateException {
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        try {
            resultSet.absolute(row + 1);
            return resultSet.getObject(column + 1);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return "";
    }

    // set new database query string (SELECT)
    public void setQuery(String query) throws SQLException, IllegalStateException {
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");

        resultSet = statement.executeQuery(query);
        metaData = resultSet.getMetaData();

        resultSet.last();
        numberOfRows = resultSet.getRow();

        fireTableStructureChanged();
    }

    // set new database update string (INSERT, UPDATE, DELETE, DDL)
    // returns number of rows affected
    public int setUpdate(String query) throws SQLException, IllegalStateException {
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");

        int rowsAffected = statement.executeUpdate(query);
        return rowsAffected;
    }

    // update the operationslog database for a given user
    // isQuery = true  -> increment num_queries
    // isQuery = false -> increment num_updates
    public static void logOperation(String loginUsername, boolean isQuery) {
        Properties props = new Properties();
        try (FileInputStream fin = new FileInputStream("project2app.properties")) {
            props.load(fin);
            MysqlDataSource ds = new MysqlDataSource();
            ds.setURL(props.getProperty("MYSQL_DB_URL"));
            ds.setUser(props.getProperty("MYSQL_DB_USERNAME"));
            ds.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));

            try (Connection logConn = ds.getConnection()) {
                // Check if a row exists for this user
                String checkSQL = "SELECT * FROM operationscount WHERE login_username = ?";
                PreparedStatement checkStmt = logConn.prepareStatement(checkSQL);
                checkStmt.setString(1, loginUsername);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Row exists — update the appropriate counter
                    String updateSQL;
                    if (isQuery) {
                        updateSQL = "UPDATE operationscount SET num_queries = num_queries + 1 WHERE login_username = ?";
                    } else {
                        updateSQL = "UPDATE operationscount SET num_updates = num_updates + 1 WHERE login_username = ?";
                    }
                    PreparedStatement updateStmt = logConn.prepareStatement(updateSQL);
                    updateStmt.setString(1, loginUsername);
                    updateStmt.executeUpdate();
                    updateStmt.close();
                } else {
                    // Row does not exist — insert a new row
                    String insertSQL = "INSERT INTO operationscount (login_username, num_queries, num_updates) VALUES (?, ?, ?)";
                    PreparedStatement insertStmt = logConn.prepareStatement(insertSQL);
                    insertStmt.setString(1, loginUsername);
                    if (isQuery) {
                        insertStmt.setInt(2, 1);
                        insertStmt.setInt(3, 0);
                    } else {
                        insertStmt.setInt(2, 0);
                        insertStmt.setInt(3, 1);
                    }
                    insertStmt.executeUpdate();
                    insertStmt.close();
                }
                checkStmt.close();
            }
        } catch (IOException | SQLException e) {
            System.err.println("Operations log error: " + e.getMessage());
        }
    }

    // close Statement and Connection
    public void disconnectFromDatabase() {
        if (connectedToDatabase) {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            } finally {
                connectedToDatabase = false;
            }
        }
    }
}
