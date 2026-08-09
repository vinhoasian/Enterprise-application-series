import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.Properties;
import java.sql.*;
import com.mysql.cj.jdbc.MysqlDataSource;

public class authenticationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Properties tmp = new Properties();
        String propPath = getServletContext().getRealPath("/WEB-INF/lib/system-app.properties");
        MysqlDataSource dataSource = new MysqlDataSource();

        try {
            tmp.load(new FileInputStream(propPath));
            dataSource.setUrl(tmp.getProperty("MYSQL_DB_URL"));
            dataSource.setUser(tmp.getProperty("MYSQL_DB_USERNAME"));
            dataSource.setPassword(tmp.getProperty("MYSQL_DB_PASSWORD"));

            Connection connect = dataSource.getConnection();
            PreparedStatement statement = connect.prepareStatement(
                "SELECT * FROM usercredentials WHERE login_username = ? AND login_password = ?");
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet res = statement.executeQuery();

            if (res.next()) {
                if (username.equals("root"))
                    resp.sendRedirect("/Project-3/rootPage.jsp");
                else if (username.equals("client"))
                    resp.sendRedirect("/Project-3/clientPage.jsp");
                else if (username.equals("dataentry"))
                    resp.sendRedirect("/Project-3/dataEntryHome.jsp");
                else if (username.equals("theaccountant"))
                    resp.sendRedirect("/Project-3/accountantHome.jsp");
            } else {
                resp.sendRedirect("/Project-3/error.html");
            }
            statement.close();
            connect.close();
        } catch (SQLException e) {
            getServletContext().log(e.toString());
        }
    }
}
