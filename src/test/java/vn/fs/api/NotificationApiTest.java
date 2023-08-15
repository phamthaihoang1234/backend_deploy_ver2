package vn.fs.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.fs.entity.Notification;
import vn.fs.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationApiTest {

    @InjectMocks
    NotificationApi notificationApi;

    @Mock
    NotificationRepository notificationRepository;



    @Test
    public void testGetAll_NotificationsExist() {
        // Create a list of notifications to return from the mock repository
        List<Notification> expectedNotifications = new ArrayList<>();
        Notification notification1 = new Notification();
        notification1.setId(1L);
        Notification notification2 = new Notification();
        notification2.setId(2L);
        expectedNotifications.add(notification1);
        expectedNotifications.add(notification2);

        // Mock the behavior of notificationRepository.findByOrderByIdDesc(...)
        when(notificationRepository.findByOrderByIdDesc()).thenReturn(expectedNotifications);

        // Call the method under test
        ResponseEntity<List<Notification>> response = notificationApi.getAll();

        // Verify the expected behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedNotifications, response.getBody());
    }

    @Test
    public void testGetAll_NoNotifications() {
        // Mock the behavior of notificationRepository.findByOrderByIdDesc(...)
        when(notificationRepository.findByOrderByIdDesc()).thenReturn(new ArrayList<>());

        // Call the method under test
        ResponseEntity<List<Notification>> response = notificationApi.getAll();

        // Verify the expected behavior for an empty list of notifications
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void Given_ExistingNotification_When_post_Then_ReturnBadRequest() {
        // Given
        Notification notification = new Notification();
        notification.setId(1L);

        when(notificationRepository.existsById(notification.getId())).thenReturn(true);

        // When
        ResponseEntity<Notification> response = notificationApi.post(notification);

        // Then
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void Given_NonExistingNotification_When_post_Then_ReturnSuccess() {
        // Given
        Notification notification = new Notification();
        notification.setId(1L);

        when(notificationRepository.existsById(notification.getId())).thenReturn(false);

        Notification savedNotification = new Notification();
        when(notificationRepository.save(notification)).thenReturn(savedNotification);

        // When
        ResponseEntity<Notification> response = notificationApi.post(notification);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertSame(savedNotification, response.getBody());
    }

    @Test
    public void testPut_NotificationExists() {
//        Long notificationId = 1L;
//
//        // Create a notification to return from the mock repository
//        Notification notification = new Notification();
//        notification.setId(notificationId);
//        notification.setStatus(false);
//
//        // Mock the behavior of notificationRepository.existsById(...)
//        when(notificationRepository.existsById(notificationId)).thenReturn(true);
//
//        // Mock the behavior of notificationRepository.getById(...)
//        when(notificationRepository.save(notification)).thenReturn(notification);
//
//        // Call the method under test
//        ResponseEntity<Notification> response = notificationApi.put(notificationId);
//
//        // Kiểm tra kết quả
//        assertNotNull(response); // Kiểm tra kết quả không null
//        assertEquals(HttpStatus.OK, response.getStatusCode()); // Kiểm tra status code là OK
//        assertNotNull(response.getBody()); // Kiểm tra body không null
//        assertEquals(notification, response.getBody()); // Kiểm tra body là sản phẩm đã được cập nhật
        Long notificationId = 1L;

        // Create a notification to return from the mock repository
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setStatus(false);

        // Mock the behavior of notificationRepository.existsById(...)
        when(notificationRepository.existsById(notificationId)).thenReturn(true);

        // Mock the behavior of notificationRepository.getById(...)
        when(notificationRepository.getById(notificationId)).thenReturn(notification);

        // ArgumentCaptor to capture the notification passed to the save() method
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        // Mock the behavior of notificationRepository.save(...)
        when(notificationRepository.save(notificationCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method under test
        ResponseEntity<Notification> response = notificationApi.put(notificationId);

        // Kiểm tra kết quả
        assertNotNull(response); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(response.getBody()); // Kiểm tra body không null

        // Kiểm tra trạng thái đã được cập nhật thành true
        assertTrue(response.getBody().getStatus());
        assertEquals(notification, response.getBody()); // Kiểm tra body là sản phẩm đã được cập nhật
    }

    @Test
    public void testPut_NotificationNotFound() {
        Long notificationId = 1L;

        // Mock the behavior of notificationRepository.existsById(...)
        when(notificationRepository.existsById(notificationId)).thenReturn(false);

        // Call the method under test
        ResponseEntity<Notification> response = notificationApi.put(notificationId);

        // Verify the expected behavior for a not found notification
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.hasBody()); // The response body should be empty
    }

    @Test
    public void testReadAll() {
        // Call the method under test
        ResponseEntity<Void> response = notificationApi.readAll();

        // Verify the expected behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(notificationRepository).readAll(); // Verify that the readAll() method is called on the repository
    }

}