import com.google.gson.Gson;
import service.Payment;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/payment/success")
public class PaymentSuccessServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final Payment paymentService = new Payment();
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        System.out.println("\n=== Processing Payment Success ===");
        
        try {
            // Verify user is logged in
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                throw new ServletException("User not logged in");
            }
            
            // Parse request body
            Map<String, Object> paymentData = gson.fromJson(req.getReader(), Map.class);
            String orderId = (String) paymentData.get("order_id");
            String paymentId = (String) paymentData.get("payment_id");
            int statusCode = ((Number) paymentData.get("status_code")).intValue();
            
            System.out.println("Processing success callback:");
            System.out.println("Order ID: " + orderId);
            System.out.println("Payment ID: " + paymentId);
            System.out.println("Status Code: " + statusCode);
            
            // Update payment and booking status
            boolean updated = paymentService.updatePaymentStatus(orderId, paymentId, statusCode);
            
            Map<String, Object> response = new HashMap<>();
            if (updated) {
                System.out.println("Payment and booking status updated successfully");
                response.put("success", true);
            } else {
                System.out.println("Failed to update payment status");
                response.put("success", false);
                response.put("error", "Failed to update payment status");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            
            resp.getWriter().write(gson.toJson(response));
            
        } catch (Exception e) {
            System.err.println("Error processing payment success: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
        }
        
        System.out.println("=== Payment Success Processing Completed ===\n");
    }
}