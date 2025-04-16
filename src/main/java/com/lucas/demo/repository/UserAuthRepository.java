package com.lucas.demo.repository;

import com.lucas.demo.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsernameAndHashedPassword(String username, String hashedPassword);

	boolean existsByUsername(String username);
}
