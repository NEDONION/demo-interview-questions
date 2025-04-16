package com.lucas.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long userId;

	@Column(name = "username")
	String username;

	@Column(name = "hashed_password")
	String hashedPassword;

	@Column(name = "email")
	String email;

	@Column(name = "age")
	int age;

	public User(long userId, String username, String hashedPassword, String email, int age) {
		this.userId = userId;
		this.username = username;
		this.hashedPassword = hashedPassword;
		this.email = email;
		this.age = age;
	}

	public User() {

	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}