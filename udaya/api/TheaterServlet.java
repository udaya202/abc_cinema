package api;

import service.Guest;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/theater/*")
public class TheaterServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            // Check if user is logged in
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                // Store the intended destination
                session = req.getSession(true);
                session.setAttribute("redirectUrl", req.getRequestURI());
                resp.sendRedirect("/login");
                return;
            }

            // Extract showtime ID from path
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendRedirect("/movies");
                return;
            }
            
            int showtimeId = Integer.parseInt(pathInfo.substring(1));
            int userId = (Integer) session.getAttribute("userId");
            
            // Get theater details including seat availability
            Guest guest = new Guest();
            Map<String, Object> theaterDetails = guest.getTheaterDetails(showtimeId);
            
            if (theaterDetails != null) {
                // Add user ID to theater details for future use
                theaterDetails.put("userId", userId);
                
                // Set attributes for JSP
                req.setAttribute("theater", theaterDetails);
                req.getRequestDispatcher("/theater.jsp").forward(req, resp);
            } else {
                resp.sendRedirect("/movies");
            }
            
        } catch (NumberFormatException e) {
            resp.sendRedirect("/movies");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading theater details");
        }
    }
}