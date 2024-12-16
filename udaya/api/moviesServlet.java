package api;

import service.Guest;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/movies")
public class moviesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            Guest guest = new Guest();
            List<Map<String, Object>> movies = guest.getMovies();

            // Set movies as request attribute
            req.setAttribute("movies", movies);

            // Forward to movies.jsp
            req.getRequestDispatcher("/movies.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading movies");
        }
    }
}