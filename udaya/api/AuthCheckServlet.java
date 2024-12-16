package api;

import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/auth/check")
public class AuthCheckServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();

        HttpSession session = req.getSession(false);
        boolean isLoggedIn = session != null && session.getAttribute("userId") != null;

        response.put("isLoggedIn", isLoggedIn);
        resp.getWriter().write(gson.toJson(response));
    }
}