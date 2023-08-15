package vn.fs.api;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.fs.entity.Category;
import vn.fs.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CategoryApiTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryApi categoryApi;



    @Test
    void testGetAll() {
        // Create a list of categories to return from the mock repository
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1L, "Category 1"));
        categories.add(new Category(2L, "Category 2"));

        // Mock the behavior of categoryRepository.findAll()
        when(categoryRepository.findAll()).thenReturn(categories);

        // Call the method under test
        ResponseEntity<List<Category>> response = categoryApi.getAll();

        // Verify the expected behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody());
    }

    @Test
    void testGetById_CategoryExists() {
        Long categoryId = 1L;

        // Create a category to return from the mock repository
        Category category = new Category(categoryId, "Category 1");

        // Mock the behavior of categoryRepository.existsById(categoryId)
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        // Mock the behavior of categoryRepository.findById(categoryId)
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Call the method under test
        ResponseEntity<Category> response = categoryApi.getById(categoryId);

        // Verify the expected behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(category, response.getBody());
    }

    @Test
    void testGetById_CategoryDoesNotExist() {
        Long categoryId = 1L;

        // Mock the behavior of categoryRepository.existsById(categoryId)
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        // Call the method under test
        ResponseEntity<Category> response = categoryApi.getById(categoryId);

        // Verify the expected behavior
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testPost() {
        // Create a new category to post
        Category newCategory = new Category(1L, "New Category");

        // Mock the behavior of categoryRepository.existsById(categoryId)
        when(categoryRepository.existsById(newCategory.getCategoryId())).thenReturn(false);

        // Mock the behavior of categoryRepository.save(category)
        when(categoryRepository.save(newCategory)).thenReturn(newCategory);

        // Call the method under test
        ResponseEntity<Category> response = categoryApi.post(newCategory);

        // Verify the expected behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newCategory, response.getBody());
    }

    @Test
    void testPost_CategoryAlreadyExists() {
        // Create a category that already exists
        Category existingCategory = new Category(1L, "Existing Category");

        // Mock the behavior of categoryRepository.existsById(categoryId)
        when(categoryRepository.existsById(existingCategory.getCategoryId())).thenReturn(true);

        // Call the method under test
        ResponseEntity<Category> response = categoryApi.post(existingCategory);

        // Verify the expected behavior
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testPut_CategoryExists() {
        Long categoryId = 1L;

        // Create a category to update
        Category updatedCategory = new Category(categoryId, "Updated Category");

        // Mock the behavior of categoryRepository.existsById(categoryId)
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        // Mock the behavior of categoryRepository.save(category)
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);

        // Call the method under test
        ResponseEntity<Category> response = categoryApi.put(updatedCategory, categoryId);

        // Verify the expected behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCategory, response.getBody());
    }

    @Test
    void testPut_CategoryDoesNotExist() {
        Long categoryId = 1L;

        // Create a category that does not exist
        Category nonExistingCategory = new Category(1L, "Non-existing Category");

        // Mock the behavior of categoryRepository.existsById(categoryId)
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        // Call the method under test
        ResponseEntity<Category> response = categoryApi.put(nonExistingCategory, categoryId);

        // Verify the expected behavior
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testPut_InvalidCategoryId() {
        Long categoryId = 1L;

        // Create a category with a different ID than the provided categoryId
        Category categoryWithInvalidId = new Category(2L, "Category with Invalid ID");

        // Call the method under test
        ResponseEntity<Category> response = categoryApi.put(categoryWithInvalidId, categoryId);

        // Verify the expected behavior
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDelete_CategoryExists() {
        Long categoryId = 1L;

        // Mock the behavior of categoryRepository.existsById(categoryId)
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        // Call the method under test
        ResponseEntity<Void> response = categoryApi.delete(categoryId);

        // Verify the expected behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDelete_CategoryDoesNotExist() {
        Long categoryId = 1L;

        // Mock the behavior of categoryRepository.existsById(categoryId)
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        // Call the method under test
        ResponseEntity<Void> response = categoryApi.delete(categoryId);

        // Verify the expected behavior
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
