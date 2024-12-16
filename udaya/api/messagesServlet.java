package api;

import com.google.gson.Gson;
import service.Admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin/messages/*")
public class messagesServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
            Admin admin = new Admin();
            req.setAttribute("messages", admin.getAllMessages());
            req.getRequestDispatcher("/adminPanel.jsp").forward(req, resp);

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();

        try {
            Admin admin = new Admin();
            boolean cleared = admin.clearAllMessages();

            response.put("success", cleared);
            response.put("message", cleared ? "All messages cleared successfully" : "Failed to clear messages");

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        resp.getWriter().write(gson.toJson(response));
    }
}
