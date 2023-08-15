package vn.fs.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import vn.fs.config.JwtUtils;
import vn.fs.dto.JwtResponse;
import vn.fs.dto.LoginRequest;
import vn.fs.dto.MessageResponse;
import vn.fs.dto.SignupRequest;
import vn.fs.entity.AppRole;
import vn.fs.entity.Cart;
import vn.fs.entity.User;
import vn.fs.repository.AppRoleRepository;
import vn.fs.repository.CartRepository;
import vn.fs.repository.UserRepository;
import vn.fs.service.SendMailService;
import vn.fs.service.implement.UserDetailsImpl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserApiTest {

    @InjectMocks
    UserApi userApi;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserRepository userRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    AppRoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    SendMailService sendMailService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    UserDetailsService userDetailsService;

    @Test
    void GivenUsersExist_getAll_ReturnUsersList() {
        // Input value
        User user1 = new User();
        user1.setUserId(1L);
        user1.setStatus(true);

        User user2 = new User();
        user2.setUserId(2L);
        user2.setStatus(true);

        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(user1);
        mockUsers.add(user2);

        when(userRepository.findByStatusTrue()).thenReturn(mockUsers); // Mock danh sách user

        // Thực hiện phương thức kiểm thử
        ResponseEntity<List<User>> result = userApi.getAll();

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(mockUsers.size(), result.getBody().size()); // Kiểm tra danh sách user trả về có số lượng phù hợp
        assertSame(mockUsers, result.getBody()); // Kiểm tra danh sách user trả về là danh sách user giả lập
    }

    @Test
    void GivenNoUser_getAll_ReturnEmptyList() {
        // Input value
        List<User> mockUsers = new ArrayList<>();

        when(userRepository.findByStatusTrue()).thenReturn(mockUsers); // Mock danh sách user rỗng

        // Thực hiện phương thức kiểm thử
        ResponseEntity<List<User>> result = userApi.getAll();

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertTrue(result.getBody().isEmpty()); // Kiểm tra danh sách user trả về là rỗng
    }


    @Test
    void GivenNonExistentUserId_getOne_ReturnNotFound() {
        // Input value
        Long userId = -1L;
        when(userRepository.existsById(userId)).thenReturn(false); // Mock không tìm thấy user

        // Thực hiện phương thức kiểm thử
        ResponseEntity<User> result = userApi.getOne(userId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null khi không tìm thấy user
    }

    @Test
    void GivenExistingUserId_getOne_ReturnUser() {
        // Input value
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setUserId(userId);
        when(userRepository.existsById(userId)).thenReturn(true); // Mock tìm thấy user
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser)); // Mock phương thức findById

        // Thực hiện phương thức kiểm thử
        ResponseEntity<User> result = userApi.getOne(userId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertSame(mockUser, result.getBody()); // Kiểm tra user trả về là user giả lập
    }

    @Test
    void GivenNonExistentEmail_getOneByEmail_ReturnNotFound() {
        // Input value
        String email = "nonexistent@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false); // Mock không tìm thấy user theo email

        // Thực hiện phương thức kiểm thử
        ResponseEntity<User> result = userApi.getOneByEmail(email);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null khi không tìm thấy user
    }

    @Test
    void GivenExistingEmail_getOneByEmail_ReturnUser() {
        // Input value
        String email = "existing@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);
        when(userRepository.existsByEmail(email)).thenReturn(true); // Mock tìm thấy user theo email
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser)); // Mock phương thức findByEmail

        // Thực hiện phương thức kiểm thử
        ResponseEntity<User> result = userApi.getOneByEmail(email);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertSame(mockUser, result.getBody()); // Kiểm tra user trả về là user giả lập
    }

    @Test
    void GivenNonExistentUserId_put_ReturnNotFound() {
        // Input value
        Long userId = -1L;
        User user = new User();
        user.setUserId(userId);

        when(userRepository.existsById(userId)).thenReturn(false); // Mock không tìm thấy user theo id

        // Thực hiện phương thức kiểm thử
        ResponseEntity<User> result = userApi.put(userId, user);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null khi không tìm thấy user
    }

    @Test
    void GivenMismatchedUserId_put_ReturnBadRequest() {
        // Input value
        Long userId = 1L;
        User user = new User();
        user.setUserId(2L);

        when(userRepository.existsById(userId)).thenReturn(true); // Mock tìm thấy user theo id

        // Thực hiện phương thức kiểm thử
        ResponseEntity<User> result = userApi.put(userId, user);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode()); // Kiểm tra status code là BAD_REQUEST
        assertNull(result.getBody()); // Kiểm tra body là null khi id không khớp
    }

    @Test
    void GivenMatchingUserIdAndDifferentPassword_put_EncryptPasswordAndReturnUser() {
        // Input value
        Long userId = 1L;
        String originalPassword = "password123";
        String encodedPassword = "encodedPassword123";

        User user = new User();
        user.setUserId(userId);
        user.setPassword(originalPassword);

        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setPassword(encodedPassword);

        when(userRepository.existsById(userId)).thenReturn(true); // Mock tìm thấy user theo id
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser)); // Mock phương thức findById
        when(passwordEncoder.encode(originalPassword)).thenReturn(encodedPassword); // Mock phương thức encode
        when(userRepository.save(user)).thenReturn(user); // Mock phương thức save

        // Thực hiện phương thức kiểm thử
        ResponseEntity<User> result = userApi.put(userId, user);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(encodedPassword, result.getBody().getPassword()); // Kiểm tra password đã được mã hóa
    }

    @Test
    void GivenMatchingUserIdAndMatchingPassword_put_ReturnUser() {
        // Input value
        Long userId = 1L;
        String existingEncodedPassword = "encodedPassword123";

        User user = new User();
        user.setUserId(userId);
        user.setPassword(existingEncodedPassword);

        User existingUser = new User();
        existingUser.setUserId(userId);
        existingUser.setPassword(existingEncodedPassword);

        Set<AppRole> roles = new HashSet<>();
        roles.add(new AppRole(1, null));

        user.setRoles(roles);

        when(userRepository.existsById(userId)).thenReturn(true); // Mock tìm thấy user theo id
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser)); // Mock phương thức findById
        when(userRepository.save(user)).thenReturn(user); // Mock phương thức save

        // Thực hiện phương thức kiểm thử
        ResponseEntity<User> result = userApi.put(userId, user);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(existingEncodedPassword, result.getBody().getPassword()); // Kiểm tra password không thay đổi
        assertEquals(roles, result.getBody().getRoles()); // Kiểm tra roles giữ nguyên
    }

    @Test
    void GivenNonExistentUserId_putAdmin_ReturnNotFound() {
        // Input value
        Long userId = -1L;
        User user = new User();
        user.setUserId(userId);

        when(userRepository.existsById(userId)).thenReturn(false); // Mock không tìm thấy user theo id

        // Thực hiện phương thức kiểm thử
        ResponseEntity<User> result = userApi.putAdmin(userId, user);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null khi không tìm thấy user
    }

    @Test
    void GivenMismatchedUserId_putAdmin_ReturnBadRequest() {
        // Input value
        Long userId = 1L;
        User user = new User();
        user.setUserId(2L);

        when(userRepository.existsById(userId)).thenReturn(true); // Mock tìm thấy user theo id

        // Thực hiện phương thức kiểm thử
        ResponseEntity<User> result = userApi.putAdmin(userId, user);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode()); // Kiểm tra status code là BAD_REQUEST
        assertNull(result.getBody()); // Kiểm tra body là null khi id không khớp
    }

    @Test
    void GivenMatchingUserId_putAdmin_ReturnUserWithAdminRole() {
        // Input value
        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);

        Set<AppRole> roles = new HashSet<>();
        roles.add(new AppRole(2, null));

        user.setRoles(roles);

        when(userRepository.existsById(userId)).thenReturn(true); // Mock tìm thấy user theo id
        when(userRepository.save(user)).thenReturn(user); // Mock phương thức save

        // Thực hiện phương thức kiểm thử
        ResponseEntity<User> result = userApi.putAdmin(userId, user);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body có giá trị
        assertEquals(roles, result.getBody().getRoles()); // Kiểm tra roles là admin role
    }

    @Test
    void GivenNonExistentUserId_delete_ReturnNotFound() {
        // Input value
        Long userId = -1L;
        when(userRepository.existsById(userId)).thenReturn(false); // Mock không tìm thấy user theo id

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Void> result = userApi.delete(userId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        verify(userRepository, never()).save(any()); // Kiểm tra phương thức save không được gọi
    }

    @Test
    void GivenExistingUserId_delete_SetStatusToFalseAndReturnOk() {
        // Input value
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setUserId(userId);

        when(userRepository.existsById(userId)).thenReturn(true); // Mock tìm thấy user theo id
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser)); // Mock phương thức findById
        when(userRepository.save(existingUser)).thenReturn(existingUser); // Mock phương thức save

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Void> result = userApi.delete(userId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        verify(userRepository, times(1)).save(existingUser); // Kiểm tra phương thức save được gọi 1 lần
        assertFalse(existingUser.getStatus()); // Kiểm tra status của user đã được set thành false
    }


    @Test
    void GivenValidLoginRequest_authenticateUser_ReturnJwtResponse() {
        // Input value
        String email = "test@example.com";
        String password = "password123";
        String jwtToken = "generatedJwtToken";
        Long userId = 1L;

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        UserDetailsImpl userDetails = new UserDetailsImpl(userId, "John Doe", email, password, "123456789",
                "Test Address", true, true, "test-image.jpg", LocalDate.now(), authorities);

        // Giả lập quá trình xác thực thành công và trả về đối tượng UserDetailsImpl
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwtToken);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<?> result = userApi.authenticateUser(loginRequest);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertTrue(result.getBody() instanceof JwtResponse); // Kiểm tra body là instance của JwtResponse

        JwtResponse jwtResponse = (JwtResponse) result.getBody();
        assertEquals(jwtToken, jwtResponse.getToken()); // Kiểm tra jwt token
        assertEquals(userId, jwtResponse.getId()); // Kiểm tra user id
        assertEquals("John Doe", jwtResponse.getName()); // Kiểm tra user name
        assertEquals(email, jwtResponse.getEmail()); // Kiểm tra email
        assertEquals("123456789", jwtResponse.getPhone()); // Kiểm tra phone
        assertEquals("Test Address", jwtResponse.getAddress()); // Kiểm tra address
        assertEquals(true, jwtResponse.getGender()); // Kiểm tra gender
        assertEquals(true, jwtResponse.getStatus()); // Kiểm tra status
        assertEquals("test-image.jpg", jwtResponse.getImage()); // Kiểm tra image
        assertNotNull(jwtResponse.getRegisterDate()); // Kiểm tra register date không null

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertEquals(roles, jwtResponse.getRoles()); // Kiểm tra danh sách roles
    }

    @Test
    void GivenValidSignupRequest_registerUser_ReturnSuccessMessageResponse() {
        // Input value
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("John Doe");
        signupRequest.setEmail("john.doe@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setPhone("123456789");
        signupRequest.setAddress("Test Address");
        signupRequest.setGender(true);
        signupRequest.setStatus(true);
        signupRequest.setImage("test-image.jpg");
        signupRequest.setRegisterDate(LocalDate.now());

        // Giả lập userRepository.existsByEmail trả về false để đảm bảo email chưa được sử dụng
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);

        // Giả lập mã hóa mật khẩu
        String hashedPassword = "hashedPassword123";
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn(hashedPassword);

        // Giả lập jwtUtils.doGenerateToken trả về một chuỗi token
        String token = "generatedJwtToken";
        when(jwtUtils.doGenerateToken(signupRequest.getEmail())).thenReturn(token);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<?> result = userApi.registerUser(signupRequest);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertTrue(result.getBody() instanceof MessageResponse); // Kiểm tra body là instance của MessageResponse

        MessageResponse messageResponse = (MessageResponse) result.getBody();
        assertEquals("Đăng kí thành công", messageResponse.getMessage()); // Kiểm tra message trong response

        // Kiểm tra lưu thông tin user và cart
        verify(userRepository, times(1)).save(any(User.class));
        verify(cartRepository, times(1)).save(any(Cart.class));

        // Kiểm tra đúng số roles được gán cho user
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        Set<AppRole> roles = savedUser.getRoles();
        assertNotNull(roles); // Kiểm tra roles không null
        assertEquals(1, roles.size()); // Kiểm tra chỉ có một role được gán cho user
        AppRole role = roles.iterator().next();
        assertEquals(1, role.getId()); // Kiểm tra id của role

        // Kiểm tra đúng thông tin của user
        assertEquals(signupRequest.getName(), savedUser.getName());
        assertEquals(signupRequest.getEmail(), savedUser.getEmail());
        assertEquals(hashedPassword, savedUser.getPassword());
        assertEquals(signupRequest.getPhone(), savedUser.getPhone());
        assertEquals(signupRequest.getAddress(), savedUser.getAddress());
        assertEquals(signupRequest.getGender(), savedUser.getGender());
        assertEquals(signupRequest.getStatus(), savedUser.getStatus());
        assertEquals(signupRequest.getImage(), savedUser.getImage());
        assertEquals(signupRequest.getRegisterDate(), savedUser.getRegisterDate());
    }

    @Test
    void GivenExistingEmail_registerUser_ReturnErrorMessageResponse() {
        // Input value
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("john.doe@example.com");

        // Giả lập userRepository.existsByEmail trả về true để đảm bảo email đã được sử dụng
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<?> result = userApi.registerUser(signupRequest);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode()); // Kiểm tra status code là BAD_REQUEST
        assertTrue(result.getBody() instanceof MessageResponse); // Kiểm tra body là instance của MessageResponse

        MessageResponse messageResponse = (MessageResponse) result.getBody();
        assertEquals("Error: Email is already taken!", messageResponse.getMessage()); // Kiểm tra message trong response

        // Kiểm tra không lưu thông tin user và cart nếu email đã tồn tại
        verify(userRepository, never()).save(any(User.class));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void logout_ReturnOkResponse() {
        // Tạo một đối tượng CartDetailApi để gọi phương thức logout
        CartDetailApi cartDetailApi = new CartDetailApi();

        // Thực hiện phương thức kiểm thử
        ResponseEntity<Void> result = userApi.logout();

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNull(result.getBody()); // Kiểm tra body là null vì phương thức không trả về dữ liệu
    }

    @Test
    void GivenExistingEmail_sendToken_ReturnOkResponse() {
        // Input value
        String email = "john.doe@example.com";

        // Tạo mock của CartDetailApi
        CartDetailApi cartDetailApi = mock(CartDetailApi.class);

        // Giả lập userRepository.existsByEmail trả về true để đảm bảo email đã tồn tại
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // Giả lập userRepository.findByEmail trả về một đối tượng User đã được set token
        User user = new User();
        user.setEmail(email);
        user.setToken("testToken");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Thực hiện phương thức kiểm thử
        ResponseEntity<String> result = userApi.sendToken(email);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNull(result.getBody()); // Kiểm tra body là null vì phương thức không trả về dữ liệu

    }

    @Test
    void GivenNonExistingEmail_sendToken_ReturnNotFoundResponse() {
        // Input value
        String email = "nonexistent@example.com";

        // Tạo mock của CartDetailApi
        CartDetailApi cartDetailApi = mock(CartDetailApi.class);

        // Giả lập userRepository.existsByEmail trả về false để đảm bảo email không tồn tại
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<String> result = userApi.sendToken(email);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode()); // Kiểm tra status code là NOT_FOUND
        assertNull(result.getBody()); // Kiểm tra body là null vì phương thức không trả về dữ liệu
    }

    @Test
    void GivenValidFile_uploadFile_ReturnOkResponse() throws Exception {
        // Input value
        MockMultipartFile file = new MockMultipartFile("file", "test-image.jpg", "image/jpeg", "test data".getBytes());

        // Tạo ResponseEntity mock
        ResponseEntity<String> responseEntity = new ResponseEntity<>("https://example.com/image123.jpg", HttpStatus.OK);

        // Giả lập gọi exchange trả về responseEntity
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // Tạo CartDetailApi với restTemplate mock
        //   CartDetailApi cartDetailApi = new CartDetailApi(restTemplate);

        // Thực hiện phương thức kiểm thử
        ResponseEntity<String> result = userApi.uploadFile(file);

        // Kiểm tra kết quả
        assertNotNull(result); // Kiểm tra kết quả không null
        assertEquals(HttpStatus.OK, result.getStatusCode()); // Kiểm tra status code là OK
        assertNotNull(result.getBody()); // Kiểm tra body không null
        assertEquals("https://example.com/image123.jpg", result.getBody()); // Kiểm tra body chứa URL hợp lệ
    }
}
