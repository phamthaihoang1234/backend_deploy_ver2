
package vn.fs.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserApi {

	AuthenticationManager authenticationManager;

	UserRepository userRepository;

	CartRepository cartRepository;

	AppRoleRepository roleRepository;

	PasswordEncoder passwordEncoder;

	JwtUtils jwtUtils;

	SendMailService sendMailService;

	@Autowired
	private RestTemplate restTemplate;

	private static final String URL_UPLOAD = "https://api.cloudinary.com/v1_1/martfury/image/upload";

	@GetMapping
	public ResponseEntity<List<User>> getAll() {
		return ResponseEntity.ok(userRepository.findByStatusTrue());
	}

	@GetMapping("{id}")
	public ResponseEntity<User> getOne(@PathVariable("id") Long id) {
		if (!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(userRepository.findById(id).get());
	}

	@GetMapping("email/{email}")
	public ResponseEntity<User> getOneByEmail(@PathVariable("email") String email) {
		if (userRepository.existsByEmail(email)) {
			return ResponseEntity.ok(userRepository.findByEmail(email).get());
		}
		return ResponseEntity.notFound().build();
	}




	@PutMapping("{id}")
	public ResponseEntity<User> put(@PathVariable("id") Long id, @RequestBody User user) {
		if (!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		if (!id.equals(user.getUserId())) {
			return ResponseEntity.badRequest().build();
		}

		User temp = userRepository.findById(id).get();

		if (!user.getPassword().equals(temp.getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		Set<AppRole> roles = new HashSet<>();
		roles.add(new AppRole(1, null));

		user.setRoles(roles);
		return ResponseEntity.ok(userRepository.save(user));
	}

	@PutMapping("admin/{id}")
	public ResponseEntity<User> putAdmin(@PathVariable("id") Long id, @RequestBody User user) {
		if (!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		if (!id.equals(user.getUserId())) {
			return ResponseEntity.badRequest().build();
		}
		Set<AppRole> roles = new HashSet<>();
		roles.add(new AppRole(2, null));

		user.setRoles(roles);
		return ResponseEntity.ok(userRepository.save(user));
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
		if (!userRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		User u = userRepository.findById(id).get();
		u.setStatus(false);
		userRepository.save(u);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getName(),
				userDetails.getEmail(), userDetails.getPassword(), userDetails.getPhone(), userDetails.getAddress(),
				userDetails.getGender(), userDetails.getStatus(), userDetails.getImage(), userDetails.getRegisterDate(),
				roles));

	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Validated @RequestBody SignupRequest signupRequest) {

		if (userRepository.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
		}

		if (userRepository.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is alreadv in use!"));
		}

		System.out.println("vao sign up");

		// create new user account
		User user = new User(signupRequest.getName(), signupRequest.getEmail(),
				passwordEncoder.encode(signupRequest.getPassword()), signupRequest.getPhone(),
				signupRequest.getAddress(), signupRequest.getGender(), signupRequest.getStatus(),
				signupRequest.getImage(), signupRequest.getRegisterDate(),
				jwtUtils.doGenerateToken(signupRequest.getEmail()));
		Set<AppRole> roles = new HashSet<>();
		roles.add(new AppRole(1, null));

		user.setRoles(roles);
		userRepository.save(user);
		Cart c = new Cart(0L, 0.0, user.getAddress(), user.getPhone(), user);
		cartRepository.save(c);
		return ResponseEntity.ok(new MessageResponse("Đăng kí thành công"));

	}

	@GetMapping("/logout")
	public ResponseEntity<Void> logout() {
		return ResponseEntity.ok().build();
	}

	@PostMapping("send-mail-forgot-password-token")
	public ResponseEntity<String> sendToken(@RequestBody String email) {

		if (!userRepository.existsByEmail(email)) {
			return ResponseEntity.notFound().build();
		}
		User user = userRepository.findByEmail(email).get();
		String token = user.getToken();
		sendMaiToken(email, token, "Reset mật khẩu");
		return ResponseEntity.ok().build();

	}



	@PostMapping(value = "/upload-file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		String url = "https://api.cloudinary.com/v1_1/martfury/image/upload";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", file.getResource());
		body.add("upload_preset", "user-image");
		body.add("cloud_name", "martfury");
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		return ResponseEntity.ok(response.getBody());
	}


	public void sendMaiToken(String email, String token, String title) {
		String body = "\r\n" + "    <h2>Hãy nhấp vào link để thay đổi mật khẩu của bạn</h2>\r\n"
				+ "    <a href=\"http://localhost:8080/forgot-password/" + token + "\">Đổi mật khẩu</a>";
		sendMailService.queue(email, title, body);
	}

}
