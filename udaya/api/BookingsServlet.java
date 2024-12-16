package api;

import service.User;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/bookings")
public class BookingsServlet extends HttpServlet {
    private final Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        
        try {
            // Check if user is logged in
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\": \"User not logged in\"}");
                return;
            }

            // Parse request body
            Map<String, Object> bookingData = gson.fromJson(req.getReader(), Map.class);
            
            int userId = (Integer) session.getAttribute("userId");
            int movieId = ((Double) bookingData.get("movieId")).intValue();
            int showtimeId = ((Double) bookingData.get("showtimeId")).intValue();
            List<String> seats = (List<String>) bookingData.get("seats");
            String paymentType = (String) bookingData.get("paymentType");
            String status = paymentType.equals("pay_now") ? "pending_payment" : "pay_later";

            // Create bookings
            User user = new User();
            boolean success = user.createBookings(movieId, showtimeId, userId, seats, status);

            if (success) {
                resp.getWriter().write("{\"success\": true}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\": false, \"error\": \"Failed to create booking\"}");
            }

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Database error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}