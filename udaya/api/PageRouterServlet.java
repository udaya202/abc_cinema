package api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/about", "/contact", "/login", "/register", "/account"})
public class PageRouterServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        
        // Check if user is logged in for protected routes
        if (path.equals("/account")) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("isLoggedIn") == null) {
                resp.sendRedirect("/login");
                return;
            }
        }
        
        switch (path) {
            case "/about":
                req.getRequestDispatcher("/about.jsp").forward(req, resp);
                break;
            case "/contact":
                req.getRequestDispatcher("/contact.jsp").forward(req, resp);
                break;
            case "/login":
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                break;
            case "/register":
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                break;
            case "/account":
                req.getRequestDispatcher("/account.jsp").forward(req, resp);
                break;
            default:
                resp.sendRedirect("/");
                break;
        }
    }
} 