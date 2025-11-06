package com.orbenox.erp;

import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.enums.UserType;
import com.orbenox.erp.security.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class OrbenoxApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrbenoxApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return (args) -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setDisplayName("Admin");
                admin.setUserType(UserType.ADMIN);
                admin.setRoot(true);
                admin.setCreatedBy("system");
                userRepository.save(admin);
            }
        };
    }
}
