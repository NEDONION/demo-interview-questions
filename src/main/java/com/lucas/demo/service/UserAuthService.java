package com.lucas.demo.service;

import com.lucas.demo.entity.User;
import com.lucas.demo.model.UserAuthModel;
import com.lucas.demo.model.UserRegisterModel;
import org.springframework.stereotype.Service;

@Service
public interface UserAuthService {

	String userLogin(UserAuthModel userAuthModel);

	void logout(String username);

	User registerNewUser(UserRegisterModel userRegisterModel);
}
