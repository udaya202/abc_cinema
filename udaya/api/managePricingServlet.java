package api;

import service.Admin;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;

@WebServlet("/admin/managePricing/*")
public class managePricingServlet extends HttpServlet {
    private final Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Admin admin = new Admin();
            String pathInfo = req.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all movies for dropdown
                List<Map<String, Object>> movies = admin.getMoviesForPricing();
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(movies));
            } else if (pathInfo.startsWith("/movie/")) {
                // Get showtimes for specific movie
                int movieId = Integer.parseInt(pathInfo.substring(7));
                List<Map<String, Object>> showtimes = admin.getShowtimesForMovie(movieId);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(showtimes));
            } else if (pathInfo.startsWith("/price/")) {
                // Get pricing details for specific movie
                int movieId = Integer.parseInt(pathInfo.substring(7));
                Map<String, Object> pricing = admin.getMoviePricing(movieId);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(pricing));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            resp.getWriter().write(gson.toJson(errorResponse));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            BufferedReader reader = req.getReader();
            Map<String, Object> pricingData = gson.fromJson(reader, Map.class);
            
            Admin admin = new Admin();
            
            // Proper type casting
            int movieId = ((Double) pricingData.get("movieId")).intValue();
            double balconyPrice = ((Number) pricingData.get("balconyPrice")).doubleValue();
            double normalPrice = ((Number) pricingData.get("normalPrice")).doubleValue();
            
            // Convert showtime IDs from Double to Integer
            List<Double> doubleIds = (List<Double>) pricingData.get("showtimeIds");
            List<Integer> showtimeIds = doubleIds.stream()
                    .map(Double::intValue)
                    .collect(Collectors.toList());
            
            boolean success = admin.addMoviePricing(movieId, balconyPrice, normalPrice, showtimeIds);
            
            resp.setContentType("application/json");
            Map<String, Boolean> response = new HashMap<>();
            response.put("success", success);
            resp.getWriter().write(gson.toJson(response));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            resp.getWriter().write(gson.toJson(errorResponse));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            BufferedReader reader = req.getReader();
            Map<String, Object> pricingData = gson.fromJson(reader, Map.class);
            
            Admin admin = new Admin();
            
            // Proper type casting
            int movieId = ((Double) pricingData.get("movieId")).intValue();
            double balconyPrice = ((Number) pricingData.get("balconyPrice")).doubleValue();
            double normalPrice = ((Number) pricingData.get("normalPrice")).doubleValue();
            
            // Convert showtime IDs from Double to Integer
            List<Double> doubleIds = (List<Double>) pricingData.get("showtimeIds");
            List<Integer> showtimeIds = doubleIds.stream()
                    .map(Double::intValue)
                    .collect(Collectors.toList());
            
            boolean success = admin.updateMoviePricing(movieId, balconyPrice, normalPrice, showtimeIds);
            
            resp.setContentType("application/json");
            Map<String, Boolean> response = new HashMap<>();
            response.put("success", success);
            resp.getWriter().write(gson.toJson(response));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            resp.getWriter().write(gson.toJson(errorResponse));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int movieId = Integer.parseInt(req.getParameter("id"));
        System.out.println(movieId);
        try {
            Admin admin = new Admin();
            boolean success = admin.deleteMoviePricing(movieId);
            
            resp.setContentType("application/json");
            Map<String, Boolean> response = new HashMap<>();
            response.put("success", success);
            resp.getWriter().write(gson.toJson(response));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            resp.getWriter().write(gson.toJson(errorResponse));
        }
    }
}
