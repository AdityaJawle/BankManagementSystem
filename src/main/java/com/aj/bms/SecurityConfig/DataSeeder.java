package com.aj.bms.SecurityConfig;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.aj.bms.Entity.Users;  
import com.aj.bms.Dao.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usersRepository.findByCrn("1000").isEmpty()) {
            Users admin = new Users();
            admin.setName("Super Admin");
            admin.setCrn("1000");
            admin.setAccountNo("00001");
            admin.setPin(passwordEncoder.encode("admin123")); 
            admin.setBalance(0.0);
            admin.setStatus("ACTIVE");
            admin.setRole("ADMIN");

            usersRepository.save(admin);
            System.out.println("✅ Admin user seeded.Default admin user created: crn=1000, password=admin123");
        } else {
            System.out.println("ℹ️ Admin user already exists. Skipping seeding.");
        }
    }
}

