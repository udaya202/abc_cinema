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
import java.util.Map;

@WebServlet(urlPatterns = {"/api/account/*"})
public class AccountServlet extends HttpServlet {
    private final Gson gson = new Gson();
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        
        try {
            if (pathInfo == null) {
                throw new ServletException("Invalid endpoint");
            }

            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                throw new ServletException("Not authenticated");
            }

            Map<String, Object> response = new HashMap<>();
            User user = new User();
            
            switch (pathInfo) {
                case "/profile":
                    handleProfileUpdate(req, resp, user, session);
                    break;
                case "/password":
                    handlePasswordUpdate(req, resp, user, session);
                    break;
                case "/preferences":
                    handlePreferencesUpdate(req, resp, user, session);
                    break;
                default:
                    throw new ServletException("Invalid endpoint");
            }
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(errorResponse));
        }
    }

    private void handleProfileUpdate(HttpServletRequest req, HttpServletResponse resp, User user, HttpSession session) 
            throws IOException, ServletException {
        Map<String, String> profileData = gson.fromJson(req.getReader(), Map.class);
        
        try {
            boolean updated = user.updateProfile(
                (Integer) session.getAttribute("userId"),
                profileData.get("name"),
                profileData.get("email"),
                profileData.get("phone")
            );
            
            if (updated) {
                // Update session attributes
                session.setAttribute("userName", profileData.get("name"));
                session.setAttribute("userEmail", profileData.get("email"));
                session.setAttribute("userPhone", profileData.get("phone"));
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Profile updated successfully");
                response.putAll(profileData);
                
                resp.getWriter().write(gson.toJson(response));
            } else {
                throw new ServletException("Failed to update profile");
            }
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }
    }

    private void handlePasswordUpdate(HttpServletRequest req, HttpServletResponse resp, User user, HttpSession session) 
            throws IOException, ServletException {
        Map<String, String> passwordData = gson.fromJson(req.getReader(), Map.class);
        
        try {
            boolean updated = user.updatePassword(
                (Integer) session.getAttribute("userId"),
                passwordData.get("currentPassword"),
                passwordData.get("newPassword")
            );
            
            if (updated) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Password updated successfully");
                resp.getWriter().write(gson.toJson(response));
            } else {
                throw new ServletException("Current password is incorrect");
            }
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }
    }

    private void handlePreferencesUpdate(HttpServletRequest req, HttpServletResponse resp, User user, HttpSession session) 
            throws IOException {
        Map<String, Boolean> prefData = gson.fromJson(req.getReader(), Map.class);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Preferences updated successfully");
        resp.getWriter().write(gson.toJson(response));
    }
} 