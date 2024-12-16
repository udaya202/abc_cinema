package service;

import Util.DBconnection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Admin {

    public boolean loginAdmin(String id, String password) {
        boolean isValid = false;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Get the database connection
            connection = DBconnection.getConnection();

            // SQL query to check if admin exists with the provided username and password
            String query = "SELECT * FROM admin WHERE id = ? AND password = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, id);
            statement.setString(2, password);

            resultSet = statement.executeQuery();

            // If a record exists, the credentials are valid
            if (resultSet.next()) {
                isValid = true;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log exception details for debugging
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return isValid;
    }

    //Add Movie method

    public boolean addMovie(String title, String posterPath, String description, List<Integer> showtimeIds) {
        Connection connection = null;
        PreparedStatement movieStmt = null;
        PreparedStatement movieShowtimeStmt = null;
        ResultSet generatedKeys = null;

        try {
            connection = DBconnection.getConnection();

            // Disable auto-commit for transactional integrity
            connection.setAutoCommit(false);

            // Insert into movies table
            String insertMovieSQL = "INSERT INTO movies (movie_title, movie_poster, movie_description) VALUES (?, ?, ?)";
            movieStmt = connection.prepareStatement(insertMovieSQL, Statement.RETURN_GENERATED_KEYS);
            movieStmt.setString(1, title);
            movieStmt.setString(2, posterPath);
            movieStmt.setString(3, description);
            movieStmt.executeUpdate();

            // Retrieve the auto-generated movie ID
            generatedKeys = movieStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new SQLException("Failed to retrieve generated movie ID.");
            }
            int movieId = generatedKeys.getInt(1);

            // Insert into movie_showtime table
            String insertMovieShowtimeSQL = "INSERT INTO movie_showtime (movie_id, showtime_id) VALUES (?, ?)";
            movieShowtimeStmt = connection.prepareStatement(insertMovieShowtimeSQL);

            for (Integer showtimeId : showtimeIds) {
                movieShowtimeStmt.setInt(1, movieId);
                movieShowtimeStmt.setInt(2, showtimeId);
                movieShowtimeStmt.executeUpdate();
            }

            // Commit the transaction
            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback(); // Rollback on error
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;

        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (movieStmt != null) movieStmt.close();
                if (movieShowtimeStmt != null) movieShowtimeStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public List<String[]> getShowtimes() {
        List<String[]> showtimes = new ArrayList<>();
        String query = "SELECT id, display_time FROM showtimes";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                showtimes.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("display_time")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return showtimes;
    }

//    public boolean deleteMovie(int movieId) throws SQLException {
//        // First delete related records from movie_showtimes
//        String deleteShowtimesQuery = "DELETE FROM movie_showtimes WHERE movie_id = ?";
//        String deleteMovieQuery = "DELETE FROM movies WHERE id = ?";
//
//        try (Connection conn = DBconnection.getConnection()) {
//            // Start transaction
//            conn.setAutoCommit(false);
//
//            try {
//                // First delete from movie_showtimes
//                try (PreparedStatement stmt = conn.prepareStatement(deleteShowtimesQuery)) {
//                    stmt.setInt(1, movieId);
//                    stmt.executeUpdate();
//                }
//
//                // Then delete the movie
//                try (PreparedStatement stmt = conn.prepareStatement(deleteMovieQuery)) {
//                    stmt.setInt(1, movieId);
//                    int rowsAffected = stmt.executeUpdate();
//
//                    if (rowsAffected > 0) {
//                        conn.commit();
//                        return true;
//                    } else {
//                        conn.rollback();
//                        return false;
//                    }
//                }
//            } catch (SQLException e) {
//                conn.rollback();
//                throw e;
//            } finally {
//                conn.setAutoCommit(true);
//            }
//        }
//    }

    public boolean deleteMovie(int movieId) {
        String query = "DELETE FROM movies WHERE id = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, movieId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> getMovieById(int movieId) {
        String query = "SELECT m.id, m.movie_title, m.movie_description, m.movie_poster, " +
                "GROUP_CONCAT(s.display_time SEPARATOR ', ') as showtimes " +
                "FROM movies m " +
                "LEFT JOIN movie_showtime ms ON m.id = ms.movie_id " +
                "LEFT JOIN showtimes s ON ms.showtime_id = s.id " +
                "WHERE m.id = ? " +
                "GROUP BY m.id";
                
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> movie = new HashMap<>();
                    movie.put("id", rs.getInt("id"));
                    movie.put("title", rs.getString("movie_title"));
                    movie.put("description", rs.getString("movie_description"));
                    movie.put("poster", rs.getString("movie_poster"));
                    movie.put("showtimes", rs.getString("showtimes"));
                    return movie;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateMovie(int movieId, String title, String description, String[] showtimeIds, String filePath) {
        if (title == null || description == null || showtimeIds == null) {
            return false;
        }

        Connection connection = null;
        PreparedStatement movieStmt = null;
        PreparedStatement deleteShowtimesStmt = null;
        PreparedStatement insertShowtimesStmt = null;
        
        try {
            connection = DBconnection.getConnection();
            connection.setAutoCommit(false);
            
            // Update movie details
            StringBuilder movieQuery = new StringBuilder(
                "UPDATE movies SET movie_title = ?, movie_description = ?"
            );
            if (filePath != null) {
                movieQuery.append(", movie_poster = ?");
            }
            movieQuery.append(" WHERE id = ?");
            
            movieStmt = connection.prepareStatement(movieQuery.toString());
            movieStmt.setString(1, title);
            movieStmt.setString(2, description);
            
            if (filePath != null) {
                movieStmt.setString(3, filePath);
                movieStmt.setInt(4, movieId);
            } else {
                movieStmt.setInt(3, movieId);
            }
            
            int rowsUpdated = movieStmt.executeUpdate();
            if (rowsUpdated == 0) {
                // Movie not found
                connection.rollback();
                return false;
            }
            
            // Update showtimes
            deleteShowtimesStmt = connection.prepareStatement(
                "DELETE FROM movie_showtime WHERE movie_id = ?"
            );
            deleteShowtimesStmt.setInt(1, movieId);
            deleteShowtimesStmt.executeUpdate();
            
            // Insert new showtime associations
            insertShowtimesStmt = connection.prepareStatement(
                "INSERT INTO movie_showtime (movie_id, showtime_id) VALUES (?, ?)"
            );
            
            for (String showtimeId : showtimeIds) {
                insertShowtimesStmt.setInt(1, movieId);
                insertShowtimesStmt.setInt(2, Integer.parseInt(showtimeId));
                insertShowtimesStmt.executeUpdate();
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
            
        } finally {
            try {
                if (movieStmt != null) movieStmt.close();
                if (deleteShowtimesStmt != null) deleteShowtimesStmt.close();
                if (insertShowtimesStmt != null) insertShowtimesStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public List<Map<String, Object>> getAllMovies() {
        List<Map<String, Object>> movies = new ArrayList<>();
        String query = "SELECT m.id, m.movie_title AS title, m.movie_poster AS poster, m.movie_description AS description, " +
                "GROUP_CONCAT(s.display_time SEPARATOR ', ') AS showtimes " +
                "FROM movies m " +
                "LEFT JOIN movie_showtime ms ON m.id = ms.movie_id " +
                "LEFT JOIN showtimes s ON ms.showtime_id = s.id " +
                "GROUP BY m.id";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> movie = new HashMap<>();
                movie.put("id", rs.getInt("id"));
                movie.put("title", rs.getString("title"));
                movie.put("poster", rs.getString("poster"));
                movie.put("description", rs.getString("description"));
                movie.put("showtimes", rs.getString("showtimes"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public List<Map<String, Object>> getAllShowtimesWithPricing() {
        List<Map<String, Object>> showtimes = new ArrayList<>();
        String query = "SELECT s.id, s.display_time, s.start_time, s.end_time " +
                "FROM showtimes s " +
                "GROUP BY s.id, s.display_time, s.start_time, s.end_time";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> showtime = new HashMap<>();
                showtime.put("id", rs.getInt("id"));
                showtime.put("displayTime", rs.getString("display_time"));
                showtime.put("startTime", rs.getTime("start_time"));
                showtime.put("endTime", rs.getTime("end_time"));
                showtimes.add(showtime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }

    public List<Map<String, Object>> getAllMoviePricing() {
        List<Map<String, Object>> pricingList = new ArrayList<>();
        String query = "SELECT DISTINCT m.id, m.movie_title, " +
                "ms.balcony_ticket_price, ms.normal_ticket_price, " +
                "GROUP_CONCAT(s.display_time SEPARATOR ', ') as showtimes " +
                "FROM movies m " +
                "JOIN movie_showtime ms ON m.id = ms.movie_id " +
                "LEFT JOIN showtimes s ON ms.showtime_id = s.id " +
                "WHERE ms.balcony_ticket_price IS NOT NULL " +
                "AND ms.normal_ticket_price IS NOT NULL " +
                "GROUP BY m.id, m.movie_title, ms.balcony_ticket_price, ms.normal_ticket_price";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> pricing = new HashMap<>();
                pricing.put("movieId", rs.getInt("id"));
                pricing.put("movieTitle", rs.getString("movie_title"));
                pricing.put("balconyPrice", rs.getDouble("balcony_ticket_price"));
                pricing.put("normalPrice", rs.getDouble("normal_ticket_price"));
                pricing.put("showtimes", rs.getString("showtimes"));
                pricingList.add(pricing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pricingList;
    }

    public boolean addShowtime(String displayTime, Time startTime, Time endTime) {
        String query = "INSERT INTO showtimes (display_time, start_time, end_time) VALUES (?, ?, ?)";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, displayTime);
            stmt.setTime(2, startTime);
            stmt.setTime(3, endTime);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateShowtime(int showtimeId, String displayTime, Time startTime, Time endTime) {
        String query = "UPDATE showtimes SET display_time = ?, start_time = ?, end_time = ? WHERE id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, displayTime);
            stmt.setTime(2, startTime);
            stmt.setTime(3, endTime);
            stmt.setInt(4, showtimeId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteShowtime(int showtimeId) {
        String query = "DELETE FROM showtimes WHERE id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, showtimeId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> getShowtimeById(int showtimeId) {
        String query = "SELECT id, display_time, start_time, end_time FROM showtimes WHERE id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, showtimeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> showtime = new HashMap<>();
                    showtime.put("id", rs.getInt("id"));
                    showtime.put("displayTime", rs.getString("display_time"));
                    showtime.put("startTime", rs.getTime("start_time"));
                    showtime.put("endTime", rs.getTime("end_time"));
                    return showtime;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getMoviesForPricing() {
        List<Map<String, Object>> movies = new ArrayList<>();
        String query = "SELECT id, movie_title FROM movies ORDER BY movie_title";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> movie = new HashMap<>();
                movie.put("id", rs.getInt("id"));
                movie.put("title", rs.getString("movie_title"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public List<Map<String, Object>> getShowtimesForMovie(int movieId) {
        List<Map<String, Object>> showtimes = new ArrayList<>();
        String query = "SELECT s.id, s.display_time FROM showtimes s " +
                "JOIN movie_showtime ms ON s.id = ms.showtime_id " +
                "WHERE ms.movie_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> showtime = new HashMap<>();
                    showtime.put("id", rs.getInt("id"));
                    showtime.put("displayTime", rs.getString("display_time"));
                    showtimes.add(showtime);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }

    public boolean addMoviePricing(int movieId, double balconyPrice, double normalPrice, List<Integer> showtimeIds) {
        // First check if pricing already exists
        String checkQuery = "SELECT COUNT(*) FROM movie_showtime WHERE movie_id = ? AND (balcony_ticket_price IS NOT NULL OR normal_ticket_price IS NOT NULL)";
        String updateQuery = "UPDATE movie_showtime SET balcony_ticket_price = ?, normal_ticket_price = ? " +
                "WHERE movie_id = ? AND showtime_id = ?";
        
        try (Connection conn = DBconnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Check existing pricing
                try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, movieId);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        // Pricing exists, throw exception
                        throw new SQLException("Pricing already exists for this movie. Please use update instead.");
                    }
                }
                
                // Add new pricing
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    for (Integer showtimeId : showtimeIds) {
                        updateStmt.setDouble(1, balconyPrice);
                        updateStmt.setDouble(2, normalPrice);
                        updateStmt.setInt(3, movieId);
                        updateStmt.setInt(4, showtimeId);
                        updateStmt.addBatch();
                    }
                    updateStmt.executeBatch();
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add pricing: " + e.getMessage());
        }
    }

    public boolean updateMoviePricing(int movieId, double balconyPrice, double normalPrice, List<Integer> showtimeIds) {
        // First, reset all prices for this movie
        String resetQuery = "UPDATE movie_showtime SET balcony_ticket_price = NULL, normal_ticket_price = NULL " +
                "WHERE movie_id = ?";
        
        String updateQuery = "UPDATE movie_showtime SET balcony_ticket_price = ?, normal_ticket_price = ? " +
                "WHERE movie_id = ? AND showtime_id = ?";
        
        try (Connection conn = DBconnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Reset existing prices
                try (PreparedStatement resetStmt = conn.prepareStatement(resetQuery)) {
                    resetStmt.setInt(1, movieId);
                    resetStmt.executeUpdate();
                }
                
                // Set new prices
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    for (Integer showtimeId : showtimeIds) {
                        updateStmt.setDouble(1, balconyPrice);
                        updateStmt.setDouble(2, normalPrice);
                        updateStmt.setInt(3, movieId);
                        updateStmt.setInt(4, showtimeId);
                        updateStmt.addBatch();
                    }
                    updateStmt.executeBatch();
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMoviePricing(int movieId) {
        String query = "UPDATE movie_showtime SET balcony_ticket_price = NULL, normal_ticket_price = NULL " +
                "WHERE movie_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, movieId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> getMoviePricing(int movieId) {
        String query = "SELECT m.id as movie_id, m.movie_title, " +
                "ms.balcony_ticket_price, ms.normal_ticket_price, " +
                "GROUP_CONCAT(ms.showtime_id) as showtime_ids " +
                "FROM movies m " +
                "JOIN movie_showtime ms ON m.id = ms.movie_id " +
                "WHERE m.id = ? AND ms.balcony_ticket_price IS NOT NULL " +
                "GROUP BY m.id, m.movie_title, ms.balcony_ticket_price, ms.normal_ticket_price";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, movieId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> pricing = new HashMap<>();
                    pricing.put("movieId", rs.getInt("movie_id"));
                    pricing.put("movieTitle", rs.getString("movie_title"));
                    pricing.put("balconyPrice", rs.getDouble("balcony_ticket_price"));
                    pricing.put("normalPrice", rs.getDouble("normal_ticket_price"));
                    
                    // Convert showtime_ids string to List<Integer>
                    String[] showtimeIdStrings = rs.getString("showtime_ids").split(",");
                    List<Integer> showtimeIds = Arrays.stream(showtimeIdStrings)
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    pricing.put("showtimeIds", showtimeIds);
                    
                    return pricing;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getAllMessages(){
        String query = "SELECT * FROM Contact ORDER BY created_at DESC";
        List<Map<String, Object>> messages = new ArrayList<>();

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> message = new HashMap<>();
                message.put("id", rs.getInt("id"));
                message.put("name", rs.getString("name"));
                message.put("email", rs.getString("email"));
                message.put("subject", rs.getString("subject"));
                message.put("message", rs.getString("message"));
                message.put("created_at", formatDateTime(rs.getTimestamp("created_at")));
                messages.add(message);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public boolean clearAllMessages() throws SQLException {
        String query = "DELETE FROM Contact";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private String formatDateTime(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        return sdf.format(timestamp);
    }

    public Map<String, Object> getBookings(int page, int limit, String movieId, String showtimeId, String status) 
            throws SQLException {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> bookings = new ArrayList<>();
        
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT b.ticket_no, b.seat_number, b.status, b.booking_date, ")
                    .append("m.movie_title, u.name as customer_name, s.display_time, ")
                    .append("CASE ")
                    .append("    WHEN b.seat_number REGEXP '^[O-U]' THEN ms.balcony_ticket_price ")
                    .append("    ELSE ms.normal_ticket_price ")
                    .append("END as amount ")
                    .append("FROM bookings b ")
                    .append("JOIN movies m ON b.movie_id = m.id ")
                    .append("JOIN users u ON b.user_id = u.id ")
                    .append("JOIN showtimes s ON b.showtime_id = s.id ")
                    .append("JOIN movie_showtime ms ON (b.movie_id = ms.movie_id AND b.showtime_id = ms.showtime_id) ")
                    .append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        // Add filters
        if (movieId != null && !movieId.isEmpty()) {
            queryBuilder.append("AND b.movie_id = ? ");
            params.add(Integer.parseInt(movieId));
        }
        if (showtimeId != null && !showtimeId.isEmpty()) {
            queryBuilder.append("AND b.showtime_id = ? ");
            params.add(Integer.parseInt(showtimeId));
        }
        if (status != null && !status.isEmpty()) {
            queryBuilder.append("AND b.status = ? ");
            params.add(status);
        }
        
        // Count total records for pagination
        String countQuery = "SELECT COUNT(*) FROM (" + queryBuilder.toString() + ") as count_query";
        
        // Add pagination
        queryBuilder.append("ORDER BY b.booking_date DESC, b.booking_time DESC ")
                    .append("LIMIT ? OFFSET ?");
        params.add(limit);
        params.add((page - 1) * limit);
        
        try (Connection conn = DBconnection.getConnection()) {
            // Get total count
            try (PreparedStatement countStmt = conn.prepareStatement(countQuery)) {
                for (int i = 0; i < params.size() - 2; i++) {
                    countStmt.setObject(i + 1, params.get(i));
                }
                ResultSet countRs = countStmt.executeQuery();
                countRs.next();
                int totalRecords = countRs.getInt(1);
                result.put("totalPages", (int) Math.ceil((double) totalRecords / limit));
            }
            
            // Get bookings
            try (PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Map<String, Object> booking = new HashMap<>();
                    booking.put("ticketNo", rs.getInt("ticket_no"));
                    booking.put("movieTitle", rs.getString("movie_title"));
                    booking.put("customerName", rs.getString("customer_name"));
                    booking.put("showtime", rs.getString("display_time"));
                    booking.put("seatNumber", rs.getString("seat_number"));
                    booking.put("amount", rs.getDouble("amount"));
                    booking.put("status", rs.getString("status"));
                    booking.put("bookingDate", rs.getDate("booking_date"));
                    bookings.add(booking);
                }
            }
            
            result.put("bookings", bookings);
            return result;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean deleteBookings(List<Integer> ticketNos) throws SQLException {
        String query = "DELETE FROM bookings WHERE ticket_no IN (" + 
                       String.join(",", Collections.nCopies(ticketNos.size(), "?")) + ")";
                       
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            for (int i = 0; i < ticketNos.size(); i++) {
                stmt.setInt(i + 1, ticketNos.get(i));
            }
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == ticketNos.size();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Map<String, Object>> getAllShowtimes() throws SQLException {
        List<Map<String, Object>> showtimes = new ArrayList<>();
        String query = "SELECT id, display_time FROM showtimes ORDER BY start_time";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> showtime = new HashMap<>();
                showtime.put("id", rs.getInt("id"));
                showtime.put("display_time", rs.getString("display_time"));
                showtimes.add(showtime);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        
        return showtimes;
    }

    // Add this method to get movies for the filter
    public List<Map<String, Object>> getMoviesForFilter() throws SQLException {
        List<Map<String, Object>> movies = new ArrayList<>();
        String query = "SELECT DISTINCT m.id, m.movie_title " +
                       "FROM movies m " +
                       "JOIN movie_showtime ms ON m.id = ms.movie_id " +
                       "ORDER BY m.movie_title";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> movie = new HashMap<>();
                movie.put("id", rs.getInt("id"));
                movie.put("movie_title", rs.getString("movie_title"));
                movies.add(movie);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        
        return movies;
    }

    // Add this method to get showtimes for the filter
    public List<Map<String, Object>> getShowtimesForFilter() throws SQLException {
        List<Map<String, Object>> showtimes = new ArrayList<>();
        // Get all showtimes from showtimes table
        String query = "SELECT id, display_time, start_time " +
                       "FROM showtimes " +
                       "ORDER BY start_time";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> showtime = new HashMap<>();
                showtime.put("id", rs.getInt("id"));
                showtime.put("display_time", rs.getString("display_time"));
                showtimes.add(showtime);
            }
            
            System.out.println("Found " + showtimes.size() + " showtimes");
            
        } catch (SQLException e) {
            System.err.println("SQL Error in getShowtimesForFilter: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        
        return showtimes;
    }

}
