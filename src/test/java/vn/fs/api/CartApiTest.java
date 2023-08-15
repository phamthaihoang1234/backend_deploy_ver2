package vn.fs.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.fs.entity.Cart;
import vn.fs.entity.User;
import vn.fs.repository.CartRepository;
import vn.fs.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartApiTest {

    @InjectMocks
    CartApi cartApi;

    @Mock
    CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void GivenEmailNotFound_getCartUser_ResultNotFoundMessage() {
        // Input value
        String emailInput = "";
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Cart> result = cartApi.getCartUser(emailInput);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
    }

    @Test
    void GivenEmailExactValue_getCartUser_ResultCartObject() {
        // Input value
        String emailInput = "existing_email@example.com";
        when(userRepository.existsByEmail(anyString())).thenReturn(true); // Mock tìm thấy email

        // Mock cart object
        Cart mockCart = new Cart(); // Giả lập đối tượng Cart
        when(cartRepository.findByUser(any())).thenReturn(mockCart); // Mock phương thức findByUser

        // Mock user object
        User mockUser = new User(); // Giả lập đối tượng User
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser)); // Mock phương thức findByEmail


        // Thực hiện phương thức kiểm thử
        ResponseEntity<Cart> result = cartApi.getCartUser(emailInput);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertSame(mockCart, result.getBody()); // Kiểm tra đối tượng Cart trả về
    }

    @Test
    void GivenEmailNotFound_putCartUser_ResultNotFoundMessage() {
        // Input value
        String emailInput = "";
        Cart cart = new Cart(); // Đối tượng cart dùng để test
        when(userRepository.existsByEmail(anyString())).thenReturn(false); // Mock không tìm thấy email

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Cart> result = cartApi.putCartUser(emailInput, cart);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
    }

    @Test
    void GivenEmailFound_putCartUser_ResultCartObject() {
        // Input value
        String emailInput = "existing_email@example.com";
        Cart cart = new Cart(); // Đối tượng cart dùng để test
        when(userRepository.existsByEmail(anyString())).thenReturn(true); // Mock tìm thấy email

        // Mock cart object
        Cart mockSavedCart = new Cart(); // Giả lập đối tượng Cart sau khi được lưu
        when(cartRepository.save(any())).thenReturn(mockSavedCart); // Mock phương thức save

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Cart> result = cartApi.putCartUser(emailInput, cart);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertSame(mockSavedCart, result.getBody()); // Kiểm tra đối tượng Cart trả về sau khi lưu
    }
}