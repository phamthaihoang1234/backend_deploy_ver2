package vn.fs.api;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.fs.dto.CategoryBestSeller;
import vn.fs.dto.Statistical;
import vn.fs.entity.Order;
import vn.fs.entity.Product;
import vn.fs.repository.OrderRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.repository.StatisticalRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatisticalApiTest {
    @InjectMocks
    StatisticalApi statisticalApi;



    @Mock
    StatisticalRepository statisticalRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderRepository orderRepository;

    @Test
    public void testGetStatisticalYear() {
        int year = 2023;
        // Create a mock response from the statisticalRepository
        Object[] data1 = {100.0, 1};
        Object[] data2 = {200.0, 2};
        List<Object[]> mockStatisticalData = new ArrayList<>();
        mockStatisticalData.add(data1);
        mockStatisticalData.add(data2);

        // Mock the behavior of statisticalRepository.getMonthOfYear(year)
        when(statisticalRepository.getMonthOfYear(year))
                .thenReturn(mockStatisticalData);

        // Call the method under test
        ResponseEntity<List<Statistical>> result = statisticalApi.getStatisticalYear(year);

        // Verify the expected behavior
        verify(statisticalRepository, times(1)).getMonthOfYear(year);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        List<Statistical> resultList = result.getBody();

        // Assertions for the list of Statistical objects returned in the response
        assertEquals(12, resultList.size());
        assertEquals(100.0, resultList.get(0).getAmount());
        assertEquals(1, resultList.get(0).getMonth());
    }

    @Test
    public void testGetYears() {
        // Create a mock response from the statisticalRepository
        List<Integer> mockYears = Arrays.asList(2020, 2021, 2022);
        when(statisticalRepository.getYears()).thenReturn(mockYears);

        // Call the method under test
        ResponseEntity<List<Integer>> result = statisticalApi.getYears();

        // Verify the expected behavior
        verify(statisticalRepository, times(1)).getYears();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        List<Integer> resultList = result.getBody();

        // Assertions for the list of years returned in the response
        assertEquals(mockYears.size(), resultList.size());
        assertEquals(mockYears, resultList);
    }

    @Test
    public void testGetRevenueByYear() {
        int year = 2023;
        double mockRevenue = 15000.0;

        // Mock the behavior of statisticalRepository.getRevenueByYear(year)
        when(statisticalRepository.getRevenueByYear(year))
                .thenReturn(mockRevenue);

        // Call the method under test
        ResponseEntity<Double> result = statisticalApi.getRevenueByYear(year);

        // Verify the expected behavior
        verify(statisticalRepository, times(1)).getRevenueByYear(year);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockRevenue, result.getBody());
    }

    @Test
    public void testGetAllOrderSuccess() {
        // Create mock orders
        Order order1 = new Order();
        order1.setOrdersId(1L);
        order1.setStatus(1);
        Order order2 = new Order();
        order2.setOrdersId(1L);
        order2.setStatus(1);
        List<Order> mockOrderList = Arrays.asList(order1, order2);

        // Mock the behavior of orderRepository.findByStatus(2)
        when(orderRepository.findByStatus(2))
                .thenReturn(mockOrderList);

        // Call the method under test
        ResponseEntity<List<Order>> result = statisticalApi.getAllOrderSuccess();

        // Verify the expected behavior
        verify(orderRepository, times(1)).findByStatus(2);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockOrderList, result.getBody());
    }

    @Test
    public void testGetCategoryBestSeller() {
        // Create mock data from the statisticalRepository
        Object[] data1 = {10, "Category 1", 5000.0};
        Object[] data2 = {5, "Category 2", 3000.0};
        List<Object[]> mockCategoryData = Arrays.asList(data1, data2);

        // Mock the behavior of statisticalRepository.getCategoryBestSeller()
        when(statisticalRepository.getCategoryBestSeller())
                .thenReturn(mockCategoryData);

        // Call the method under test
        ResponseEntity<List<CategoryBestSeller>> result = statisticalApi.getCategoryBestSeller();

        // Verify the expected behavior
        verify(statisticalRepository, times(1)).getCategoryBestSeller();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        List<CategoryBestSeller> resultList = result.getBody();

        // Assertions for the list of CategoryBestSeller objects returned in the response
        assertEquals(mockCategoryData.size(), resultList.size());
        // Add more assertions for the category data if needed
    }

    @Test
    public void testGetInventory() {
        // Create mock products
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setStatus(true);
        Product product2 = new Product();
        product2.setProductId(1L);
        product2.setStatus(true);
        List<Product> mockProductList = Arrays.asList(product1, product2);

        // Mock the behavior of productRepository.findByStatusTrueOrderByQuantityDesc()
        when(productRepository.findByStatusTrueOrderByQuantityDesc())
                .thenReturn(mockProductList);

        // Call the method under test
        ResponseEntity<List<Product>> result = statisticalApi.getInventory();

        // Verify the expected behavior
        verify(productRepository, times(1)).findByStatusTrueOrderByQuantityDesc();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockProductList, result.getBody());
    }

}
