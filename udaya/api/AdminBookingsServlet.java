package api;

import com.google.gson.Gson;
import service.Admin;
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

@WebServlet("/admin/bookings/*")
public class AdminBookingsServlet extends HttpServlet {
    private final Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo();
        
        // If no path info or root path, forward to admin panel
        if (pathInfo == null || pathInfo.equals("/")) {
            // Set bookings page as active
            req.setAttribute("activePage", "bookings");
            req.getRequestDispatcher("/adminPanel.jsp").forward(req, resp);
            return;
        }
        
        // Handle API requests
        switch (pathInfo) {
            case "/list":
                handleGetBookings(req, resp);
                break;
            case "/movies/list":
                handleGetMovies(req, resp);
                break;
            case "/showtimes/list":
                handleGetShowtimes(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
    
    private void handleGetBookings(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get pagination and filter parameters with defaults
            int page = getIntParameter(req, "page", 1);
            int limit = getIntParameter(req, "limit", 10);
            String movieId = req.getParameter("movie");
            String showtimeId = req.getParameter("showtime");
            String status = req.getParameter("status");
            
            Admin admin = new Admin();
            Map<String, Object> result = admin.getBookings(page, limit, movieId, showtimeId, status);
            
            response.put("success", true);
            response.put("bookings", result.get("bookings"));
            response.put("totalPages", result.get("totalPages"));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        resp.getWriter().write(gson.toJson(response));
    }
    
    private int getIntParameter(HttpServletRequest req, String param, int defaultValue) {
        String value = req.getParameter(param);
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();
        
        try {
            String action = req.getPathInfo();
            if ("/delete".equals(action)) {
                Map<String, Object> requestData = gson.fromJson(req.getReader(), Map.class);
                List<Integer> ticketNos = (List<Integer>) requestData.get("ticketNos");
                
                if (ticketNos == null || ticketNos.isEmpty()) {
                    throw new ServletException("No tickets selected for deletion");
                }
                
                Admin admin = new Admin();
                boolean success = admin.deleteBookings(ticketNos);
                
                response.put("success", success);
                if (success) {
                    response.put("message", "Bookings deleted successfully");
                }
            } else {
                throw new ServletException("Invalid action");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        resp.getWriter().write(gson.toJson(response));
    }
    
    private void handleGetMovies(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();
        
        try {
            Admin admin = new Admin();
            List<Map<String, Object>> movies = admin.getMoviesForFilter();
            
            response.put("success", true);
            response.put("movies", movies);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        String jsonResponse = gson.toJson(response);
        resp.getWriter().write(jsonResponse);
        resp.getWriter().flush();
    }
    
    private void handleGetShowtimes(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();
        
        try {
            Admin admin = new Admin();
            List<Map<String, Object>> showtimes = admin.getShowtimesForFilter();
            
            // Add debug information
            System.out.println("Showtimes found: " + showtimes.size());
            
            response.put("success", true);
            response.put("showtimes", showtimes);
            
        } catch (Exception e) {
            // Enhanced error logging
            System.err.println("Error getting showtimes: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("error", "Failed to load showtimes: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        String jsonResponse = gson.toJson(response);
        System.out.println("Response: " + jsonResponse); // Debug output
        resp.getWriter().write(jsonResponse);
        resp.getWriter().flush();
    }
} 