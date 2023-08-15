package vn.fs.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.fs.entity.Category;
import vn.fs.entity.Product;
import vn.fs.repository.CategoryRepository;
import vn.fs.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductApiTest {

    @InjectMocks
    ProductApi productApi;

    @Mock
    ProductRepository repo;

    @Mock
    CategoryRepository cRepo;


    @Test
    void GivenUsersExist_getAll_ReturnProductsList() {
        // Input value
        Product pro1 = new Product();
        pro1.setProductId(1L);
        pro1.setStatus(true);

        Product pro2 = new Product();
        pro2.setProductId(1L);
        pro2.setStatus(true);

        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(pro1);
        mockProducts.add(pro2);


        when(repo.findByStatusTrue()).thenReturn(mockProducts); // Mock danh sách user

        // Thực hiện phương thức kiểm thử
        ResponseEntity<List<Product>> result = productApi.getAll();

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(mockProducts.size(), result.getBody().size()); // Kiểm tra danh sách user trả về có số lượng phù hợp
        assertSame(mockProducts, result.getBody()); // Kiểm tra danh sách user trả về là danh sách user giả lập
    }

    @Test
    void GivenUsersExist_getBestSeller_ReturnProductsList() {
        // Input value
        Product pro1 = new Product();
        pro1.setProductId(1L);
        pro1.setStatus(true);

        Product pro2 = new Product();
        pro2.setProductId(1L);
        pro2.setStatus(true);

        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(pro1);
        mockProducts.add(pro2);


        when(repo.findByStatusTrueOrderBySoldDesc()).thenReturn(mockProducts); // Mock danh sách user

        // Thực hiện phương thức kiểm thử
        ResponseEntity<List<Product>> result = productApi.getBestSeller();

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(mockProducts.size(), result.getBody().size()); // Kiểm tra danh sách user trả về có số lượng phù hợp
        assertSame(mockProducts, result.getBody()); // Kiểm tra danh sách user trả về là danh sách user giả lập
    }

    @Test
    void GivenUsersExist_getBestSellerAdmin_ReturnProductsList() {
        // Input value
        Product pro1 = new Product();
        pro1.setProductId(1L);
        pro1.setStatus(true);

        Product pro2 = new Product();
        pro2.setProductId(1L);
        pro2.setStatus(true);

        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(pro1);
        mockProducts.add(pro2);


        when(repo.findTop10ByOrderBySoldDesc()).thenReturn(mockProducts); // Mock danh sách user

        // Thực hiện phương thức kiểm thử
        ResponseEntity<List<Product>> result = productApi.getBestSellerAdmin();

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(mockProducts.size(), result.getBody().size()); // Kiểm tra danh sách user trả về có số lượng phù hợp
        assertSame(mockProducts, result.getBody()); // Kiểm tra danh sách user trả về là danh sách user giả lập
    }

    @Test
    void GivenUsersExist_getLasted_ReturnProductsList() {
        // Input value
        Product pro1 = new Product();
        pro1.setProductId(1L);
        pro1.setStatus(true);

        Product pro2 = new Product();
        pro2.setProductId(1L);
        pro2.setStatus(true);

        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(pro1);
        mockProducts.add(pro2);


        when(repo.findByStatusTrueOrderByEnteredDateDesc()).thenReturn(mockProducts); // Mock danh sách user

        // Thực hiện phương thức kiểm thử
        ResponseEntity<List<Product>> result = productApi.getLasted();

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(mockProducts.size(), result.getBody().size()); // Kiểm tra danh sách user trả về có số lượng phù hợp
        assertSame(mockProducts, result.getBody()); // Kiểm tra danh sách user trả về là danh sách user giả lập
    }

    @Test
    void GivenUsersExist_getRated_ReturnProductsList() {
        // Input value
        Product pro1 = new Product();
        pro1.setProductId(1L);
        pro1.setStatus(true);

        Product pro2 = new Product();
        pro2.setProductId(1L);
        pro2.setStatus(true);

        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(pro1);
        mockProducts.add(pro2);


        when(repo.findProductRated()).thenReturn(mockProducts); // Mock danh sách user

        // Thực hiện phương thức kiểm thử
        ResponseEntity<List<Product>> result = productApi.getRated();

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(mockProducts.size(), result.getBody().size()); // Kiểm tra danh sách user trả về có số lượng phù hợp
        assertSame(mockProducts, result.getBody()); // Kiểm tra danh sách user trả về là danh sách user giả lập
    }


    @Test
    void GivenUsersExist_suggest_ReturnProductsList() {

        Long categoryId = 1L;
        Long productId = 2L;


        // Input value
        Product pro1 = new Product();
        pro1.setProductId(1L);
        pro1.setStatus(true);

        Product pro2 = new Product();
        pro2.setProductId(1L);
        pro2.setStatus(true);

        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(pro1);
        mockProducts.add(pro2);


        when(repo.findProductSuggest(categoryId, productId, categoryId, categoryId)).thenReturn(mockProducts); // Mock danh sách user

        // Thực hiện phương thức kiểm thử
        ResponseEntity<List<Product>> result = productApi.suggest(categoryId, productId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(mockProducts.size(), result.getBody().size()); // Kiểm tra danh sách user trả về có số lượng phù hợp
        assertSame(mockProducts, result.getBody()); // Kiểm tra danh sách user trả về là danh sách user giả lập
    }

    @Test
    void GivenUsersExist_getByCategory_ReturnProductsList() {
        Long categoryId = 1L;

        // Mocking the Category and Products
        Category category = new Category();
        category.setCategoryId(categoryId);

        Product pro1 = new Product();
        pro1.setProductId(1L);
        pro1.setStatus(true);

        Product pro2 = new Product();
        pro2.setProductId(2L);
        pro2.setStatus(true);

        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(pro1);
        mockProducts.add(pro2);

        // Mocking the behavior of the repositories
        when(cRepo.existsById(categoryId)).thenReturn(true);
        when(cRepo.findById(categoryId)).thenReturn(Optional.of(category));
        when(repo.findByCategory(category)).thenReturn(mockProducts);

        // Call the API method
        ResponseEntity<List<Product>> result = productApi.getByCategory(categoryId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(mockProducts.size(), result.getBody().size()); // Kiểm tra danh sách user trả về có số lượng phù hợp
        assertSame(mockProducts, result.getBody()); // Kiểm tra danh sách user trả về là danh sách user giả lập
    }

    @Test
    void givenInvalidCategoryId_shouldReturnNotFound() {
        Long categoryId = 1L;

        // Mocking the behavior of the repository
        when(cRepo.existsById(categoryId)).thenReturn(false);

        // Call the API method
        ResponseEntity<List<Product>> response = productApi.getByCategory(categoryId);

        // Assert the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void GivenNonExistentProductId_getOne_ReturnNotFound() {
        // Input value
        Long productId = -1L;
        when(repo.existsById(productId)).thenReturn(false); // Mock không tìm thấy user

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Product> result = productApi.getById(productId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null khi không tìm thấy user
    }

    @Test
    void GivenExistingProductId_getOne_ReturnProduct() {
        // Input value
        Long productId = 1L;
        Product mockProduct = new Product();
        mockProduct.setProductId(productId);
        when(repo.existsById(productId)).thenReturn(true); // Mock tìm thấy user
        when(repo.findById(productId)).thenReturn(Optional.of(mockProduct)); // Mock phương thức findById

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Product> result = productApi.getById(productId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertSame(mockProduct, result.getBody()); // Kiểm tra user trả về là user giả lập
    }



    @Test
    void GivenNewProduct_post_ReturnOkResponse() {
        // Input value
        Product newProduct = new Product();
        newProduct.setProductId(1L);
        newProduct.setName("Test Product");
        newProduct.setPrice(100.0);
        newProduct.setDescription("Test description");
        newProduct.setCategory(new Category()); // Để đơn giản, chúng ta tạo một đối tượng Category trống

        // Giả lập kiểm tra không tìm thấy sản phẩm theo productId
        when(repo.existsById(newProduct.getProductId())).thenReturn(false);

        // Giả lập phương thức save trả về sản phẩm mới
        when(repo.save(newProduct)).thenReturn(newProduct);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Product> result = productApi.post(newProduct);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body không null
        assertEquals(newProduct, result.getBody()); // Kiểm tra body là sản phẩm mới
    }

    @Test
    void GivenExistingProduct_post_ReturnBadRequestResponse() {
        // Input value
        Product existingProduct = new Product();
        existingProduct.setProductId(1L);
        existingProduct.setName("Existing Product");
        existingProduct.setPrice(200.0);
        existingProduct.setDescription("Existing description");
        existingProduct.setCategory(new Category()); // Để đơn giản, chúng ta tạo một đối tượng Category trống

        // Giả lập kiểm tra tìm thấy sản phẩm theo productId
        when(repo.existsById(existingProduct.getProductId())).thenReturn(true);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Product> result = productApi.post(existingProduct);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode()); // Kiểm tra status code là BAD_REQUEST
        assertNull(result.getBody()); // Kiểm tra body là null vì có lỗi xảy ra
    }

    @Test
    void GivenMatchingProductId_put_ReturnOkResponse() {
        // Input value
        Long productId = 1L;

        Product product = new Product();
        product.setProductId(productId);

        Product existingProduct = new Product();
        existingProduct.setProductId(productId);
        existingProduct.setName("Existing Product");
        existingProduct.setPrice(200.0);
        existingProduct.setDescription("Existing description");
        existingProduct.setCategory(new Category()); // Để đơn giản, chúng ta tạo một đối tượng Category trống

        // Giả lập kiểm tra product có id trùng với path variable id
        when(repo.existsById(productId)).thenReturn(true);

        // Giả lập phương thức save trả về sản phẩm đã được cập nhật
        when(repo.save(product)).thenReturn(product);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Product> result = productApi.put(productId, product);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body không null
        assertEquals(product, result.getBody()); // Kiểm tra body là sản phẩm đã được cập nhật
    }

    @Test
    void GivenMismatchedProductId_put_ReturnBadRequestResponse() {
        // Input value
        Long productId = 1L;

        Product product = new Product();
        product.setProductId(2L); // Thiết lập product id không trùng với path variable id

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Product> result = productApi.put(productId, product);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode()); // Kiểm tra status code là BAD_REQUEST
        assertNull(result.getBody()); // Kiểm tra body là null vì có lỗi xảy ra
    }

    @Test
    void GivenNonExistingProductId_put_ReturnNotFoundResponse() {
        // Input value
        Long productId = 1L;

        Product product = new Product();
        product.setProductId(productId);

        // Giả lập kiểm tra product không tồn tại
        when(repo.existsById(productId)).thenReturn(false);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Product> result = productApi.put(productId, product);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null vì có lỗi xảy ra
    }

    @Test
    void GivenExistingProductId_delete_ReturnOkResponse() {
        // Input value
        Long productId = 1L;

        // Giả lập product đã tồn tại
        when(repo.existsById(productId)).thenReturn(true);

        // Giả lập product được trả về khi findById được gọi
        Product product = new Product();
        product.setProductId(productId);
        when(repo.findById(productId)).thenReturn(Optional.of(product));

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Void> result = productApi.delete(productId);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNull(result.getBody()); // Kiểm tra body là null vì không có dữ liệu trả về
        verify(repo).save(product); // Kiểm tra phương thức save được gọi với sản phẩm đã thay đổi trạng thái
    }

    @Test
    void GivenNonExistingProductId_delete_ReturnNotFoundResponse() {
        // Input value
        Long productId = 1L;

        // Giả lập product không tồn tại
        when(repo.existsById(productId)).thenReturn(false);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Void> result = productApi.delete(productId);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null vì có lỗi xảy ra
        verify(repo, never()).save(any()); // Kiểm tra phương thức save không được gọi vì không có sản phẩm nào để cập nhật trạng thái
    }
}