package api;

import service.Admin;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Map;
import com.google.gson.Gson;
import java.util.HashMap;
import java.net.URLDecoder;
import java.io.BufferedReader;

@WebServlet("/admin/manageShowtimes/*")
public class manageShowtimesServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String displayTime = req.getParameter("displayTime");
            String startTime = req.getParameter("startTime");
            String endTime = req.getParameter("endTime");

            // Convert string times to SQL Time objects
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Time sqlStartTime = new Time(timeFormat.parse(startTime).getTime());
            Time sqlEndTime = new Time(timeFormat.parse(endTime).getTime());

            Admin admin = new Admin();
            boolean isAdded = admin.addShowtime(displayTime, sqlStartTime, sqlEndTime);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": " + isAdded + "}");
            
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null) {
                throw new ServletException("Showtime ID is required");
            }
            
            // Read the request body for PUT requests
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            
            // Parse the URL-encoded parameters
            String[] params = buffer.toString().split("&");
            Map<String, String> paramMap = new HashMap<>();
            for (String param : params) {
                String[] pair = param.split("=");
                if (pair.length == 2) {
                    paramMap.put(pair[0], URLDecoder.decode(pair[1], "UTF-8"));
                }
            }
            
            int showtimeId = Integer.parseInt(pathInfo.substring(1));
            String displayTime = paramMap.get("displayTime");
            String startTime = paramMap.get("startTime");
            String endTime = paramMap.get("endTime");

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Time sqlStartTime = new Time(timeFormat.parse(startTime).getTime());
            Time sqlEndTime = new Time(timeFormat.parse(endTime).getTime());

            Admin admin = new Admin();
            boolean isUpdated = admin.updateShowtime(showtimeId, displayTime, sqlStartTime, sqlEndTime);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": " + isUpdated + "}");
            
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int showtimeId = Integer.parseInt(req.getParameter("id"));
            Admin admin = new Admin();
            boolean isDeleted = admin.deleteShowtime(showtimeId);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": " + isDeleted + "}");
            
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && !pathInfo.equals("/")) {
                // Get single showtime
                int showtimeId = Integer.parseInt(pathInfo.substring(1));
                Admin admin = new Admin();
                Map<String, Object> showtime = admin.getShowtimeById(showtimeId);
                
                if (showtime != null) {
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    // Convert Time objects to string format for JSON
                    showtime.put("startTime", showtime.get("startTime").toString());
                    showtime.put("endTime", showtime.get("endTime").toString());
                    
                    resp.getWriter().write(new Gson().toJson(showtime));
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Showtime not found");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Showtime ID is required");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
