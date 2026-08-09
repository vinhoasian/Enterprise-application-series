import java.io.*;
import java.sql.*;
import java.util.*;
import com.mysql.cj.jdbc.MysqlDataSource;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class jobInsertServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String jnum       = req.getParameter("jnum");
            String jname      = req.getParameter("jname");
            int    numworkers = Integer.parseInt(req.getParameter("numworkers"));
            String city       = req.getParameter("city");

            Properties tmp = new Properties();
            String propPath = getServletContext().getRealPath("/WEB-INF/lib/dataentry.properties");
            MysqlDataSource dataSource = new MysqlDataSource();

            tmp.load(new FileInputStream(propPath));
            dataSource.setUrl(tmp.getProperty("MYSQL_DB_URL"));
            dataSource.setUser(tmp.getProperty("MYSQL_DB_USERNAME"));
            dataSource.setPassword(tmp.getProperty("MYSQL_DB_PASSWORD"));

            Connection connect = dataSource.getConnection();
            PreparedStatement statement = connect.prepareStatement(
                "INSERT INTO jobs VALUES (?, ?, ?, ?)");
            statement.setString(1, jnum);
            statement.setString(2, jname);
            statement.setInt(3, numworkers);
            statement.setString(4, city);
            statement.executeUpdate();

            req.setAttribute("message", "New job record: (" + jnum + ", " + jname + ", " +
                numworkers + ", " + city + ") - successfully entered into database.");
            statement.close();
            connect.close();

        } catch (Exception e) {
            req.setAttribute("error", "Error: " + e.getMessage());
        }

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/dataEntryHome.jsp");
        dispatcher.forward(req, resp);
    }
}
