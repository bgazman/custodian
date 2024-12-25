package consulting.gazman.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SecurityApplication {

	public static void main(String[] args) {
//		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//		String rawPassword = "testPassword123";
//		String encodedPassword = passwordEncoder.encode(rawPassword);
//		System.out.println("Encoded: " + encodedPassword);
//		System.out.println("Matches: " + passwordEncoder.matches(rawPassword, encodedPassword));
		SpringApplication.run(SecurityApplication.class, args);
	}

}
