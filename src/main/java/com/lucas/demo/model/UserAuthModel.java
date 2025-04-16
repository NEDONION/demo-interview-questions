package com.lucas.demo.model;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthModel {

	@NotBlank(message = "Username cannot be empty")
	String username;

	@NotBlank(message = "hashedPassword cannot be empty")
	String hashedPassword;
}
