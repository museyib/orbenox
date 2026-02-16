package com.orbenox.erp;

import com.orbenox.erp.security.entity.AppUser;
import com.orbenox.erp.security.entity.UserType;
import com.orbenox.erp.security.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableCaching
public class OrbenoxApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrbenoxApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder, EntityManager em) {
        return (args) -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setDisplayName("Admin");
                admin.setUserType(em.getReference(UserType.class, 1L));
                admin.setRoot(true);
                admin.setCreatedBy("system");
                admin.setEnabled(true);
                admin.setDeleted(false);
                userRepository.save(admin);
            }
        };
    }
}
