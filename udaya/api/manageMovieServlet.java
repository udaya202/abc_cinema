package api;

import service.Admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

@WebServlet("/admin/manageMovies/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class manageMovieServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Admin admin = new Admin();
        List<Map<String, Object>> movies = admin.getAllMovies();

        // Set movies as a request attribute
        req.setAttribute("movies", movies);

        // Forward request to the JSP page
        req.getRequestDispatcher("/adminPanel.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            String[] showtimeIds = req.getParameterValues("showtimeIds");

            // Process uploaded file
            Part filePart = req.getPart("poster");
            String fileName = filePart.getSubmittedFileName();
            String uploadDir = req.getServletContext().getRealPath("/uploads");
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdir();
            }

            String filePath = "uploads/" + fileName;
            filePart.write(uploadDir + File.separator + fileName);

            // Convert showtimeIds to a List<Integer>
            List<Integer> showtimes = new ArrayList<>();
            for (String id : showtimeIds) {
                showtimes.add(Integer.parseInt(id));
            }

            Admin admin = new Admin();
            boolean isAdded = admin.addMovie(title, filePath, description, showtimes);

            // Send JSON response
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": " + isAdded + "}");
            
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }

//    @Override
//    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setContentType("application/json");
//        resp.setCharacterEncoding("UTF-8");
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            String movieId = req.getParameter("id");
//            System.out.println("Received Movie ID: " + movieId);
//
//            if (movieId == null || movieId.trim().isEmpty()) {
//                throw new ServletException("Movie ID is required");
//            }
//
//            // Verify admin session
//            HttpSession session = req.getSession(false);
//            if (session == null || session.getAttribute("isAdmin") == null) {
//                throw new ServletException("Unauthorized access");
//            }
//
//            Admin admin = new Admin();
//            boolean isDeleted = admin.deleteMovie(Integer.parseInt(movieId.trim()));
//
//            if (isDeleted) {
//                response.put("success", true);
//                response.put("message", "Movie deleted successfully");
//            } else {
//                throw new ServletException("Failed to delete movie");
//            }
//
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("error", e.getMessage());
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        }
//
//        resp.getWriter().write(gson.toJson(response));
//    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String movieId = req.getParameter("id");

        if (movieId != null) {
            Admin admin = new Admin();
            boolean isDeleted = admin.deleteMovie(Integer.parseInt(movieId)); // Your Admin class handles DB deletion

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": " + isDeleted + "}");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Movie ID is required");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Extract movieId from path
            String pathInfo = req.getPathInfo();
            if (pathInfo == null) {
                throw new ServletException("Movie ID is required");
            }
            int movieId = Integer.parseInt(pathInfo.substring(1));

            // Get form parameters
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            String[] showtimeIds = req.getParameterValues("showtimeIds");

            String filePath = null;
            Part filePart = req.getPart("poster");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = filePart.getSubmittedFileName();
                String uploadDir = req.getServletContext().getRealPath("/uploads");
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists()) {
                    uploadFolder.mkdir();
                }

                filePath = "uploads/" + fileName;
                filePart.write(uploadDir + File.separator + fileName);
            }

            Admin admin = new Admin();
            boolean isUpdated = admin.updateMovie(movieId, title, description, showtimeIds, filePath);

            // Send JSON response
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": " + isUpdated + "}");
            
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }

}
