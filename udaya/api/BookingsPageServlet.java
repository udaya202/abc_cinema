package api;

import service.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/mybookings")
public class BookingsPageServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("/login");
            return;
        }
        
        try {
            int userId = (Integer) session.getAttribute("userId");
            User user = new User();
            
            // Get pending bookings with ticket numbers
            String query = "SELECT b.ticket_no as ticketNo, b.seat_number, b.status, " +
                          "DATE_FORMAT(b.booking_date, '%Y-%m-%d') as bookingDate, " +
                          "m.movie_title, s.display_time " +
                          "FROM bookings b " +
                          "JOIN movies m ON b.movie_id = m.id " +
                          "JOIN showtimes s ON b.showtime_id = s.id " +
                          "WHERE b.user_id = ? AND b.status = 'pending_payment'";
            
            List<Map<String, Object>> pendingBookings = user.getPendingBookings(userId);
            if (!pendingBookings.isEmpty()) {
                req.setAttribute("pendingBookings", pendingBookings);
            }
            
            // Get paid tickets
            List<Map<String, Object>> tickets = user.getPaidTickets(userId);
            if (!tickets.isEmpty()) {
                req.setAttribute("tickets", tickets);
            }
            
            req.getRequestDispatcher("/mybookings.jsp").forward(req, resp);
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading bookings");
        }
    }
}