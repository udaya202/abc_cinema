package api;

import service.Admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/adminlogin")
public class adminloginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/adminlogin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("id");
        String password = req.getParameter("password");

        // Instantiate the AdminLoginService to validate credentials
        Admin loginService = new Admin();

        // Call the loginAdmin method to check credentials
        boolean isValidAdmin = loginService.loginAdmin(username, password);

        if (isValidAdmin) {
            // Admin login successful
            HttpSession session = req.getSession();
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("role", "admin"); // Set the role to admin
            resp.sendRedirect("/admin/manageMovies");
        } else {
            // Invalid login
            req.setAttribute("loginFailed", "true");
            req.getRequestDispatcher("/adminlogin.jsp").forward(req, resp);
        }
    }

}
