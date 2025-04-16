package com.lucas.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	private static final String SECRET = "YourSecret"; // Replace with your secret key

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		log.info("JwtRequestFilter.doFilterInternal() called" + request);
		final String requestTokenHeader = request.getHeader("Authorization");

		String username = null;
		String jwtToken = null;

		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				Claims claims = Jwts.parser()
						.setSigningKey(SECRET.getBytes())
						.parseClaimsJws(jwtToken)
						.getBody();

				username = claims.getSubject();
			} catch (Exception e) {
				log.info("JwtRequestFilter.doFilterInternal() exception: " + e.getMessage());
			}
		}

		// Validate the token
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// If token is valid configure Spring Security to manually set authentication
			String redisToken = stringRedisTemplate.opsForValue().get("token:" + username);
			if (jwtToken != null && jwtToken.equals(redisToken)) {
				// Token is valid. You can add further actions, such as setting the authentication in the context.
			} else {
				// Token validation failed
				// You can throw an exception or set an authentication error
			}
		}

		chain.doFilter(request, response);
	}

	public String getUsernameFromJWT(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(SECRET)
				.parseClaimsJws(token)
				.getBody();

		return claims.getSubject();
	}
}