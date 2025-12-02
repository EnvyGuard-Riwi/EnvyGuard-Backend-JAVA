package com.envyguard.envyguard_backend.service;

import com.envyguard.envyguard_backend.entity.User;
import com.envyguard.envyguard_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializerService implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${app.admin.email:admin@envyguard.com}")
    private String adminEmail;
    
    @Value("${app.admin.password:Admin123!}")
    private String adminPassword;
    
    @Value("${app.admin.firstName:EnvyGuard}")
    private String adminFirstName;
    
    @Value("${app.admin.lastName:Administrator}")
    private String adminLastName;
    
    @Override
    public void run(String... args) throws Exception {
        createAdminUser();
    }
    
    private void createAdminUser() {
        // Verificar si ya existe un usuario admin
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User(
                adminEmail,
                passwordEncoder.encode(adminPassword),
                adminFirstName,
                adminLastName
            );
            
            userRepository.save(admin);
            System.out.println("==========================================");
            System.out.println("ADMIN USER CREATED SUCCESSFULLY");
            System.out.println("Email: " + adminEmail);
            System.out.println("Note: Store the admin password securely. Do not commit to version control.");
            System.out.println("==========================================");
        } else {
            System.out.println("Admin user already exists");
        }
    }
}
