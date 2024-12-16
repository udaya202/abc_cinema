package service;

import Util.DBconnection;
import java.sql.*;
import java.util.*;
import com.google.gson.Gson;

public class Guest {

    public Map<String, Object> login(String email, String password) throws SQLException {
        String query = "SELECT id, name, email, phone_number FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", rs.getInt("id"));
                    userData.put("name", rs.getString("name"));
                    userData.put("email", rs.getString("email"));
                    userData.put("phoneNumber", rs.getString("phone_number"));
                    return userData;
                }
            }
        }
        return null;
    }

    // Registration method
    public boolean register(String name, String email, String phoneNumber, String password) throws SQLException {
        // SQL query to check if email already exists
        String checkEmailQuery = "SELECT COUNT(*) FROM users WHERE email = ?";

        // SQL query to insert new user
        String insertQuery = "INSERT INTO users (name, email, phone_number, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBconnection.getConnection()) {
            // First check if email already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkEmailQuery)) {
                checkStmt.setString(1, email);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Email already exists");
                }
            }

            // If email doesn't exist, proceed with registration
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, email);
                insertStmt.setString(3, phoneNumber);
                insertStmt.setString(4, password); // In real world, password should be hashed

                int rowsAffected = insertStmt.executeUpdate();
                return rowsAffected > 0;
            }
        }
    }

    public boolean submitContactForm(String name, String email, String subject, String message) throws SQLException {
        String query = "INSERT INTO Contact (name, email, subject, message) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Validate inputs
            if (name == null || name.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                subject == null || subject.trim().isEmpty() ||
                message == null || message.trim().isEmpty()) {
                throw new SQLException("All fields are required");
            }
            
            // Validate email format
//            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
//                throw new SQLException("Invalid email format");
//            }
            
            // Set parameters
            stmt.setString(1, name.trim());
            stmt.setString(2, email.trim().toLowerCase());
            stmt.setString(3, subject.trim());
            stmt.setString(4, message.trim());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Add this method to your existing Guest class
    public List<Map<String, Object>> getMovies() {
        List<Map<String, Object>> movies = new ArrayList<>();
        String query = "SELECT m.id, m.movie_title, m.movie_poster, m.movie_description, " +
                       "GROUP_CONCAT(DISTINCT s.display_time ORDER BY s.display_time SEPARATOR ', ') as showtimes " +
                       "FROM movies m " +
                       "LEFT JOIN movie_showtime ms ON m.id = ms.movie_id " +
                       "LEFT JOIN showtimes s ON ms.showtime_id = s.id " +
                       "WHERE m.id IS NOT NULL " +  // Ensure we get only valid movies
                       "GROUP BY m.id, m.movie_title, m.movie_poster, m.movie_description";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> movie = new HashMap<>();
                movie.put("id", rs.getInt("id"));
                movie.put("title", rs.getString("movie_title"));
                movie.put("poster", rs.getString("movie_poster"));
                movie.put("description", rs.getString("movie_description"));
                
                // Handle showtimes
                String showtimes = rs.getString("showtimes");
                movie.put("showtimes", showtimes != null ? Arrays.asList(showtimes.split(", ")) : new ArrayList<>());
                
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    // Add this method to your existing Guest class
    public Map<String, Object> getMovieDetails(int movieId) {
        String query = "SELECT m.id, m.movie_title, m.movie_poster, m.movie_description, " +
                       "GROUP_CONCAT(DISTINCT s.display_time ORDER BY s.display_time SEPARATOR ', ') as showtimes, " +
                       "GROUP_CONCAT(DISTINCT s.id ORDER BY s.display_time) as showtime_ids " +
                       "FROM movies m " +
                       "LEFT JOIN movie_showtime ms ON m.id = ms.movie_id " +
                       "LEFT JOIN showtimes s ON ms.showtime_id = s.id " +
                       "WHERE m.id = ? " +
                       "GROUP BY m.id, m.movie_title, m.movie_poster, m.movie_description";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, movieId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> movie = new HashMap<>();
                    movie.put("id", rs.getInt("id"));
                    movie.put("title", rs.getString("movie_title"));
                    movie.put("poster", rs.getString("movie_poster"));
                    movie.put("description", rs.getString("movie_description"));
                    
                    // Handle showtimes
                    String showtimes = rs.getString("showtimes");
                    String showtimeIds = rs.getString("showtime_ids");
                    
                    if (showtimes != null && showtimeIds != null) {
                        String[] times = showtimes.split(", ");
                        String[] ids = showtimeIds.split(",");
                        List<Map<String, String>> showtimesList = new ArrayList<>();
                        
                        for (int i = 0; i < times.length; i++) {
                            Map<String, String> showtime = new HashMap<>();
                            showtime.put("id", ids[i].trim());
                            showtime.put("time", times[i].trim());
                            showtimesList.add(showtime);
                        }
                        
                        movie.put("showtimes", showtimesList);
                    } else {
                        movie.put("showtimes", new ArrayList<>());
                    }
                    
                    return movie;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Add this method to your Guest class
    public Map<String, Object> getTheaterDetails(int showtimeId) {
        String query = "SELECT ms.movie_id, m.movie_title, m.movie_poster, m.movie_description, " +
                       "s.display_time, ms.balcony_ticket_price, ms.normal_ticket_price, " +
                       "(SELECT GROUP_CONCAT(b.seat_number) " +
                       "FROM bookings b " +
                       "WHERE b.movie_id = ms.movie_id " +
                       "AND b.showtime_id = ms.showtime_id) as booked_seats " +
                       "FROM movie_showtime ms " +
                       "JOIN movies m ON ms.movie_id = m.id " +
                       "JOIN showtimes s ON ms.showtime_id = s.id " +
                       "WHERE ms.showtime_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, showtimeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> theater = new HashMap<>();
                    
                    // Movie details
                    Map<String, Object> movie = new HashMap<>();
                    movie.put("id", rs.getInt("movie_id"));
                    movie.put("title", rs.getString("movie_title"));
                    movie.put("poster", rs.getString("movie_poster"));
                    movie.put("description", rs.getString("movie_description"));
                    theater.put("movie", movie);
                    
                    // Showtime and pricing details
                    theater.put("showtimeId", showtimeId);
                    theater.put("showtime", rs.getString("display_time"));
                    theater.put("balconyPrice", rs.getDouble("balcony_ticket_price"));
                    theater.put("normalPrice", rs.getDouble("normal_ticket_price"));
                    
                    // Booked seats
                    String bookedSeatsStr = rs.getString("booked_seats");
                    List<String> bookedSeats = new ArrayList<>();
                    if (bookedSeatsStr != null) {
                        bookedSeats = Arrays.asList(bookedSeatsStr.split(","));
                    }
                    // Convert to JSON array string for JavaScript
                    theater.put("bookedSeats", new Gson().toJson(bookedSeats));
                    
                    return theater;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}