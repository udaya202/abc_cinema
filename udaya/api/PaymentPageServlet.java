package api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/payment")
public class PaymentPageServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("/login");
            return;
        }
        
        // Get payment source (bookings or theater)
        String source = req.getParameter("source");
        if (source == null || source.isEmpty()) {
            resp.sendRedirect("/mybookings");
            return;
        }
        
        // Forward to payment page
        req.getRequestDispatcher("/payment.jsp").forward(req, resp);
    }
} 