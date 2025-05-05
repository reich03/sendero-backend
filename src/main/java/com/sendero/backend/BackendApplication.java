package com.sendero.backend;

import com.sendero.backend.model.User;
import com.sendero.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				User user = new User();
				user.setUsername("admin");
				user.setEmail("admin@sendero.com");
				user.setPassword(passwordEncoder.encode("123456"));
				user.setRole("ADMIN");
				userRepository.save(user);
				System.out.println("✔ Usuario admin insertado con éxito");
			}
		};
	}
}
