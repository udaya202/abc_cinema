<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="movies-container">
    <c:forEach var="movie" items="${movies}">
        <div class="movie-card" data-movie-id="${movie.id}">
            <div class="movie-poster">
                <img src="${movie.poster}" alt="${movie.title}">
            </div>
            <div class="movie-details">
                <h3>${movie.title}</h3>
                <p>${movie.description}</p>
                <div class="movie-showtimes">
                    <strong>Showtimes:</strong> ${movie.showtimes}
                </div>
                <div class="movie-actions">
                    <button class="edit-btn" onclick="editMovie(${movie.id})">
                        <i class="bi bi-pencil"></i>
                        Edit
                    </button>
                    <button class="delete-btn" onclick="deleteMovie(${movie.id})">
                        <i class="bi bi-trash"></i>
                        Delete
                    </button>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

<!-- Delete Confirmation Modal -->
<div id="deleteConfirmModal" class="modal">
    <div class="modal-content">
        <h2>Confirm Delete</h2>
        <p>Are you sure you want to delete this movie?</p>
        <div class="modal-actions">
            <button onclick="confirmDelete()" class="delete-btn">Delete</button>
            <button onclick="closeDeleteModal()" class="cancel-btn">Cancel</button>
        </div>
    </div>
</div> 