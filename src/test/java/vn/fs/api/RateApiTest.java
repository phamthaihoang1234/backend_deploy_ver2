package vn.fs.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.fs.entity.OrderDetail;
import vn.fs.entity.Product;
import vn.fs.entity.Rate;
import vn.fs.entity.User;
import vn.fs.repository.OrderDetailRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.repository.RateRepository;
import vn.fs.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateApiTest {

    @InjectMocks
    RateApi rateApi;

    @Mock
    RateRepository rateRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    OrderDetailRepository orderDetailRepository;

    @Mock
    ProductRepository productRepository;

    @Test
    void Given_ExistingUserProductAndOrderDetail_When_post_Then_ReturnSuccess() {
        // Given
        Rate rate = new Rate();
        User user = new User();
        Product product = new Product();
        OrderDetail orderDetail = new OrderDetail();

        rate.setUser(user);
        rate.setProduct(product);
        rate.setOrderDetail(orderDetail);

        when(userRepository.existsById(user.getUserId())).thenReturn(true);
        when(productRepository.existsById(product.getProductId())).thenReturn(true);
        when(orderDetailRepository.existsById(orderDetail.getOrderDetailId())).thenReturn(true);

        // When
        ResponseEntity<Rate> response = rateApi.post(rate);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        verify(rateRepository).save(rate);
    }

    @Test
    void Given_NonExistingUser_When_post_Then_ReturnNotFound() {
        // Given
        Rate rate = new Rate();
        User user = new User();
        rate.setUser(user);

        when(userRepository.existsById(user.getUserId())).thenReturn(false);

        // When
        ResponseEntity<Rate> response = rateApi.post(rate);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(rateRepository, never()).save(any());
    }

    @Test
    void Given_ExistingUserProductOrderDetail_When_post_Then_ReturnSuccess() {
        // Given
        Rate rate = new Rate();
        User user = new User();
        user.setUserId(1L);
        rate.setUser(user);

        Product product = new Product();
        product.setProductId(1L);
        rate.setProduct(product);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetailId(1L);
        rate.setOrderDetail(orderDetail);

        when(userRepository.existsById(user.getUserId())).thenReturn(true);
        when(productRepository.existsById(product.getProductId())).thenReturn(true);
        when(orderDetailRepository.existsById(orderDetail.getOrderDetailId())).thenReturn(true);

        Rate savedRate = new Rate();
        when(rateRepository.save(rate)).thenReturn(savedRate);

        // When
        ResponseEntity<Rate> response = rateApi.post(rate);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertSame(savedRate, response.getBody());
    }

    @Test
    void Given_AllExistingEntities_When_post_Then_ReturnServerError() {
        // Given
        Rate rate = new Rate();
        User user = new User();
        user.setUserId(1L);
        rate.setUser(user);

        Product product = new Product();
        product.setProductId(1L);
        rate.setProduct(product);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetailId(1L);
        rate.setOrderDetail(orderDetail);

        when(userRepository.existsById(user.getUserId())).thenReturn(true);
        when(productRepository.existsById(any())).thenReturn(false);
        //when(orderDetailRepository.existsById(orderDetail.getOrderDetailId())).thenReturn(true);

     //   when(rateRepository.save(rate)).thenThrow(new RuntimeException());

        // When
        ResponseEntity<Rate> response = rateApi.post(rate);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void Given_AllExistingEntities_When_post_Then_ReturnNotFoundBy_orderDetailRepository() {
        // Given
        Rate rate = new Rate();
        User user = new User();
        user.setUserId(1L);
        rate.setUser(user);

        Product product = new Product();
        product.setProductId(1L);
        rate.setProduct(product);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetailId(1L);
        rate.setOrderDetail(orderDetail);

        when(userRepository.existsById(user.getUserId())).thenReturn(true);
        when(productRepository.existsById(any())).thenReturn(true);
        when(orderDetailRepository.existsById(any())).thenReturn(false);

        //   when(rateRepository.save(rate)).thenThrow(new RuntimeException());

        // When
        ResponseEntity<Rate> response = rateApi.post(rate);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void Given_ExistingRate_When_put_Then_ReturnSuccess() {
        // Given
        Rate rate = new Rate();
        rate.setId(1L);

        when(rateRepository.existsById(rate.getId())).thenReturn(true);

        // When
        ResponseEntity<Rate> response = rateApi.put(rate);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        verify(rateRepository).save(rate);
    }

    @Test
    void Given_NonExistingRate_When_put_Then_ReturnNotFound() {
        // Given
        Rate rate = new Rate();
        rate.setId(1L);

        when(rateRepository.existsById(rate.getId())).thenReturn(false);

        // When
        ResponseEntity<Rate> response = rateApi.put(rate);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        verify(rateRepository, never()).save(any());
    }

    @Test
    void Given_ExistingRate_When_delete_Then_ReturnSuccess() {
        // Given
        Long rateId = 1L;

        when(rateRepository.existsById(rateId)).thenReturn(true);

        // When
        ResponseEntity<Void> response = rateApi.delete(rateId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        verify(rateRepository).deleteById(rateId);
    }

    @Test
    void Given_NonExistingRate_When_delete_Then_ReturnNotFound() {
        // Given
        Long rateId = 1L;

        when(rateRepository.existsById(rateId)).thenReturn(false);

        // When
        ResponseEntity<Void> response = rateApi.delete(rateId);

        // Then
        assertEquals(404, response.getStatusCodeValue());
        verify(rateRepository, never()).deleteById(any());
    }

    @Test
    public void testFindAll() {
        // Create mock rates
        Rate rate1 = new Rate();
        rate1.setId(1L);

        Rate rate2 = new Rate();
        rate2.setId(1L);
        List<Rate> mockRateList = new ArrayList<>();
        mockRateList.add(rate1);
        mockRateList.add(rate2);

        // Mock the behavior of rateRepository.findAllByOrderByIdDesc()
        when(rateRepository.findAllByOrderByIdDesc()).thenReturn(mockRateList);

        // Call the method under test
        ResponseEntity<List<Rate>> result = rateApi.findAll();

        // Verify the expected behavior
        verify(rateRepository, times(1)).findAllByOrderByIdDesc();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockRateList, result.getBody());
    }

    @Test
    public void testFindById_ExistingOrderDetail() {
        Long orderDetailId = 1L;

        OrderDetail mockOrderDetail = new OrderDetail();
        mockOrderDetail.setOrderDetailId(orderDetailId);
        // Create a mock rate
        Rate mockRate = new Rate();
        mockRate.setId(1L);
        mockRate.setOrderDetail(mockOrderDetail);
        // Mock the behavior of orderDetailRepository.existsById(orderDetailId)
        when(orderDetailRepository.existsById(orderDetailId)).thenReturn(true);


// Mock the behavior of orderDetailRepository.findById(orderDetailId)
        when(orderDetailRepository.findById(orderDetailId)).thenReturn(Optional.of(mockOrderDetail));

        // Mock the behavior of rateRepository.findByOrderDetail(...)
        when(rateRepository.findByOrderDetail(mockOrderDetail)).thenReturn(mockRate);




        // Call the method under test
        ResponseEntity<Rate> result = rateApi.findById(orderDetailId);

        // Verify the expected behavior
        verify(orderDetailRepository, times(1)).existsById(orderDetailId);
        verify(rateRepository, times(1)).findByOrderDetail(any());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockRate, result.getBody());
    }

    @Test
    public void testFindById_NonExistingOrderDetail() {
        Long orderDetailId = 1L;
        // Mock the behavior of orderDetailRepository.existsById(orderDetailId)
        when(orderDetailRepository.existsById(orderDetailId)).thenReturn(false);

        // Call the method under test
        ResponseEntity<Rate> result = rateApi.findById(orderDetailId);

        // Verify the expected behavior
        verify(orderDetailRepository, times(1)).existsById(orderDetailId);
        verify(rateRepository, never()).findByOrderDetail(any());
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals(null, result.getBody());
    }

    @Test
    public void testFindByProduct_ExistingProduct() {
        Long productId = 1L;
        // Create mock rates
        Rate rate1 = new Rate();
        rate1.setId(1L);

        Rate rate2 = new Rate();
        rate2.setId(1L);
        List<Rate> mockRateList = new ArrayList<>();
        mockRateList.add(rate1);
        mockRateList.add(rate2);

        // Mock the behavior of productRepository.existsById(productId)
        when(productRepository.existsById(productId)).thenReturn(true);
        // Mock the behavior of productRepository.findById(productId)
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product(/* Set properties for the product */)));
        // Mock the behavior of rateRepository.findByProductOrderByIdDesc(...)
        when(rateRepository.findByProductOrderByIdDesc(any())).thenReturn(mockRateList);

        // Call the method under test
        ResponseEntity<List<Rate>> result = rateApi.findByProduct(productId);

        // Verify the expected behavior
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, times(1)).findById(productId);
        verify(rateRepository, times(1)).findByProductOrderByIdDesc(any());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockRateList, result.getBody());
    }

    @Test
    public void testFindByProduct_NonExistingProduct() {
        Long productId = 1L;
        // Mock the behavior of productRepository.existsById(productId)
        when(productRepository.existsById(productId)).thenReturn(false);

        // Call the method under test
        ResponseEntity<List<Rate>> result = rateApi.findByProduct(productId);

        // Verify the expected behavior
        verify(productRepository, times(1)).existsById(productId);
        verify(productRepository, never()).findById(productId);
        verify(rateRepository, never()).findByProductOrderByIdDesc(any());
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals(null, result.getBody());
    }


}