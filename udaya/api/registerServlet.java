package api;

import com.google.gson.Gson;
import service.Guest;
import service.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;

@WebServlet("/api/register")
public class registerServlet extends HttpServlet {
    private final Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Read request body
            BufferedReader reader = req.getReader();
            Map<String, String> userData = gson.fromJson(reader, Map.class);
            
            // Validate input
            if (!validateInput(userData, response)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(response));
                return;
            }
            
            // Create user
            Guest guest = new Guest();
            boolean success = guest.register(
                userData.get("name"),
                userData.get("email"),
                userData.get("phone"),
                userData.get("password")
            );
            
            if (success) {
                response.put("success", true);
                response.put("message", "Registration successful!");
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                throw new SQLException("Failed to create account");
            }
            
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.put("success", false);
            response.put("error", e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.put("success", false);
            response.put("error", "An unexpected error occurred");
        }
        
        resp.getWriter().write(gson.toJson(response));
    }
    
    private boolean validateInput(Map<String, String> userData, Map<String, Object> response) {
        if (userData.get("name") == null || userData.get("name").trim().isEmpty()) {
            response.put("error", "Name is required");
            return false;
        }
        
        if (userData.get("email") == null || !userData.get("email").matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            response.put("error", "Valid email is required");
            return false;
        }
        
        if (userData.get("phone") == null || !userData.get("phone").matches("^\\+?[0-9]{10,}$")) {
            response.put("error", "Valid phone number is required");
            return false;
        }
        
        if (userData.get("password") == null || userData.get("password").length() < 8) {
            response.put("error", "Password must be at least 8 characters long");
            return false;
        }
        
        return true;
    }
}
