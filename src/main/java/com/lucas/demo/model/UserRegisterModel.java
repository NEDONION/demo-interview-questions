package com.lucas.demo.model;


import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterModel {
	@NotBlank(message = "username cannot be empty")
	private String username;

	@NotBlank(message = "password cannot be empty")
	private String password;

	private String email;

	private int age;
}
