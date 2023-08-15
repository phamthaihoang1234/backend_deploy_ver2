package vn.fs.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import vn.fs.entity.Favorite;
import vn.fs.entity.Product;
import vn.fs.entity.User;
import vn.fs.repository.FavoriteRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritesApiTest {

    @InjectMocks
    FavoritesApi favoritesApi;

    @Mock
    FavoriteRepository favoriteRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ProductRepository productRepository;

    @Test
    void Given_ExistingEmail_When_findByEmail_Then_ReturnFavoritesList() {
        // Given
        String email = "test@example.com";
        User user = new User();
        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        List<Favorite> favorites = new ArrayList<>();
        when(favoriteRepository.findByUser(user)).thenReturn(favorites);

        // When
        ResponseEntity<List<Favorite>> response = favoritesApi.findByEmail(email);

        // Then
        verify(userRepository).existsByEmail(email);
        verify(userRepository).findByEmail(email);
        verify(favoriteRepository).findByUser(user);
        assertEquals(200, response.getStatusCodeValue());
        assertSame(favorites, response.getBody());
    }

    @Test
    void Given_NonExistingEmail_When_findByEmail_Then_ReturnNotFound() {
        // Given
        String email = "nonexisting@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // When
        ResponseEntity<List<Favorite>> response = favoritesApi.findByEmail(email);

        // Then
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).findByEmail(email);
        verify(favoriteRepository, never()).findByUser(any());
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void Given_ExistingProductId_When_findByProduct_Then_ReturnCount() {
        // Given
        Long productId = 123L;
        Product product = new Product();
        when(productRepository.existsById(productId)).thenReturn(true);
        when(productRepository.getById(productId)).thenReturn(product);

        int count = 5;
        when(favoriteRepository.countByProduct(product)).thenReturn(count);

        // When
        ResponseEntity<Integer> response = favoritesApi.findByProduct(productId);

        // Then
        verify(productRepository).existsById(productId);
        verify(productRepository).getById(productId);
        verify(favoriteRepository).countByProduct(product);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(count, response.getBody());
    }

    @Test
    void Given_NonExistingProductId_When_findByProduct_Then_ReturnNotFound() {
        // Given
        Long productId = 456L;
        when(productRepository.existsById(productId)).thenReturn(false);

        // When
        ResponseEntity<Integer> response = favoritesApi.findByProduct(productId);

        // Then
        verify(productRepository).existsById(productId);
        verify(productRepository, never()).getById(any());
        verify(favoriteRepository, never()).countByProduct(any());
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void Given_ExistingProductAndUser_When_findByProductAndUser_Then_ReturnFavorite() {
        // Given
        Long productId = 123L;
        String userEmail = "user@example.com";
        Product product = new Product();
        User user = new User();

        when(userRepository.existsByEmail(userEmail)).thenReturn(true);
        when(productRepository.existsById(productId)).thenReturn(true);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Favorite expectedFavorite = new Favorite();
        when(favoriteRepository.findByProductAndUser(product, user)).thenReturn(expectedFavorite);

        // When
        ResponseEntity<Favorite> response = favoritesApi.findByProductAndUser(productId, userEmail);

        // Then
        verify(userRepository).existsByEmail(userEmail);
        verify(productRepository).existsById(productId);
        verify(userRepository).findByEmail(userEmail);
        verify(productRepository).findById(productId);
        verify(favoriteRepository).findByProductAndUser(product, user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedFavorite, response.getBody());
    }

    @Test
    void Given_NonExistingProductOrUser_When_findByProductAndUser_Then_ReturnNotFound() {
        // Given
        Long productId = 456L;
        String userEmail = "nonexistent@example.com";

        lenient().when(userRepository.existsByEmail(userEmail)).thenReturn(false);
        lenient().when(productRepository.existsById(productId)).thenReturn(false);

        // When
        ResponseEntity<Favorite> response = favoritesApi.findByProductAndUser(productId, userEmail);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void Given_ValidFavorite_When_post_Then_ReturnAddedFavorite() {
        // Given
        Favorite favorite = new Favorite();

        when(favoriteRepository.save(any(Favorite.class))).thenReturn(favorite);

        // When
        ResponseEntity<Favorite> response = favoritesApi.post(favorite);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(favorite, response.getBody());
    }

    @Test
    void Given_ExistingFavorite_When_delete_Then_ReturnSuccess() {
        // Given
        Long favoriteId = 123L;

        when(favoriteRepository.existsById(favoriteId)).thenReturn(true);

        // When
        ResponseEntity<Void> response = favoritesApi.delete(favoriteId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(favoriteRepository).deleteById(favoriteId);
    }

    @Test
    void Given_NonExistingFavorite_When_delete_Then_ReturnNotFound() {
        // Given
        Long favoriteId = 456L;

        // When
        ResponseEntity<Void> response = favoritesApi.delete(favoriteId);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(favoriteRepository, never()).deleteById(anyLong());
    }
}