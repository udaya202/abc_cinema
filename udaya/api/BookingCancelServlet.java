package api;

import com.google.gson.Gson;
import service.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/bookings/cancel")
public class BookingCancelServlet extends HttpServlet {
    private final Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verify user is logged in
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                throw new ServletException("User not logged in");
            }
            
            int userId = (Integer) session.getAttribute("userId");
            
            // Parse request body
            Map<String, Object> requestData = gson.fromJson(req.getReader(), Map.class);
            List<String> seatNumbers = (List<String>) requestData.get("seatNumbers");
            
            if (seatNumbers == null || seatNumbers.isEmpty()) {
                throw new ServletException("No seats selected for cancellation");
            }
            
            // Cancel bookings
            User user = new User();
            boolean success = user.cancelBookings(userId, seatNumbers);
            
            response.put("success", success);
            if (success) {
                response.put("message", "Booking(s) deleted successfully");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        resp.getWriter().write(gson.toJson(response));
    }
} 