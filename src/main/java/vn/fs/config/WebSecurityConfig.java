/*
 * (C) Copyright 2022. All Rights Reserved.
 *
 * @author DongTHD
 * @date Mar 10, 2022
*/
package vn.fs.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import vn.fs.service.implement.UserDetailsServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	UserDetailsServiceImpl userDetailsService;
	AuthEntryPointJwt unauthorizedHandler;
	JwtUtils jwtUtils;

	public AuthTokenFilter authenticationJwTokenFilter() {
		Map<String, List<HttpMethod>> map = new HashMap<>();
		map.put("/api/public/*", null);
		map.put("/api/auth/*", null);
		RequestMatcher skipMatcher = new SkipPathRequestMatcher(map);
		return new AuthTokenFilter(jwtUtils, skipMatcher, userDetailsService);
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable().authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.antMatchers().access("hasRole('ROLE_USER')")
				.antMatchers( ).access("hasRole('ROLE_ADMIN')")
				.antMatchers("/api/products",
						"/api/products/bestseller",
						"/api/products/latest",
						"/api/products/rated",
						"/api/products/suggest/**",
						"/api/products/category/**",
						"/api/products/{id}",
						"/api/categories", "api/categories/{id}",
						"/api/rates/**",
						"/api/send-mail/**",
						"/api/favorites/email/**",
						"/api/auth/email/**",
						"/api/auth/signin/**",
						"/api/auth/signup/**",
						"/api/auth/upload-file",
						"/api/auth/{id}",
						"/api/auth/send-mail-forgot-password-token",
						"/forgot-password/**",
						"/api/notification/**",
						"/api/provinces/**",
						"/api/districts/**",
						"/api/wards/**",
						"/api/district/**",
						"/api/provinces/**",
						"/api/ward/**",
						"/api/orders/guess/**",
						"/api/statistical/**",
						"/notification",
						"/api/orderDetail/order/**"




				)
				.permitAll().anyRequest().authenticated()
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(unauthorizedHandler)
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

				http.addFilterBefore(authenticationJwTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}
}
