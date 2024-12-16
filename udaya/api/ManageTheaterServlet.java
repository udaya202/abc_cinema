package api;

import service.Admin;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/manageTheater")
public class ManageTheaterServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Admin admin = new Admin();
        
        // Fetch showtimes with pricing information
        List<Map<String, Object>> showtimes = admin.getAllShowtimesWithPricing();
        req.setAttribute("showtimes", showtimes);
        
        // Fetch movie pricing information
        List<Map<String, Object>> moviePricing = admin.getAllMoviePricing();
        req.setAttribute("moviePricing", moviePricing);
        
        // Forward to the theater management page
        req.getRequestDispatcher("/adminPanel.jsp").forward(req, resp);
    }
} 