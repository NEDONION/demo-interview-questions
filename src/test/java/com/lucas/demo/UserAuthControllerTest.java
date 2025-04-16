package com.lucas.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucas.demo.entity.User;
import com.lucas.demo.model.UserAuthModel;
import com.lucas.demo.controller.UserAuthController;
import com.lucas.demo.model.UserRegisterModel;
import com.lucas.demo.service.UserAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserAuthControllerTest {

	@InjectMocks
	private UserAuthController userAuthController;

	@Mock
	private UserAuthService userAuthService;

	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		objectMapper = new ObjectMapper();
	}

	@Test
	public void testUserLogin() throws Exception {
		// 创建一个测试用户认证模型
		UserAuthModel userAuthModel = new UserAuthModel();
		userAuthModel.setUsername("testUser");
		userAuthModel.setHashedPassword("testPassword");

		// 模拟userAuthService的行为
		when(userAuthService.userLogin(userAuthModel)).thenReturn("testToken");

		// 调用Controller方法
		ResponseEntity<String> responseEntity = userAuthController.userLogin(userAuthModel);

		// 验证响应
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("testToken", responseEntity.getBody());

		// 验证是否调用了userAuthService.userLogin方法
		verify(userAuthService, times(1)).userLogin(userAuthModel);
	}

	@Test
	public void testUserLogout() {
		UserAuthModel userAuthModel = new UserAuthModel();
		userAuthModel.setUsername("testUser");

		// 模拟userAuthService.logout方法的行为
		doNothing().when(userAuthService).logout("testUser");

		ResponseEntity<String> responseEntity = userAuthController.userLogout(userAuthModel);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("User logged out successfully", responseEntity.getBody());
	}



}