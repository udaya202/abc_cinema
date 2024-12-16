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

@WebServlet("/api/bookings/*")
public class BookingActionServlet extends HttpServlet {
    private final Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        String action = req.getPathInfo();
        
        try {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                throw new ServletException("User not logged in");
            }
            
            int userId = (Integer) session.getAttribute("userId");
            Map<String, Object> requestData = gson.fromJson(req.getReader(), Map.class);
            
            User user = new User();
            boolean success = false;
            
            if ("/pay".equals(action)) {
                List<Integer> ticketNos = (List<Integer>) requestData.get("ticketNos");
                success = user.updateBookingStatus(userId, ticketNos, "paid");
            } else if ("/cancel".equals(action)) {
                List<String> seatNumbers = (List<String>) requestData.get("seatNumbers");
                success = user.cancelBookings(userId, seatNumbers);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            
            resp.getWriter().write(gson.toJson(response));
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error));
        }
    }
} 