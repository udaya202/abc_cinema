package api;

import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/payment/config")
public class PaymentConfigServlet extends HttpServlet {
    private static final String MERCHANT_ID = "1228994";  // Replace with your merchant ID
    private static final boolean IS_SANDBOX = true;  // Set to false for production
    
    private final Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        Map<String, Object> config = new HashMap<>();
        
        try {
            config.put("merchantId", MERCHANT_ID);
            config.put("sandbox", IS_SANDBOX);
            config.put("notifyUrl", req.getScheme() + "://" + req.getServerName() + 
                       ":" + req.getServerPort() + "/api/payment/notify");
            
            resp.getWriter().write(gson.toJson(config));
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load payment configuration");
            resp.getWriter().write(gson.toJson(error));
        }
    }
} 