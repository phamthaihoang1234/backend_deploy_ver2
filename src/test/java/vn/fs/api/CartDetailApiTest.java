package vn.fs.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.fs.entity.Cart;
import vn.fs.entity.CartDetail;
import vn.fs.entity.Product;
import vn.fs.repository.CartDetailRepository;
import vn.fs.repository.CartRepository;
import vn.fs.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartDetailApiTest {

    @InjectMocks
    CartDetailApi cartDetailApi;

    @Mock
    CartDetailRepository cartDetailRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    ProductRepository productRepository;

    @Test
    void GivenIdNotExactValue_getByCartId_ReturnNotFound() {
        // Input value
        Long cartId = -1L;
        when(cartRepository.existsById(cartId)).thenReturn(false); // Mock không tìm thấy cart

        // Thực hiện phương thức kiểm thử
        ResponseEntity<List<CartDetail>> result = cartDetailApi.getByCartId(cartId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
    }

    @Test
    void GivenIdExactValue_getByCartId_ResultCardExact() {
        // Input value
        Long cartId = 1L;
        when(cartRepository.existsById(cartId)).thenReturn(true); // Mock tìm thấy cart

        // Mock list of cart details
        List<CartDetail> mockCartDetails = new ArrayList<>(); // Giả lập danh sách cart details
        when(cartDetailRepository.findByCart(any())).thenReturn(mockCartDetails); // Mock phương thức findByCart

        Cart mockCart = new Cart(); // Giả lập đối tượng User
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(mockCart)); // Mock phương thức findByEmail

        // Thực hiện phương thức kiểm thử
        ResponseEntity<List<CartDetail>> result = cartDetailApi.getByCartId(cartId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertSame(mockCartDetails, result.getBody()); // Kiểm tra danh sách cart details trả về
    }

    @Test
    void GivenNonExistentId_getOne_ReturnNotFound() {
        // Input value
        Long cartDetailId = -1L;
        when(cartDetailRepository.existsById(cartDetailId)).thenReturn(false); // Mock không tìm thấy cart detail

        // Thực hiện phương thức kiểm thử
        ResponseEntity<CartDetail> result = cartDetailApi.getOne(cartDetailId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null khi không tìm thấy cart detail
    }

    @Test
    void GivenExistingId_getOne_ReturnCartDetail() {
        // Input value
        Long cartDetailId = 1L;
        CartDetail mockCartDetail = new CartDetail(); // Tạo đối tượng giả lập cho cart detail
        when(cartDetailRepository.existsById(cartDetailId)).thenReturn(true); // Mock tìm thấy cart detail
        when(cartDetailRepository.findById(cartDetailId)).thenReturn(Optional.of(mockCartDetail)); // Mock phương thức findById

        // Thực hiện phương thức kiểm thử
        ResponseEntity<CartDetail> result = cartDetailApi.getOne(cartDetailId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertSame(mockCartDetail, result.getBody()); // Kiểm tra cart detail trả về là đúng cart detail giả lập
    }

    @Test
    void GivenNonExistentCart_post_ReturnNotFound() {
        // Input value
        CartDetail detail = new CartDetail();
        Cart cart = new Cart();
        cart.setCartId(-1L);
        detail.setCart(cart);

        when(cartRepository.existsById(cart.getCartId())).thenReturn(false); // Mock không tìm thấy cart

        // Thực hiện phương thức kiểm thử
        ResponseEntity<CartDetail> result = cartDetailApi.post(detail);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null khi không tìm thấy cart
    }

    @Test
    void GivenNonExistentProduct_post_ReturnNotFound() {
        // Input value
        CartDetail detail = new CartDetail();
        Cart cart = new Cart();
        Product product = new Product();
        cart.setCartId(1L);
        product.setProductId(1L);
        detail.setCart(cart);
        detail.setProduct(product);
        detail.setPrice(100.0);

        when(cartRepository.existsById(cart.getCartId())).thenReturn(true); // Mock tìm thấy cart
        when(productRepository.findByStatusTrue()).thenReturn(new ArrayList<>()); // Mock danh sách product trống

        // Thực hiện phương thức kiểm thử
        ResponseEntity<CartDetail> result = cartDetailApi.post(detail);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null khi không tìm thấy product
    }

    @Test
    void GivenExistingProduct_post_ReturnUpdatedCartDetail() {
        // Input value
        CartDetail detail = new CartDetail();
        Cart cart = new Cart();
        Product product = new Product();
        cart.setCartId(1L);
        product.setProductId(1L);
        detail.setCart(cart);
        detail.setProduct(product);
        detail.setPrice(100.0);

        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(product);

        when(cartRepository.existsById(cart.getCartId())).thenReturn(true); // Mock tìm thấy cart
        when(productRepository.findByStatusTrue()).thenReturn(mockProducts); // Mock danh sách product
        when(productRepository.findByProductIdAndStatusTrue(product.getProductId())).thenReturn(product); // Mock tìm thấy product

        Cart cartMock = new Cart();
        cartMock.setCartId(1L);
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cartMock));

        List<CartDetail> mockCartDetails = new ArrayList<>();
        when(cartDetailRepository.findByCart(cart)).thenReturn(mockCartDetails); // Mock danh sách cart details rỗng

        //mock save
        CartDetail cartDetailDbMock = new CartDetail();
        cartDetailDbMock.setQuantity(4);
        cartDetailDbMock.setPrice(400.0);
        when(cartDetailRepository.save(any())).thenReturn(cartDetailDbMock);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<CartDetail> result = cartDetailApi.post(detail);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(4, result.getBody().getQuantity()); // Kiểm tra danh sách cart details có 1 phần tử
    }

    @Test
    void GivenExistingProduct_post_ReturnNewCartDetail() {
        // Input value
        CartDetail detail = new CartDetail();
        Cart cart = new Cart();
        Product product = new Product();
        cart.setCartId(1L);
        product.setProductId(1L);
        detail.setCart(cart);
        detail.setProduct(product);
        detail.setPrice(100.0);

        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(product);

        when(cartRepository.existsById(cart.getCartId())).thenReturn(true); // Mock tìm thấy cart
        when(productRepository.findByStatusTrue()).thenReturn(mockProducts); // Mock danh sách product
        when(productRepository.findByProductIdAndStatusTrue(product.getProductId())).thenReturn(product); // Mock tìm thấy product

        Cart cartMock = new Cart();
        cartMock.setCartId(1L);
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cartMock));

        List<CartDetail> mockCartDetails = new ArrayList<>();
        CartDetail existingCartDetail = new CartDetail();
        existingCartDetail.setProduct(product);
        existingCartDetail.setQuantity(2);
        existingCartDetail.setPrice(200.0);
        mockCartDetails.add(existingCartDetail);
        when(cartDetailRepository.findByCart(cart)).thenReturn(mockCartDetails); // Mock danh sách cart details với 1 phần tử

        //mock save
        CartDetail cartDetailDbMock = new CartDetail();
        cartDetailDbMock.setQuantity(4);
        cartDetailDbMock.setPrice(400.0);
        when(cartDetailRepository.save(any())).thenReturn(cartDetailDbMock);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<CartDetail> result = cartDetailApi.post(detail);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(1, mockCartDetails.size()); // Kiểm tra danh sách cart details không tăng
        assertEquals(existingCartDetail.getQuantity() + 1, result.getBody().getQuantity()); // Kiểm tra quantity đã tăng lên
        assertEquals(existingCartDetail.getPrice() + detail.getPrice(), result.getBody().getPrice()); // Kiểm tra giá tiền đã cập nhật
    }

    @Test
    void GivenNonExistentCart_put_ReturnNotFound() {
        // Input value
        CartDetail detail = new CartDetail();
        Cart cart = new Cart();
        cart.setCartId(-1L);
        detail.setCart(cart);

        when(cartRepository.existsById(cart.getCartId())).thenReturn(false); // Mock không tìm thấy cart

        // Thực hiện phương thức kiểm thử
        ResponseEntity<CartDetail> result = cartDetailApi.put(detail);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null khi không tìm thấy cart
    }

    @Test
    void GivenExistingCart_put_ReturnUpdatedCartDetail() {
        // Input value
        CartDetail detail = new CartDetail();
        Cart cart = new Cart();
        cart.setCartId(1L);
        detail.setCart(cart);

        when(cartRepository.existsById(cart.getCartId())).thenReturn(true); // Mock tìm thấy cart
        when(cartDetailRepository.save(detail)).thenReturn(detail); // Mock phương thức save

        // Thực hiện phương thức kiểm thử
        ResponseEntity<CartDetail> result = cartDetailApi.put(detail);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertSame(detail, result.getBody()); // Kiểm tra cart detail trả về là cart detail giả lập
    }

    @Test
    void GivenNonExistentCartDetail_delete_ReturnNotFound() {
        // Input value
        Long cartDetailId = -1L;
        when(cartDetailRepository.existsById(cartDetailId)).thenReturn(false); // Mock không tìm thấy cart detail

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Void> result = cartDetailApi.delete(cartDetailId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null khi không tìm thấy cart detail
    }

    @Test
    void GivenExistingCartDetail_delete_ReturnOk() {
        // Input value
        Long cartDetailId = 1L;
        when(cartDetailRepository.existsById(cartDetailId)).thenReturn(true); // Mock tìm thấy cart detail

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Void> result = cartDetailApi.delete(cartDetailId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNull(result.getBody()); // Kiểm tra body là null
    }
}