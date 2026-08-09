import java.io.*;
import java.sql.*;
import java.util.*;
import com.mysql.cj.jdbc.MysqlDataSource;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class clientServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String input = req.getParameter("input");
        req.setAttribute("input", input);

        try {
            Properties tmp = new Properties();
            String propPath = getServletContext().getRealPath("/WEB-INF/lib/client.properties");
            MysqlDataSource dataSource = new MysqlDataSource();

            tmp.load(new FileInputStream(propPath));
            dataSource.setUrl(tmp.getProperty("MYSQL_DB_URL"));
            dataSource.setUser(tmp.getProperty("MYSQL_DB_USERNAME"));
            dataSource.setPassword(tmp.getProperty("MYSQL_DB_PASSWORD"));

            Connection connect = dataSource.getConnection();
            PreparedStatement statement = connect.prepareStatement(input);
            boolean hasResultSet = statement.execute();

            if (hasResultSet) {
                ResultSet rs = statement.getResultSet();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columns = rsmd.getColumnCount();

                List<String> headers = new ArrayList<>();
                for (int i = 1; i <= columns; i++)
                    headers.add(rsmd.getColumnName(i));
                req.setAttribute("headers", headers);

                List<List<String>> results = new ArrayList<>();
                while (rs.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= columns; i++)
                        row.add(rs.getString(i));
                    results.add(row);
                }
                req.setAttribute("queryResults", results);
                rs.close();
            } else {
                req.setAttribute("message",
                    "The statement executed successfully. A total of " +
                    statement.getUpdateCount() + " row(s) were updated.");
            }

            statement.close();
            connect.close();

        } catch (Exception e) {
            req.setAttribute("error", "Error executing the SQL statement: " + e.getMessage());
        }

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/clientPage.jsp");
        dispatcher.forward(req, resp);
    }
}
