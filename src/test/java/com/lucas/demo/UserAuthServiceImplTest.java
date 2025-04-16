package com.lucas.demo;


import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lucas.demo.entity.User;
import com.lucas.demo.model.UserAuthModel;
import com.lucas.demo.repository.UserAuthRepository;
import com.lucas.demo.service.UserAuthServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class UserAuthServiceImplTest {

	@InjectMocks
	private UserAuthServiceImpl userAuthService;

	@Mock
	private UserAuthRepository userAuthRepository;

	@Mock
	private StringRedisTemplate stringRedisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		// 模拟stringRedisTemplate的行为
		when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get("token:testUser")).thenReturn("yourMockedToken");
	}

	@Test
	public void testUserLogin() {
		// 创建一个测试用户认证模型
		UserAuthModel userAuthModel = new UserAuthModel();
		userAuthModel.setUsername("testUser");
		userAuthModel.setHashedPassword("testPassword");

		// 模拟userRepository的行为
		when(userAuthRepository
				.findByUsernameAndHashedPassword("testUser", "testPassword"))
				.thenReturn(Optional.of(new User()));


		// 调用Service方法
		String token = userAuthService.userLogin(userAuthModel);

		// 验证返回的token不为null
		assertNotNull(token);

		// 验证是否调用了userAuthRepository.findByUsernameAndHashedPassword方法
		verify(userAuthRepository, times(1))
				.findByUsernameAndHashedPassword("testUser", "testPassword");

		// 验证是否调用了stringRedisTemplate.opsForValue().get方法
		verify(stringRedisTemplate, times(2))
				.opsForValue().get("token:testUser");
	}

}