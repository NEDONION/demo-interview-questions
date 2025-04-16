package com.lucas.demo.service;


import com.lucas.demo.entity.User;
import com.lucas.demo.model.UserRegisterModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Optional;
import com.lucas.demo.model.UserAuthModel;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.lucas.demo.repository.UserAuthRepository;

@Slf4j
@Service
public class UserAuthServiceImpl implements UserAuthService {


	private final UserAuthRepository userAuthRepository;

	private final StringRedisTemplate stringRedisTemplate;

	public UserAuthServiceImpl(UserAuthRepository userAuthRepository, StringRedisTemplate stringRedisTemplate) {
		this.userAuthRepository = userAuthRepository;
		this.stringRedisTemplate = stringRedisTemplate;
	}

	private static final String TOKEN_PREFIX = "token:";
	private static final long EXPIRATION_TIME = 30000; // 30 seconds
	//	private static final long EXPIRATION_TIME = 900_000; // 15 minutes
	private static final String SECRET = "YourSecret"; // Use a strong, unique secret key

	@Override
	public String userLogin(UserAuthModel userAuthModel) {

		// Check if there is a token in Redis
		String storedToken = stringRedisTemplate.opsForValue().get(TOKEN_PREFIX + userAuthModel.getUsername());

		if (storedToken != null && validateToken(storedToken, userAuthModel.getUsername())) {
			return storedToken;
		} else {

			Optional<User> user = userAuthRepository
					.findByUsernameAndHashedPassword(
							userAuthModel.getUsername(), userAuthModel.getHashedPassword());

			if (user.isPresent()) {
				String token = generateNewToken(userAuthModel);

				// Store the token in Redis with an expiration time
				stringRedisTemplate.opsForValue().set(
						TOKEN_PREFIX + userAuthModel.getUsername(),
						token,
						EXPIRATION_TIME,
						TimeUnit.MILLISECONDS);
				return token;
			} else {
				return null;
			}
		}
	}

	@Override
	public void logout(String username) {

		String storedToken = stringRedisTemplate.opsForValue().get(TOKEN_PREFIX + username);

		if (storedToken == null) {
			log.info("User is not logged in : " + username);
		}

		// Remove the token from Redis
		stringRedisTemplate.delete(TOKEN_PREFIX + username);
	}


	@Override
	public User registerNewUser(UserRegisterModel userRegisterModel) {
		// Check if the username is already taken
		if (userAuthRepository.existsByUsername(userRegisterModel.getUsername())) {
			throw new RuntimeException("Username already taken");
		}

		// Hash the password
		String hashedPassword = hashPassword(userRegisterModel.getPassword());

		User newUser = new User();
		newUser.setUsername(userRegisterModel.getUsername());
		newUser.setHashedPassword(hashedPassword);
		newUser.setEmail(userRegisterModel.getEmail());
		newUser.setAge(userRegisterModel.getAge());

		userAuthRepository.save(newUser);
		return newUser;
	}

	private String hashPassword(String password) {
		// Implement password hashing logic, possibly using BCrypt or another hashing algorithm
		return password;
	}

	private boolean validateToken(String token, String username) {
		try {
			Claims claims = Jwts.parser()
					.setSigningKey(SECRET.getBytes())
					.parseClaimsJws(token)
					.getBody();

			// Check for the expiration and the username
			return username.equals(claims.getSubject()) && !claims.getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			// Token is out of date
			return false;
		} catch (Exception e) {
			log.error("validateToken failed: " + username);
			return false;
		}
	}

	private String generateNewToken(UserAuthModel userAuthModel) {
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		return Jwts.builder()
				.setSubject(userAuthModel.getUsername())
				.setIssuedAt(now)
				.setExpiration(new Date(nowMillis + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
				.compact();
	}
}