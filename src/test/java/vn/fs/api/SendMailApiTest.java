package vn.fs.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import vn.fs.repository.UserRepository;
import vn.fs.service.SendMailService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendMailApiTest {

    @InjectMocks
    SendMailApi sendMailApi;

    @Mock
    SendMailService sendMail;

    @Mock
    UserRepository Urepo;

    @Test
    void Given_ValidEmail_When_sendOpt_Then_ReturnRandomOtp() {
        // Given
        String email = "test@example.com";
        int randomOtp = 123456;
        when(Urepo.existsByEmail(email)).thenReturn(false);

        // When
        ResponseEntity<Integer> response = sendMailApi.sendOpt(email);

        // Then
        verify(Urepo).existsByEmail(email);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void Given_ExistingEmail_When_sendOpt_Then_ReturnNotFound() {
        // Given
        String email = "existing@example.com";
        when(Urepo.existsByEmail(email)).thenReturn(true);

        // When
        ResponseEntity<Integer> response = sendMailApi.sendOpt(email);

        // Then
        verify(Urepo).existsByEmail(email);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void Given_EmailOtpTitle_When_sendMailOtp_Then_SendMailWithOtp() {
        // Given
        String email = "test@example.com";
        int otp = 123456;
        String title = "Xác nhận tài khoản";
        String expectedBody = "<div>\r\n" +
                "        <h3>Mã OTP của bạn là: <span style=\"color:red; font-weight: bold;\">"
                + otp + "</span></h3>\r\n" + "    </div>";

        // When
        sendMailApi.sendMailOtp(email, otp, title);

        // Then
        verify(sendMail).queue(email, title, expectedBody);
    }
}