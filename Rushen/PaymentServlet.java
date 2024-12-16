package api;

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
import java.util.List;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.lang.System;

@WebServlet("/api/payment/*")
public class PaymentServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final Payment paymentService = new Payment();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo();
        if ("/initialize".equals(pathInfo)) {
            handleInitialize(req, resp);
        } else if ("/hash".equals(pathInfo)) {
            handleHashGeneration(req, resp);
        } else if ("/notify".equals(pathInfo)) {
            handlePaymentNotification(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void handleInitialize(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();
        
        try {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                throw new ServletException("User not logged in");
            }
            
            int userId = (Integer) session.getAttribute("userId");
            String source = req.getParameter("source");
            
            System.out.println("Initializing payment for user: " + userId + ", source: " + source);
            
            // Get selected tickets from request body if source is 'bookings'
            List<Integer> selectedTickets = null;
            if ("bookings".equals(source)) {
                // Read the request body
                BufferedReader reader = req.getReader();
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                
                String requestBody = buffer.toString();
                System.out.println("Raw request body: " + requestBody);
                
                if (!requestBody.isEmpty()) {
                    try {
                        Map<String, Object> requestData = gson.fromJson(requestBody, Map.class);
                        System.out.println("Parsed request data: " + requestData);
                        
                        if (requestData != null && requestData.get("tickets") != null) {
                            List<Double> doubleList = (List<Double>) requestData.get("tickets");
                            System.out.println("Raw ticket list: " + doubleList);
                            
                            if (doubleList == null || doubleList.isEmpty() || doubleList.stream().anyMatch(d -> d == null)) {
                                throw new ServletException("Invalid or empty ticket numbers");
                            }
                            
                            selectedTickets = doubleList.stream()
                                .map(Double::intValue)
                                .collect(Collectors.toList());
                                
                            System.out.println("Final selected tickets: " + selectedTickets);
                        } else {
                            throw new ServletException("No tickets found in request");
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing request body: " + e.getMessage());
                        e.printStackTrace();
                        throw new ServletException("Invalid request format: " + e.getMessage());
                    }
                } else {
                    throw new ServletException("Empty request body");
                }
            }
            
            // Get payment details based on source
            Map<String, Object> paymentDetails = paymentService.initializePayment(userId, source, selectedTickets);
            
            response.put("success", true);
            response.putAll(paymentDetails);
            
        } catch (Exception e) {
            System.err.println("Error in handleInitialize: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        String jsonResponse = gson.toJson(response);
        System.out.println("Sending response: " + jsonResponse);
        resp.getWriter().write(jsonResponse);
        resp.getWriter().flush();
    }
    
    private void handleHashGeneration(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> requestData = gson.fromJson(req.getReader(), Map.class);
            String orderId = (String) requestData.get("orderId");
            double amount = ((Number) requestData.get("amount")).doubleValue();
            
            String hash = paymentService.generatePaymentHash(orderId, amount);
            
            response.put("success", true);
            response.put("hash", hash);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        resp.getWriter().write(gson.toJson(response));
    }
    
    private void handlePaymentNotification(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        System.out.println("\n=== Payment Notification Received ===");
        
        try {
            // Log all parameters for debugging
            Map<String, String[]> params = req.getParameterMap();
            for (String key : params.keySet()) {
                System.out.println(key + ": " + params.get(key)[0]);
            }
            
            // Extract payment notification parameters
            String merchantId = req.getParameter("merchant_id");
            String orderId = req.getParameter("order_id");
            String paymentId = req.getParameter("payment_id");
            String payhereAmount = req.getParameter("payhere_amount");
            String payhereCurrency = req.getParameter("payhere_currency");
            int statusCode = Integer.parseInt(req.getParameter("status_code"));
            String md5sig = req.getParameter("md5sig");
            
            System.out.println("\nProcessing payment notification:");
            System.out.println("Order ID: " + orderId);
            System.out.println("Payment ID: " + paymentId);
            System.out.println("Status Code: " + statusCode);
            
            // Verify payment authenticity
            boolean isValid = paymentService.verifyPayment(
                merchantId, orderId, paymentId, 
                payhereAmount, payhereCurrency, 
                statusCode, md5sig
            );
            
            if (isValid) {
                System.out.println("Payment signature verified successfully");
                
                // Update payment and booking status
                boolean updated = paymentService.updatePaymentStatus(orderId, paymentId, statusCode);
                
                if (updated) {
                    System.out.println("Payment and booking status updated successfully");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("Payment processed successfully");
                } else {
                    System.out.println("Failed to update payment status");
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("Failed to update payment status");
                }
            } else {
                System.out.println("Invalid payment signature");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid payment signature");
            }
            
        } catch (Exception e) {
            System.err.println("Error processing payment notification: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error processing payment");
        }
        
        System.out.println("=== Payment Notification Processing Completed ===\n");
    }
} 