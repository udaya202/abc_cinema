package api;

import com.google.gson.Gson;
import service.Guest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/contact")
public class ContactServlet extends HttpServlet {
    private final Gson gson = new Gson();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Read request body
            Map<String, String> contactData = gson.fromJson(req.getReader(), Map.class);
            
            // Extract form data
            String name = contactData.get("name");
            String email = contactData.get("email");
            String subject = contactData.get("subject");
            String message = contactData.get("message");
            
            // Submit contact form
            Guest guest = new Guest();
            boolean isSubmitted = guest.submitContactForm(name, email, subject, message);
            
            if (isSubmitted) {
                response.put("success", true);
                response.put("message", "Thank you for your message. We'll get back to you soon!");
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                throw new ServletException("Failed to submit contact form");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        resp.getWriter().write(gson.toJson(response));
    }
} 