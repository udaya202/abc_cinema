package api;

import com.google.gson.Gson;
import service.Guest;
import service.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    private final Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Read request body
            Map<String, String> loginData = gson.fromJson(req.getReader(), Map.class);
            String email = loginData.get("email");
            String password = loginData.get("password");
            
            // Validate input
            if (email == null || password == null) {
                throw new ServletException("Email and password are required");
            }
            
            // Authenticate user
            Guest guest = new Guest();
            Map<String, Object> userData = guest.login(email, password);
            
            if (userData != null) {
                // Create session
                HttpSession session = req.getSession(true);
                session.setAttribute("userId", userData.get("id"));
                session.setAttribute("userName", userData.get("name"));
                session.setAttribute("userEmail", userData.get("email"));
                session.setAttribute("isLoggedIn", true);
                
                response.put("success", true);
                response.put("message", "Login successful!");
                response.put("user", userData);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                throw new ServletException("Invalid email or password");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        resp.getWriter().write(gson.toJson(response));
    }
} 