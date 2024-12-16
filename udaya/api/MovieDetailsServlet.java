package api;

import service.Guest;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/movie/*")
public class MovieDetailsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            // Extract movie ID from path
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendRedirect("/movies");
                return;
            }
            
            int movieId = Integer.parseInt(pathInfo.substring(1));
            
            // Get movie details
            Guest guest = new Guest();
            Map<String, Object> movieDetails = guest.getMovieDetails(movieId);
            
            if (movieDetails != null) {
                req.setAttribute("movie", movieDetails);
                req.getRequestDispatcher("/movieDetails.jsp").forward(req, resp);
            } else {
                resp.sendRedirect("/movies");
            }
            
        } catch (NumberFormatException e) {
            resp.sendRedirect("/movies");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading movie details");
        }
    }
} 