package vn.fs.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.fs.config.JwtUtils;
import vn.fs.dto.OrderRequest;
import vn.fs.entity.*;
import vn.fs.repository.*;
import vn.fs.utils.SendMailUtil;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderApiTest {

    @InjectMocks
    OrderApi orderApi;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderDetailRepository orderDetailRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    CartRepository cartRepository;

    @Mock
    CartDetailRepository cartDetailRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    SendMailUtil senMail;

    @Test
    void GivenExistingOrders_FindAll_ReturnListOfOrders() {
        // Given
        List<Order> mockOrders = new ArrayList<>();
        mockOrders.add(new Order());
        mockOrders.add(new Order());

        when(orderRepository.findAllByOrderByOrdersIdDesc()).thenReturn(mockOrders);

        // When
        ResponseEntity<List<Order>> result = orderApi.findAll();

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockOrders, result.getBody());
    }

    @Test
    void GivenExistingOrderId_GetById_ReturnOrder() {
        // Given
        Long orderId = 1L;
        Order mockOrder = new Order();

        when(orderRepository.existsById(orderId)).thenReturn(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        // When
        ResponseEntity<Order> result = orderApi.getById(orderId);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockOrder, result.getBody());
    }

    @Test
    void GivenNonExistingOrderId_GetById_ReturnNotFound() {
        // Given
        Long orderId = 1L;

        when(orderRepository.existsById(orderId)).thenReturn(false);

        // When
        ResponseEntity<Order> result = orderApi.getById(orderId);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void GivenExistingUserEmail_GetByUser_ReturnOrders() {
        // Given
        String userEmail = "test@example.com";
        User mockUser = new User();
        List<Order> mockOrders = new ArrayList<>();

        when(userRepository.existsByEmail(userEmail)).thenReturn(true);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockUser));
        when(orderRepository.findByUserOrderByOrdersIdDesc(mockUser)).thenReturn(mockOrders);

        // When
        ResponseEntity<List<Order>> result = orderApi.getByUser(userEmail);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockOrders, result.getBody());
    }

    @Test
    void GivenNonExistingUserEmail_GetByUser_ReturnNotFound() {
        // Given
        String userEmail = "nonexistent@example.com";

        when(userRepository.existsByEmail(userEmail)).thenReturn(false);

        // When
        ResponseEntity<List<Order>> result = orderApi.getByUser(userEmail);

        // Then
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }
    @Test
    void GivenValidEmailAndCart_checkout_ReturnOrder() {
        // Tạo dữ liệu giả lập cho email và giỏ hàng
        String email = "test@example.com";
        Cart cart = new Cart();
        cart.setCartId(1L);

        // Tạo danh sách sản phẩm trong giỏ hàng
        List<CartDetail> items = new ArrayList<>();
        items.add(new CartDetail(1L, 1, 10.0, new Product(), cart));
        items.add(new CartDetail(2L, 2, 20.0, new Product(), cart));

        // Thiết lập behavior cho các repository
        when(userRepository.existsByEmail(email)).thenReturn(true); // User tồn tại
        when(cartRepository.existsById(cart.getCartId())).thenReturn(true); // Cart tồn tại
        when(cartDetailRepository.findByCart(cart)).thenReturn(items); // Danh sách sản phẩm trong giỏ hàng
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setOrdersId(1L); // Giả sử order được lưu trong database có orderId là 1
            return savedOrder;
        });
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User())); // Tìm user bằng email

        // Thực hiện gọi phương thức để kiểm tra
        ResponseEntity<Order> result = orderApi.checkout(email, cart);

        // Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra response status là OK
        assertNotNull(result.getBody()); // Kiểm tra response body không rỗng
        assertEquals(1L, result.getBody().getOrdersId()); // Kiểm tra orderId của order trả về

        // Kiểm tra xem phương thức delete đã được gọi cho từng CartDetail trong danh sách
        verify(cartDetailRepository, times(items.size())).delete(any(CartDetail.class));

        // Kiểm tra xem phương thức sendMailOrder đã được gọi với tham số là order
        verify(senMail).sendMailOrder(any(Order.class));
    }

    @Test
    void GivenInvalidEmail_checkout_ReturnNotFound() {
        String email = "nonexistent@example.com";
        Cart cart = new Cart();
        cart.setCartId(1L);

        when(userRepository.existsByEmail(email)).thenReturn(false);

        ResponseEntity<Order> result = orderApi.checkout(email, cart);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void GivenInvalidCartId_checkout_ReturnNotFound() {
        String email = "test@example.com";
        Cart cart = new Cart();
        cart.setCartId(1L);

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(cartRepository.existsById(cart.getCartId())).thenReturn(false);

        ResponseEntity<Order> result = orderApi.checkout(email, cart);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void GivenValidEmailAndOrderRequest_checkout_ReturnOrder() {
        String email = "test@example.com";

        // Tạo dữ liệu giả lập cho orderRequest
        Cart cart = new Cart();
        cart.setAddress("123 Main St");
        cart.setPhone("1234567890");
        User user = new User("Test User", email, "hashedPassword", "1234567890", "123 Main St",
                true, true, null, LocalDate.now(), "jwtToken");
        cart.setUser(user);

        List<CartDetail> cartDetails = new ArrayList<>();
        cartDetails.add(new CartDetail(1L, 2, 10.0, new Product(), cart));

        OrderRequest orderRequest = new OrderRequest(cart, cartDetails);

        // Thiết lập behavior cho các repository
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setOrdersId(1L); // Giả sử order được lưu trong database có orderId là 1
            return savedOrder;
        });

        // Thực hiện gọi phương thức để kiểm tra
        ResponseEntity<Order> result = orderApi.checkout(email, orderRequest);

        // Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra response status là OK
        assertNotNull(result.getBody()); // Kiểm tra response body không rỗng
        assertEquals(1L, result.getBody().getOrdersId()); // Kiểm tra orderId của order trả về

        // Kiểm tra xem phương thức sendMailOrder đã được gọi với tham số là order
        verify(senMail).sendMailOrder(any(Order.class));
    }

    @Test
    void GivenValidOrderId_cancel_ReturnOkResponse() {
        Long orderId = 1L;

        // Tạo order giả lập
        Order order = new Order();
        order.setOrdersId(orderId);
        order.setStatus(2); // Giả sử trạng thái order là 2

        // Thiết lập behavior cho orderRepository
        when(orderRepository.existsById(orderId)).thenReturn(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Thực hiện gọi phương thức để kiểm tra
        ResponseEntity<Void> result = orderApi.cancel(orderId);

        // Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra response status là OK

        // Kiểm tra xem orderRepository.save đã được gọi với đúng tham số
        verify(orderRepository).save(order);

        // Kiểm tra xem phương thức sendMailOrderCancel đã được gọi với tham số là order
        verify(senMail).sendMailOrderCancel(order);
    }

    @Test
    void GivenInvalidOrderId_cancel_ReturnNotFoundResponse() {
        Long orderId = 1L;

        // Thiết lập behavior cho orderRepository
        when(orderRepository.existsById(orderId)).thenReturn(false);

        // Thực hiện gọi phương thức để kiểm tra
        ResponseEntity<Void> result = orderApi.cancel(orderId);

        // Kiểm tra kết quả trả về
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra response status là NOT_FOUND

        // Kiểm tra xem orderRepository.save không được gọi
        verify(orderRepository, never()).save(any(Order.class));

        // Kiểm tra xem phương thức sendMailOrderCancel không được gọi
        verify(senMail, never()).sendMailOrderCancel(any(Order.class));
    }

    @Test
    void GivenValidOrderId_deliver_ReturnOkResponse() {
        Long orderId = 1L;

        // Tạo order giả lập
        Order order = new Order();
        order.setOrdersId(orderId);
        order.setStatus(0); // Giả sử trạng thái order là 0

        // Thiết lập behavior cho orderRepository
        when(orderRepository.existsById(orderId)).thenReturn(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Thực hiện gọi phương thức để kiểm tra
        ResponseEntity<Void> result = orderApi.deliver(orderId);

        // Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra response status là OK

        // Kiểm tra xem orderRepository.save đã được gọi với đúng tham số
        verify(orderRepository).save(order);

        // Kiểm tra xem phương thức sendMailOrderDeliver đã được gọi với tham số là order
        verify(senMail).sendMailOrderDeliver(order);
    }

    @Test
    void GivenInvalidOrderId_deliver_ReturnNotFoundResponse() {
        Long orderId = 1L;

        // Thiết lập behavior cho orderRepository
        when(orderRepository.existsById(orderId)).thenReturn(false);

        // Thực hiện gọi phương thức để kiểm tra
        ResponseEntity<Void> result = orderApi.deliver(orderId);

        // Kiểm tra kết quả trả về
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra response status là NOT_FOUND

        // Kiểm tra xem orderRepository.save không được gọi
        verify(orderRepository, never()).save(any(Order.class));

        // Kiểm tra xem phương thức sendMailOrderDeliver không được gọi
        verify(senMail, never()).sendMailOrderDeliver(any(Order.class));
    }

    @Test
    void GivenValidOrderId_success_ReturnOkResponse() {
        Long orderId = 1L;

        // Tạo order giả lập
        Order order = new Order();
        order.setOrdersId(orderId);
        order.setStatus(0); // Giả sử trạng thái order là 0

        // Thiết lập behavior cho orderRepository
        when(orderRepository.existsById(orderId)).thenReturn(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Thực hiện gọi phương thức để kiểm tra
        ResponseEntity<Void> result = orderApi.success(orderId);

        // Kiểm tra kết quả trả về
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra response status là OK

        // Kiểm tra xem orderRepository.save đã được gọi với đúng tham số
        verify(orderRepository).save(order);

        // Kiểm tra xem phương thức sendMailOrderSuccess đã được gọi với tham số là order
        verify(senMail).sendMailOrderSuccess(order);
    }

    @Test
    void GivenInvalidOrderId_success_ReturnNotFoundResponse() {
        Long orderId = 1L;

        // Thiết lập behavior cho orderRepository
        when(orderRepository.existsById(orderId)).thenReturn(false);

        // Thực hiện gọi phương thức để kiểm tra
        ResponseEntity<Void> result = orderApi.success(orderId);

        // Kiểm tra kết quả trả về
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra response status là NOT_FOUND

        // Kiểm tra xem orderRepository.save không được gọi
        verify(orderRepository, never()).save(any(Order.class));

        // Kiểm tra xem phương thức sendMailOrderSuccess không được gọi
        verify(senMail, never()).sendMailOrderSuccess(any(Order.class));
    }

    @Test
    void updateProduct_ExistingOrder_UpdateProductQuantityAndSold() {
        // Tạo dữ liệu giả lập
        Order order = new Order();
        order.setOrdersId(1L);

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setQuantity(2);
        orderDetail1.setProduct(new Product());

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setQuantity(3);
        orderDetail2.setProduct(new Product());

        List<OrderDetail> orderDetails = Arrays.asList(orderDetail1, orderDetail2);

        // Thiết lập behavior cho orderDetailRepository
        when(orderDetailRepository.findByOrder(order)).thenReturn(orderDetails);

        // Thiết lập behavior cho productRepository
        Product product1 = new Product();
        product1.setProductId(101L);
        product1.setQuantity(10);
        product1.setSold(5);

        when(productRepository.findById(any())).thenReturn(Optional.of(product1));

        // Thực hiện gọi phương thức để kiểm tra
        orderApi.updateProduct(order);

        // Kiểm tra xem số lượng và số lượng đã bán của sản phẩm đã được cập nhật đúng chưa
        assertEquals(5, product1.getQuantity());
        assertEquals(10, product1.getSold());
        // Kiểm tra xem productRepository.save đã được gọi đúng số lần
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    void updateProduct_NonExistingOrder_NothingUpdated() {
        // Tạo dữ liệu giả lập
        Order order = new Order();
        order.setOrdersId(1L);

        // Thiết lập behavior cho orderDetailRepository
        when(orderDetailRepository.findByOrder(order)).thenReturn(Collections.emptyList());

        // Thực hiện gọi phương thức để kiểm tra
        orderApi.updateProduct(order);

        // Kiểm tra xem không có phương thức productRepository.save nào được gọi
        verify(productRepository, never()).save(any(Product.class));
    }

}