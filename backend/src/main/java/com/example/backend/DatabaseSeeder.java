package com.example.backend;

import com.example.backend.enums.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//seeder

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if the database has been seeded
        if (isDatabaseSeeded()) {
            System.out.println("Database already seeded. Skipping...");
            return;
        }

        System.out.println("Seeding database...");

        List<User> userList=new ArrayList<>();

        // Create admin user
        User admin = User.builder()
                .firstName("David")
                .lastName("vanSteertegem")
                .password(passwordEncoder.encode("1"))
                .email("david.van.steertegem@ehb.be")
                .role(Role.ADMIN)
                .build();
        userList.add(admin);

        User admin2=User.builder()
                .firstName("Robbe")
                .lastName("Poedts")
                .password(passwordEncoder.encode("1"))
                .email("robbe.poedts@hotmail.be")
                .role(Role.ADMIN)
                .build();
        userList.add(admin2);

        // Save admin user
        User user = User.builder()
                .firstName("Robbe")
                .lastName("Poedts")
                .password(passwordEncoder.encode("1"))
                .email("robbe.poedts@student.ehb.be")
                .role(Role.USER)
                .build();
        userList.add(user);

        userList.stream().forEach(u->userRepository.save(u));

        // Mark database as seeded
        markDatabaseAsSeeded();

        System.out.println("Database seeded successfully.");
    }

    private boolean isDatabaseSeeded() {
        // Check if the admin user already exists (indicating database is seeded)
        Optional<User> admin = userRepository.findByEmail("david.van.steertegem@ehb.be");
        return admin.isPresent();
    }

    private void markDatabaseAsSeeded() {
        // You can implement additional logic here if required, such as storing a flag in a separate table.
    }
}
