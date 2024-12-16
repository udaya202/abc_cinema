package service;

import Util.DBconnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Base64;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javax.imageio.ImageIO;
import Util.EmailUtil;

public class User {
    private int id;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public boolean updateProfile(int userId, String name, String email, String phone) throws SQLException {
        String query = "UPDATE users SET name = ?, email = ?, phone_number = ? WHERE id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setInt(4, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean updatePassword(int userId, String currentPassword, String newPassword) throws SQLException {
        String checkQuery = "SELECT password FROM users WHERE id = ?";
        String updateQuery = "UPDATE users SET password = ? WHERE id = ?";
        
        try (Connection conn = DBconnection.getConnection()) {
            // First verify current password
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, userId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next() && rs.getString("password").equals(currentPassword)) {
                    // Current password is correct, update to new password
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, newPassword);
                        updateStmt.setInt(2, userId);
                        
                        int rowsAffected = updateStmt.executeUpdate();
                        return rowsAffected > 0;
                    }
                }
            }
        }
        return false;
    }

    public boolean createBookings(int movieId, int showtimeId, int userId, List<String> seats, String status) 
            throws SQLException {
        // Always set status to 'pending_payment' regardless of input
        String bookingStatus = "pending_payment";
        
        String query = "INSERT INTO bookings (movie_id, showtime_id, user_id, seat_number, status, booking_date, booking_time) " +
                       "VALUES (?, ?, ?, ?, ?, CURDATE(), CURTIME())";
        
        // First check if any of these seats are already booked
        String checkQuery = "SELECT seat_number FROM bookings WHERE movie_id = ? AND showtime_id = ? AND seat_number IN (" +
                           String.join(",", Collections.nCopies(seats.size(), "?")) + ")";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction
            
            // Check for existing bookings
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, movieId);
            checkStmt.setInt(2, showtimeId);
            for (int i = 0; i < seats.size(); i++) {
                checkStmt.setString(i + 3, seats.get(i));
            }
            
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                // Some seats are already booked
                throw new SQLException("Some seats are already booked");
            }
            
            // If no seats are booked, proceed with booking
            stmt = conn.prepareStatement(query);
            
            for (String seatNumber : seats) {
                stmt.setInt(1, movieId);
                stmt.setInt(2, showtimeId);
                stmt.setInt(3, userId);
                stmt.setString(4, seatNumber);
                stmt.setString(5, bookingStatus);
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            
            // Check if all insertions were successful
            boolean allSuccess = true;
            for (int result : results) {
                if (result <= 0) {
                    allSuccess = false;
                    break;
                }
            }
            
            if (allSuccess) {
                // Get user and movie details for email
                Map<String, Object> bookingDetails = getBookingDetails(conn, userId, movieId, showtimeId, seats);
                
                // Send confirmation email
                EmailUtil.sendBookingConfirmation(
                    (String) bookingDetails.get("email"),
                    (String) bookingDetails.get("name"),
                    (String) bookingDetails.get("movieTitle"),
                    (String) bookingDetails.get("showtime"),
                    seats,
                    calculateTotalAmount(conn, seats, movieId, showtimeId)
                );
                
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw e;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Object> getBookingDetails(Connection conn, int userId, int movieId, 
            int showtimeId, List<String> seats) throws SQLException {
        
        Map<String, Object> details = new HashMap<>();
        String query = "SELECT u.name, u.email, m.movie_title, s.display_time " +
                      "FROM users u " +
                      "JOIN movies m ON m.id = ? " +
                      "JOIN showtimes s ON s.id = ? " +
                      "WHERE u.id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, movieId);
            stmt.setInt(2, showtimeId);
            stmt.setInt(3, userId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                details.put("name", rs.getString("name"));
                details.put("email", rs.getString("email"));
                details.put("movieTitle", rs.getString("movie_title"));
                details.put("showtime", rs.getString("display_time"));
            }
        }
        
        return details;
    }

    private double calculateTotalAmount(Connection conn, List<String> seats, int movieId, int showtimeId) 
            throws SQLException {
        double total = 0;
        String query = "SELECT " +
                      "CASE " +
                      "    WHEN ? REGEXP '^[O-U]' THEN ms.balcony_ticket_price " +
                      "    ELSE ms.normal_ticket_price " +
                      "END as price " +
                      "FROM movie_showtime ms " +
                      "WHERE ms.movie_id = ? AND ms.showtime_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (String seat : seats) {
                stmt.setString(1, seat);
                stmt.setInt(2, movieId);
                stmt.setInt(3, showtimeId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    total += rs.getDouble("price");
                }
            }
        }
        
        return total;
    }

    public List<Map<String, Object>> getPendingBookings(int userId) throws SQLException {
        List<Map<String, Object>> bookings = new ArrayList<>();
        String query = "SELECT b.ticket_no, b.seat_number, b.status, " +
                       "DATE_FORMAT(b.booking_date, '%Y-%m-%d') as booking_date, " +
                       "m.movie_title, m.movie_poster, s.display_time, " +
                       "CASE WHEN b.seat_number REGEXP '^[O-U]' THEN ms.balcony_ticket_price " +
                       "ELSE ms.normal_ticket_price END as price " +
                       "FROM bookings b " +
                       "JOIN movies m ON b.movie_id = m.id " +
                       "JOIN showtimes s ON b.showtime_id = s.id " +
                       "JOIN movie_showtime ms ON (b.movie_id = ms.movie_id AND b.showtime_id = ms.showtime_id) " +
                       "WHERE b.user_id = ? AND b.status = 'pending_payment'";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> booking = new HashMap<>();
                booking.put("ticketNo", rs.getInt("ticket_no"));
                booking.put("seatNumber", rs.getString("seat_number"));
                booking.put("status", rs.getString("status"));
                booking.put("bookingDate", rs.getString("booking_date"));
                booking.put("movieTitle", rs.getString("movie_title"));
                booking.put("moviePoster", rs.getString("movie_poster"));
                booking.put("showtime", rs.getString("display_time"));
                booking.put("price", rs.getDouble("price"));
                bookings.add(booking);
            }
        }
        
        return bookings;
    }

    public List<Map<String, Object>> getPaidTickets(int userId) {
        List<Map<String, Object>> tickets = new ArrayList<>();
        String query = "SELECT b.ticket_no, b.movie_id, b.showtime_id, b.seat_number, " +
                       "b.booking_date, b.booking_time, " +
                       "m.movie_title, m.movie_poster, " +
                       "s.display_time " +
                       "FROM bookings b " +
                       "JOIN movies m ON b.movie_id = m.id " +
                       "JOIN showtimes s ON b.showtime_id = s.id " +
                       "WHERE b.user_id = ? AND b.status = 'paid' " +
                       "ORDER BY b.booking_date DESC, b.booking_time DESC";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            Map<String, Map<String, Object>> groupedTickets = new HashMap<>();
            
            while (rs.next()) {
                String key = rs.getInt("ticket_no") + "";
                
                if (!groupedTickets.containsKey(key)) {
                    Map<String, Object> ticket = new HashMap<>();
                    ticket.put("ticketId", generateTicketId(rs.getInt("ticket_no")));
                    ticket.put("movieTitle", rs.getString("movie_title"));
                    ticket.put("moviePoster", rs.getString("movie_poster"));
                    ticket.put("showtime", rs.getString("display_time"));
                    ticket.put("bookingDate", rs.getDate("booking_date"));
                    ticket.put("seats", new ArrayList<String>());
                    ticket.put("qrCode", generateQRCode(key));
                    groupedTickets.put(key, ticket);
                }
                
                Map<String, Object> ticket = groupedTickets.get(key);
                ((List<String>) ticket.get("seats")).add(rs.getString("seat_number"));
            }
            
            tickets.addAll(groupedTickets.values());
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tickets;
    }

    private String generateTicketId(int ticketNo) {
        return String.format("ABC-%06d", ticketNo);
    }

    private String generateQRCode(String ticketId) {
        try {
            // Use ZXing library to generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                ticketId, 
                BarcodeFormat.QR_CODE, 
                200, 
                200
            );

            // Convert BitMatrix to BufferedImage
            BufferedImage qrImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            // Convert to Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
            
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean updateBookingStatus(int userId, List<Integer> ticketNos, String newStatus) 
            throws SQLException {
        String query = "UPDATE bookings SET status = ? " +
                       "WHERE user_id = ? AND ticket_no IN (" + 
                       String.join(",", Collections.nCopies(ticketNos.size(), "?")) + ")";
                       
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, newStatus);
            stmt.setInt(2, userId);
            for (int i = 0; i < ticketNos.size(); i++) {
                stmt.setInt(i + 3, ticketNos.get(i));
            }
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == ticketNos.size();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean cancelBookings(int userId, List<String> seatNumbers) throws SQLException {
        String query = "DELETE FROM bookings " +
                       "WHERE user_id = ? AND seat_number IN (" + 
                       String.join(",", Collections.nCopies(seatNumbers.size(), "?")) + ")";
                       
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            for (int i = 0; i < seatNumbers.size(); i++) {
                stmt.setString(i + 2, seatNumbers.get(i));
            }
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == seatNumbers.size();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
