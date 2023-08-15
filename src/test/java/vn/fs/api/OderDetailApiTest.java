package vn.fs.api;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.fs.entity.Order;
import vn.fs.entity.OrderDetail;
import vn.fs.repository.OrderDetailRepository;
import vn.fs.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OderDetailApiTest {


    @InjectMocks
    private OderDetailApi orderController;

    @Mock
    private OrderDetailRepository orderDetailRepository;


    @Mock
    private OrderRepository orderRepository;


    @Test
    public void testGetByOrder_OrderExists() {


        Long orderId = 1L;

        // Mock the behavior of orderRepository.existsById(orderId)
        when(orderRepository.existsById(orderId)).thenReturn(true);

        // Create an Order object to return from the mock repository
        Order order = new Order();
        order.setOrdersId(1L);

        // Mock the behavior of orderRepository.findById(orderId)
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Create a list of OrderDetails to return from the mock repository
        List<OrderDetail> expectedOrderDetails = new ArrayList<>();
        OrderDetail od1 = new OrderDetail();
        od1.setOrderDetailId(1L);

        OrderDetail od2 = new OrderDetail();
        od2.setOrderDetailId(1L);

        expectedOrderDetails.add(od1);
        expectedOrderDetails.add(od2);

        // Mock the behavior of orderDetailRepository.findByOrder(...)
        when(orderDetailRepository.findByOrder(order)).thenReturn(expectedOrderDetails);

        // Call the method under test
        ResponseEntity<List<OrderDetail>> response = orderController.getByOrder(orderId);

        // Verify the expected behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedOrderDetails, response.getBody());
    }

    @Test
    public void testGetByOrder_OrderNotExists() {
        Long orderId = 1L;

        // Mock the behavior of orderRepository.existsById(orderId)
        when(orderRepository.existsById(orderId)).thenReturn(false);

        // Call the method under test
        ResponseEntity<List<OrderDetail>> response = orderController.getByOrder(orderId);

        // Verify the expected behavior
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.hasBody());
    }

    @Test
    public void testGetByOrder_NullOrderDetailReturned() {
        Long orderId = 1L;

        Order order = new Order();
        order.setOrdersId(1L);

        // Mock the behavior of orderRepository.findById(orderId)
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Mock the behavior of orderRepository.existsById(orderId)
        when(orderRepository.existsById(orderId)).thenReturn(true);

        // Mock the behavior of orderDetailRepository.findByOrder(...)
        when(orderDetailRepository.findByOrder(order)).thenReturn(null);

        // Call the method under test
        ResponseEntity<List<OrderDetail>> response = orderController.getByOrder(orderId);

        // Verify the expected behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.hasBody());
    }

    @Test
    public void testGetByOrder_EmptyOrderDetailListReturned() {
        Long orderId = 1L;

        Order order = new Order();
        order.setOrdersId(1L);

        // Mock the behavior of orderRepository.findById(orderId)
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Mock the behavior of orderRepository.existsById(orderId)
        when(orderRepository.existsById(orderId)).thenReturn(true);

        // Mock the behavior of orderDetailRepository.findByOrder(...)
        when(orderDetailRepository.findByOrder(order)).thenReturn(new ArrayList<>());

        // Call the method under test
        ResponseEntity<List<OrderDetail>> response = orderController.getByOrder(orderId);

        // Verify the expected behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

}
