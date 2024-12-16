package api;

import com.google.gson.Gson;
import service.Admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/Movies/*")
public class GetMoviebyId extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // e.g., "/1"
        if (pathInfo == null || pathInfo.length() <= 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid movie ID\"}");
            return;
        }

        int movieId;
        try {
            movieId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid movie ID\"}");
            return;
        }

        Admin admin = new Admin();
        Map<String, Object> movie = admin.getMovieById(movieId);

        if (movie == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"Movie not found\"}");
            return;
        }

        resp.setContentType("application/json");
        resp.getWriter().write(new Gson().toJson(movie));
    }
}
