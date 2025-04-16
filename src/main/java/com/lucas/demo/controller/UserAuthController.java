package com.lucas.demo.controller;


import com.lucas.demo.entity.User;
import com.lucas.demo.model.UserRegisterModel;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import com.lucas.demo.model.UserAuthModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lucas.demo.service.UserAuthService;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserAuthController {


	private final UserAuthService userAuthService;

	public UserAuthController(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
	}

	@PostMapping("/login")
	public ResponseEntity<String> userLogin(@Valid @RequestBody UserAuthModel userAuthModel) {

		log.info("userLoginModel is " + userAuthModel);
		String token = null;
		try {
			token = userAuthService.userLogin(userAuthModel);
		} catch (Exception ex) {
			log.error("UserLoginController.userLogin() exception: " + ex.getMessage());
			return new ResponseEntity<>("Getting token failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (token != null) {
			return new ResponseEntity<>(token, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("token is null", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<String> userLogout(@Valid @RequestBody UserAuthModel userAuthModel) {
		log.info("Logout request for user: " + userAuthModel.getUsername());
		try {
			userAuthService.logout(userAuthModel.getUsername());
			return new ResponseEntity<>("User logged out successfully", HttpStatus.OK);
		} catch (Exception ex) {
			log.error("UserLogoutController.userLogout() exception: " + ex.getMessage());
			return new ResponseEntity<>("Logout failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/register")
	public ResponseEntity<User> userRegister(@Valid @RequestBody UserRegisterModel userRegisterModel) {
		log.info("Registering user: " + userRegisterModel.getUsername());
		try {
			User user = userAuthService.registerNewUser(userRegisterModel);
			log.info("User registered successfully: " + user);
			return new ResponseEntity<>(user, HttpStatus.CREATED);
		} catch (Exception ex) {
			log.error("UserLoginController.userRegister() exception: " + ex.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
