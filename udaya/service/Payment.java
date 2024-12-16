package service;

import Util.DBconnection;
import Util.EmailUtil;

import java.sql.*;
import java.util.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Payment {
    // PayHere Configuration
    private static final String MERCHANT_ID = "1228994";
    private static final String MERCHANT_SECRET = "MzY2MTkzNTc3NzI1NzQzMTY4OTIxNzEzNjcyMDQxMjcwMjYzNjA5OQ==";
    
    public Map<String, Object> initializePayment(int userId, String source, List<Integer> selectedTicketNos) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = DBconnection.getConnection()) {
            // Get user info
            Map<String, Object> userInfo = getUserInfo(conn, userId);
            result.put("userInfo", userInfo);
            
            // Get tickets based on source and selection
            List<Map<String, Object>> tickets;
            if ("bookings".equals(source)) {
                if (selectedTicketNos == null || selectedTicketNos.isEmpty()) {
                    throw new SQLException("No tickets selected for payment");
                }
                tickets = getSelectedTickets(conn, userId, selectedTicketNos);
            } else {
                tickets = getTheaterBookings(conn, userId);
            }
            
            if (tickets.isEmpty()) {
                throw new SQLException("No valid tickets found for payment");
            }
            
            // Calculate total amount
            double totalAmount = tickets.stream()
                .mapToDouble(ticket -> (Double) ticket.get("amount"))
                .sum();
            
            // Generate order ID
            String orderId = generateOrderId(userId);
            
            result.put("tickets", tickets);
            result.put("totalAmount", totalAmount);
            result.put("orderId", orderId);
            
            // Store payment info in database
            storePaymentInfo(conn, userId, orderId, tickets, totalAmount);
            
        } catch (SQLException e) {
            throw new SQLException("Failed to initialize payment: " + e.getMessage());
        }
        
        return result;
    }
    
    private Map<String, Object> getUserInfo(Connection conn, int userId) throws SQLException {
        String query = "SELECT name, email, phone_number FROM users WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> userInfo = new HashMap<>();
                String[] names = rs.getString("name").split(" ", 2);
                userInfo.put("firstName", names[0]);
                userInfo.put("lastName", names.length > 1 ? names[1] : "");
                userInfo.put("email", rs.getString("email"));
                userInfo.put("phone", rs.getString("phone_number"));
                return userInfo;
            }
            
            throw new SQLException("User not found");
        }
    }
    
    private List<Map<String, Object>> getPendingBookings(Connection conn, int userId) throws SQLException {
        String query = "SELECT b.ticket_no, b.seat_number, m.movie_title, s.display_time, " +
                      "CASE WHEN b.seat_number REGEXP '^[O-U]' THEN ms.balcony_ticket_price " +
                      "ELSE ms.normal_ticket_price END as amount " +
                      "FROM bookings b " +
                      "JOIN movies m ON b.movie_id = m.id " +
                      "JOIN showtimes s ON b.showtime_id = s.id " +
                      "JOIN movie_showtime ms ON (b.movie_id = ms.movie_id AND b.showtime_id = ms.showtime_id) " +
                      "WHERE b.user_id = ? AND b.status = 'pending_payment'";
        
        return getTickets(conn, query, userId);
    }
    
    private List<Map<String, Object>> getTheaterBookings(Connection conn, int userId) throws SQLException {
        String query = "SELECT b.ticket_no, b.seat_number, m.movie_title, s.display_time, " +
                      "CASE WHEN b.seat_number REGEXP '^[O-U]' THEN ms.balcony_ticket_price " +
                      "ELSE ms.normal_ticket_price END as amount " +
                      "FROM bookings b " +
                      "JOIN movies m ON b.movie_id = m.id " +
                      "JOIN showtimes s ON b.showtime_id = s.id " +
                      "JOIN movie_showtime ms ON (b.movie_id = ms.movie_id AND b.showtime_id = ms.showtime_id) " +
                      "WHERE b.user_id = ? AND b.status = 'pending_payment' " +
                      "AND b.booking_date = CURDATE()";
        
        return getTickets(conn, query, userId);
    }
    
    private List<Map<String, Object>> getTickets(Connection conn, String query, int userId) throws SQLException {
        List<Map<String, Object>> tickets = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> ticket = new HashMap<>();
                ticket.put("ticketNo", rs.getInt("ticket_no"));
                ticket.put("movieTitle", rs.getString("movie_title"));
                ticket.put("showtime", rs.getString("display_time"));
                ticket.put("seatNumber", rs.getString("seat_number"));
                ticket.put("amount", rs.getDouble("amount"));
                tickets.add(ticket);
            }
        }
        
        return tickets;
    }
    
    private String generateOrderId(int userId) {
        return String.format("ABC-%d-%d", userId, System.currentTimeMillis());
    }
    
    private void storePaymentInfo(Connection conn, int userId, String orderId, 
            List<Map<String, Object>> tickets, double totalAmount) throws SQLException {
        
        String query = "INSERT INTO payments (user_id, order_id, ticket_nos, amount, status) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            String ticketNos = tickets.stream()
                .map(t -> t.get("ticketNo").toString())
                .collect(Collectors.joining(","));
            
            stmt.setInt(1, userId);
            stmt.setString(2, orderId);
            stmt.setString(3, ticketNos);
            stmt.setDouble(4, totalAmount);
            stmt.setString(5, "pending");
            
            stmt.executeUpdate();
        }
    }
    
    public String generatePaymentHash(String orderId, double amount) {
        try {
            String toHash = String.format("%s%s%.2f%s%s",
                MERCHANT_ID,
                orderId,
                amount,
                "LKR",
                toUpperMD5(MERCHANT_SECRET)
            );
            
            return toUpperMD5(toHash);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate payment hash", e);
        }
    }
    
    private String toUpperMD5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString().toUpperCase();
    }
    
    public boolean verifyPayment(String merchantId, String orderId, String paymentId,
            String amount, String currency, int statusCode, String receivedHash) {
        try {
            // Verify merchant ID
            if (!MERCHANT_ID.equals(merchantId)) {
                System.err.println("Invalid merchant ID");
                return false;
            }
            
            // Generate local MD5 hash for verification
            String localHash = generateMD5Hash(merchantId, orderId, amount, currency, 
                                             String.valueOf(statusCode), MERCHANT_SECRET);
            
            // Verify hash
            if (!localHash.equalsIgnoreCase(receivedHash)) {
                System.err.println("Hash verification failed");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Payment verification error: " + e.getMessage());
            return false;
        }
    }
    
    private String generateMD5Hash(String... values) {
        try {
            String concatenated = String.join("", values);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(concatenated.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate MD5 hash", e);
        }
    }
    
    public boolean updatePaymentStatus(String orderId, String paymentId, int statusCode) {
        Connection conn = null;
        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);
            
            System.out.println("\n=== Starting Payment Status Update ===");
            
            // First check if payment was already processed
            String checkQuery = "SELECT status FROM payments WHERE order_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
                stmt.setString(1, orderId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && "success".equals(rs.getString("status"))) {
                    System.out.println("Payment already processed successfully");
                    return true;
                }
            }
            
            // Update payment status
            String paymentQuery = "UPDATE payments SET payment_id = ?, status = ?, updated_at = NOW() " +
                                "WHERE order_id = ?";
            
            int paymentUpdated = 0;
            try (PreparedStatement stmt = conn.prepareStatement(paymentQuery)) {
                stmt.setString(1, paymentId);
                stmt.setString(2, getStatusFromCode(statusCode));
                stmt.setString(3, orderId);
                paymentUpdated = stmt.executeUpdate();
                System.out.println("Payment record updated: " + paymentUpdated);
            }
            
            if (paymentUpdated == 0) {
                throw new SQLException("Payment record not found for order: " + orderId);
            }
            
            // If payment is successful, update booking status
            if (statusCode == 2) {
                // Get ticket numbers
                List<String> ticketNumbers = getTicketNumbers(conn, orderId);
                if (ticketNumbers.isEmpty()) {
                    throw new SQLException("No tickets found for order: " + orderId);
                }
                
                // Update each booking - now passing orderId
                int totalUpdated = updateBookingStatuses(conn, ticketNumbers, orderId);
                System.out.println("Updated " + totalUpdated + " booking(s)");
                
                if (totalUpdated != ticketNumbers.size()) {
                    throw new SQLException("Not all bookings were updated");
                }
                
                // Verify updates
                verifyBookingUpdates(conn, ticketNumbers);
            }
            
            conn.commit();
            System.out.println("All updates committed successfully");
            return true;
            
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("Updates rolled back due to error");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error in updatePaymentStatus: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("=== Payment Status Update Completed ===\n");
        }
    }
    
    private List<String> getTicketNumbers(Connection conn, String orderId) throws SQLException {
        List<String> ticketNumbers = new ArrayList<>();
        String query = "SELECT ticket_nos FROM payments WHERE order_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String ticketNos = rs.getString("ticket_nos");
                if (ticketNos != null && !ticketNos.trim().isEmpty()) {
                    ticketNumbers.addAll(Arrays.asList(ticketNos.split(",")));
                }
            }
        }
        return ticketNumbers;
    }
    
    private int updateBookingStatuses(Connection conn, List<String> ticketNumbers, String orderId) throws SQLException {
        String query = "UPDATE bookings SET status = 'paid' WHERE ticket_no = ?";
        int totalUpdated = 0;
        
        // First get all ticket details for email
        List<Map<String, Object>> ticketDetails = getTicketDetails(conn, ticketNumbers);
        String userEmail = null;
        String userName = null;
        
        if (!ticketDetails.isEmpty()) {
            userEmail = (String) ticketDetails.get(0).get("userEmail");
            userName = (String) ticketDetails.get(0).get("userName");
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (String ticketNo : ticketNumbers) {
                stmt.setInt(1, Integer.parseInt(ticketNo.trim()));
                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    totalUpdated++;
                    System.out.println("Updated booking status for ticket: " + ticketNo);
                }
            }
            
            // If all updates successful, send confirmation email
            if (totalUpdated == ticketNumbers.size() && userEmail != null) {
                double totalAmount = ticketDetails.stream()
                    .mapToDouble(t -> ((Number) t.get("amount")).doubleValue())
                    .sum();
                
                EmailUtil.sendPaymentConfirmation(
                    userEmail,
                    userName,
                    ticketDetails,
                    orderId,  // Now we have orderId available
                    totalAmount
                );
            }
        }
        return totalUpdated;
    }
    
    private List<Map<String, Object>> getTicketDetails(Connection conn, List<String> ticketNumbers) 
            throws SQLException {
        List<Map<String, Object>> tickets = new ArrayList<>();
        
        String placeholders = String.join(",", Collections.nCopies(ticketNumbers.size(), "?"));
        String query = "SELECT b.ticket_no, b.seat_number, m.movie_title, s.display_time, " +
                      "u.email as userEmail, u.name as userName, " +
                      "CASE WHEN b.seat_number REGEXP '^[O-U]' THEN ms.balcony_ticket_price " +
                      "ELSE ms.normal_ticket_price END as amount " +
                      "FROM bookings b " +
                      "JOIN users u ON b.user_id = u.id " +
                      "JOIN movies m ON b.movie_id = m.id " +
                      "JOIN showtimes s ON b.showtime_id = s.id " +
                      "JOIN movie_showtime ms ON (b.movie_id = ms.movie_id AND b.showtime_id = ms.showtime_id) " +
                      "WHERE b.ticket_no IN (" + placeholders + ")";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            int paramIndex = 1;
            for (String ticketNo : ticketNumbers) {
                stmt.setInt(paramIndex++, Integer.parseInt(ticketNo.trim()));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> ticket = new HashMap<>();
                ticket.put("ticketNo", rs.getInt("ticket_no"));
                ticket.put("movieTitle", rs.getString("movie_title"));
                ticket.put("showtime", rs.getString("display_time"));
                ticket.put("seatNumber", rs.getString("seat_number"));
                ticket.put("amount", rs.getDouble("amount"));
                ticket.put("userEmail", rs.getString("userEmail"));
                ticket.put("userName", rs.getString("userName"));
                tickets.add(ticket);
            }
        }
        
        return tickets;
    }
    
    private void verifyBookingUpdates(Connection conn, List<String> ticketNumbers) throws SQLException {
        String verifyQuery = "SELECT ticket_no, status FROM bookings WHERE ticket_no = ?";
        try (PreparedStatement stmt = conn.prepareStatement(verifyQuery)) {
            for (String ticketNo : ticketNumbers) {
                stmt.setInt(1, Integer.parseInt(ticketNo.trim()));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Verified ticket " + ticketNo + 
                                     " status: " + rs.getString("status"));
                }
            }
        }
    }
    
    private String getStatusFromCode(int statusCode) {
        switch (statusCode) {
            case 2:
                return "success";
            case 0:
                return "pending";
            case -1:
                return "canceled";
            case -2:
                return "failed";
            case -3:
                return "chargedback";
            default:
                return "unknown";
        }
    }
    
    private List<Map<String, Object>> getSelectedTickets(Connection conn, int userId, List<Integer> ticketNos) 
            throws SQLException {
        
        String placeholders = String.join(",", Collections.nCopies(ticketNos.size(), "?"));
        String query = "SELECT b.ticket_no, b.seat_number, m.movie_title, s.display_time, " +
                       "CASE WHEN b.seat_number REGEXP '^[O-U]' THEN ms.balcony_ticket_price " +
                       "ELSE ms.normal_ticket_price END as amount " +
                       "FROM bookings b " +
                       "JOIN movies m ON b.movie_id = m.id " +
                       "JOIN showtimes s ON b.showtime_id = s.id " +
                       "JOIN movie_showtime ms ON (b.movie_id = ms.movie_id AND b.showtime_id = ms.showtime_id) " +
                       "WHERE b.user_id = ? AND b.ticket_no IN (" + placeholders + ") " +
                       "AND b.status = 'pending_payment'";
        
        List<Map<String, Object>> tickets = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            int paramIndex = 1;
            stmt.setInt(paramIndex++, userId);
            for (Integer ticketNo : ticketNos) {
                stmt.setInt(paramIndex++, ticketNo);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> ticket = new HashMap<>();
                ticket.put("ticketNo", rs.getInt("ticket_no"));
                ticket.put("movieTitle", rs.getString("movie_title"));
                ticket.put("showtime", rs.getString("display_time"));
                ticket.put("seatNumber", rs.getString("seat_number"));
                ticket.put("amount", rs.getDouble("amount"));
                tickets.add(ticket);
            }
        }
        
        return tickets;
    }
} 
