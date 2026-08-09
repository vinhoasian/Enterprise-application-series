import java.io.*;
import java.sql.*;
import java.util.*;
import com.mysql.cj.jdbc.MysqlDataSource;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class shipmentInsertServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String snum     = req.getParameter("snum");
            String pnum     = req.getParameter("pnum");
            String jnum     = req.getParameter("jnum");
            int    quantity = Integer.parseInt(req.getParameter("quantity"));

            Properties tmp = new Properties();
            String propPath = getServletContext().getRealPath("/WEB-INF/lib/dataentry.properties");
            MysqlDataSource dataSource = new MysqlDataSource();

            tmp.load(new FileInputStream(propPath));
            dataSource.setUrl(tmp.getProperty("MYSQL_DB_URL"));
            dataSource.setUser(tmp.getProperty("MYSQL_DB_USERNAME"));
            dataSource.setPassword(tmp.getProperty("MYSQL_DB_PASSWORD"));

            Connection connect = dataSource.getConnection();
            PreparedStatement statement = connect.prepareStatement(
                "INSERT INTO shipments VALUES (?, ?, ?, ?)");
            statement.setString(1, snum);
            statement.setString(2, pnum);
            statement.setString(3, jnum);
            statement.setInt(4, quantity);
            statement.executeUpdate();

            if (quantity >= 100) {
                PreparedStatement updateStmt = connect.prepareStatement(
                    "UPDATE suppliers SET status = status + 5 WHERE snum = ?");
                updateStmt.setString(1, snum);
                updateStmt.executeUpdate();
                updateStmt.close();

                req.setAttribute("message", "New shipments record: (" + snum + ", " + pnum + ", " +
                    jnum + ", " + quantity + ") - successfully entered into database. Business logic triggered.");
            } else {
                req.setAttribute("message", "New shipments record: (" + snum + ", " + pnum + ", " +
                    jnum + ", " + quantity + ") - successfully entered into database.");
            }

            statement.close();
            connect.close();

        } catch (Exception e) {
            req.setAttribute("error", "Error: " + e.getMessage());
        }

        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/dataEntryHome.jsp");
        dispatcher.forward(req, resp);
    }
}
