
package vn.fs.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.fs.config.JwtUtils;
import vn.fs.dto.OrderRequest;
import vn.fs.entity.*;
import vn.fs.repository.*;
import vn.fs.utils.SendMailUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderApi {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@Autowired
	UserRepository userRepository;

	PasswordEncoder passwordEncoder;

	JwtUtils jwtUtils;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	CartDetailRepository cartDetailRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	SendMailUtil senMail;

	@GetMapping
	public ResponseEntity<List<Order>> findAll() {
		return ResponseEntity.ok(orderRepository.findAllByOrderByOrdersIdDesc());
	}

	@GetMapping("{id}")
	public ResponseEntity<Order> getById(@PathVariable("id") Long id) {

		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findById(id).get());
	}

	@GetMapping("/user/{email}")
	public ResponseEntity<List<Order>> getByUser(@PathVariable("email") String email) {
		System.out.println("vao get order by email");
		if (!userRepository.existsByEmail(email)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity
				.ok(orderRepository.findByUserOrderByOrdersIdDesc(userRepository.findByEmail(email).get()));
	}

	@PostMapping("/{email}")
	public ResponseEntity<Order> checkout(@PathVariable("email") String email, @RequestBody Cart cart) {

		if (!userRepository.existsByEmail(email)) {
			return ResponseEntity.notFound().build();
		}
		if (!cartRepository.existsById(cart.getCartId())) {
			return ResponseEntity.notFound().build();
		}
		List<CartDetail> items = cartDetailRepository.findByCart(cart);
		Double amount = 0.0;
		for (CartDetail i : items) {
			amount += i.getPrice();
		}
		Order order = orderRepository.save(new Order(0L, new Date(), amount, cart.getAddress(), cart.getPhone(), 0,
				userRepository.findByEmail(email).get()));
		for (CartDetail i : items) {
			OrderDetail orderDetail = new OrderDetail(0L, i.getQuantity(), i.getPrice(), i.getProduct(), order);
//			Product p = productRepository.getById(i.getProduct().getProductId());
//			p.setQuantity(p.getQuantity()-i.getQuantity());
//			productRepository.save(p);
			orderDetailRepository.save(orderDetail);
		}
//		cartDetailRepository.deleteByCart(cart);
		for (CartDetail i : items) {
			cartDetailRepository.delete(i);
		}
		senMail.sendMailOrder(order);
		return ResponseEntity.ok(order);
	}

	@PostMapping("/guess/{email}")
	public ResponseEntity<Order> checkout(@PathVariable("email") String email, @RequestBody OrderRequest orderRequest) {

		Cart cart = orderRequest.getCart();
		List<CartDetail> CartDetails	= orderRequest.getOrderDetails();
		// create accout user for guess
		System.out.println("Email thu dc la:" + email);
		System.out.println("ten nguoi dung thu dc la:" + cart.getUser().getName());
		System.out.println("so dien thoai thu dc la:" + cart.getPhone());
		System.out.println("dia chi  thu dc la:" + cart.getAddress());
		for (CartDetail i : CartDetails) {
			System.out.println("item thu dc thu dc la:" + i.getProduct().getName());
		}


		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String currentDate = LocalDate.now().format(formatter);
		User user = new User(cart.getUser().getName(), email,
				passwordEncoder.encode("123@qtu"), cart.getPhone(),
				cart.getAddress(), true, true,
				null, LocalDate.parse(currentDate, formatter),
				jwtUtils.doGenerateToken(email));
		Set<AppRole> roles = new HashSet<>();
		roles.add(new AppRole(1, null));

		user.setRoles(roles);
		userRepository.save(user);

		Cart c = new Cart(0L, 0.0, user.getAddress(), user.getPhone(), user);
		cartRepository.save(c);

		// create account user for guess
		List<CartDetail> items = CartDetails;
		Double amount = 0.0;
		for (CartDetail i : items) {
			amount += i.getPrice();
		}
		Order order = orderRepository.save(new Order(0L, new Date(), amount, cart.getAddress(), cart.getPhone(), 0,
				user));
		for (CartDetail i : items) {
			OrderDetail orderDetail = new OrderDetail(0L, i.getQuantity(), i.getPrice(), i.getProduct(), order);
//			Product p = productRepository.getById(i.getProduct().getProductId());
//			p.setQuantity(p.getQuantity()-i.getQuantity());
//			productRepository.save(p);
			orderDetailRepository.save(orderDetail);
		}


		senMail.sendMailOrder(order);
		return ResponseEntity.ok(order);
	}



	@GetMapping("cancel/{orderId}")
	public ResponseEntity<Void> cancel(@PathVariable("orderId") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Order order = orderRepository.findById(id).get();

		order.setStatus(3);
		orderRepository.save(order);
		senMail.sendMailOrderCancel(order);
		return ResponseEntity.ok().build();
	}

	@GetMapping("deliver/{orderId}")
	public ResponseEntity<Void> deliver(@PathVariable("orderId") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Order order = orderRepository.findById(id).get();

		List<OrderDetail> listOrderDetail = orderDetailRepository.findByOrder(order);

		for (OrderDetail i : listOrderDetail) {

			Product p = productRepository.getById(i.getProduct().getProductId());
			p.setQuantity(p.getQuantity()-i.getQuantity());
			productRepository.save(p);

		}


		order.setStatus(1);
		orderRepository.save(order);
		senMail.sendMailOrderDeliver(order);
		return ResponseEntity.ok().build();
	}

	@GetMapping("success/{orderId}")
	public ResponseEntity<Void> success(@PathVariable("orderId") Long id) {
		if (!orderRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Order order = orderRepository.findById(id).get();
		order.setStatus(2);
		orderRepository.save(order);
		senMail.sendMailOrderSuccess(order);
		updateProduct(order);
		return ResponseEntity.ok().build();
	}

	public void updateProduct(Order order) {
		List<OrderDetail> listOrderDetail = orderDetailRepository.findByOrder(order);
		for (OrderDetail orderDetail : listOrderDetail) {
			Product product = productRepository.findById(orderDetail.getProduct().getProductId()).get();
			if (product != null) {
				product.setQuantity(product.getQuantity() - orderDetail.getQuantity());
				product.setSold(product.getSold() + orderDetail.getQuantity());
				productRepository.save(product);
			}
		}
	}

}
