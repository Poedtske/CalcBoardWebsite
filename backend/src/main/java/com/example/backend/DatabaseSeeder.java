package com.example.backend;

import com.example.backend.enums.Category;
import com.example.backend.enums.Role;
import com.example.backend.model.Invoice;
import com.example.backend.model.Product;
import com.example.backend.model.User;
import com.example.backend.repository.ProductRepository;
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
    private final ProductRepository productRepository;
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
        List<Product> productList=new ArrayList<>();

        // Create admin user
        User admin = User.builder()
                .firstName("David")
                .lastName("vanSteertegem")
                .password(passwordEncoder.encode("1"))
                .email("david.van.steertegem@ehb.be")
                .role(Role.ADMIN)
                .build();
        admin.addInvoice(new Invoice(admin));
        userList.add(admin);

        User admin2=User.builder()
                .firstName("Robbe")
                .lastName("Poedts")
                .password(passwordEncoder.encode("1"))
                .email("robbe.poedts@hotmail.be")
                .role(Role.ADMIN)
                .build();
        admin2.addInvoice(new Invoice(admin));
        userList.add(admin2);

        // Save admin user
        User user = User.builder()
                .firstName("Robbe")
                .lastName("Poedts")
                .password(passwordEncoder.encode("1"))
                .email("robbe.poedts@student.ehb.be")
                .role(Role.USER)
                .build();
        user.addInvoice(new Invoice(user));
        userList.add(user);

        userList.stream().forEach(u->userRepository.save(u));


        //adding products
        productList.add( Product.builder()
                .name("Vanille Wafels met Chocolade")
                .price(BigDecimal.valueOf(7))
                .available(true)
                .category(Category.FOOD)
                .img("chocoladewafel.avif")
                .build());

        productList.add( Product.builder()
                .name("Vanille Wafels")
                .price(BigDecimal.valueOf(7))
                .available(true)
                .category(Category.FOOD)
                .img("vanillewafel.avif")
                .build());
        productList.add( Product.builder()
                .name("Boterwafels Chocolade")
                .price(BigDecimal.valueOf(5))
                .available(true)
                .category(Category.FOOD)
                .img("chocolade-boterwafel.avif")
                .build());
        productList.add( Product.builder()
                .name("Boterwafels")
                .price(BigDecimal.valueOf(5))
                .available(true)
                .category(Category.FOOD)
                .img("boterwafel.avif")
                .build());
        productList.add( Product.builder()
                .name("Frangipane rond")
                .price(BigDecimal.valueOf(5))
                .available(true)
                .category(Category.FOOD)
                .img("franchipane-rond.avif")
                .build());
        productList.add( Product.builder()
                .name("Frangipane")
                .price(BigDecimal.valueOf(5))
                .available(true)
                .category(Category.FOOD)
                .img("franchipane.avif")
                .build());
        productList.add( Product.builder()
                .name("Carré-confituur")
                .price(BigDecimal.valueOf(5))
                .available(true)
                .category(Category.FOOD)
                .img("carré-confituur.avif")
                .build());
        productList.add( Product.builder()
                .name("Gevulde Wafeltjes")
                .price(BigDecimal.valueOf(5))
                .available(true)
                .category(Category.FOOD)
                .img("gevuldeWafels.avif")
                .build());

        productList.add( Product.builder()
                .name("Cola")
                .price(BigDecimal.valueOf(1.5))
                .available(true)
                .category(Category.DRINK)
                .img("cola.jpg")
                .build());
        productList.add( Product.builder()
                .name("Ice-Tea")
                .price(BigDecimal.valueOf(1.5))
                .available(true)
                .category(Category.DRINK)
                .img("ice-tea.avif")
                .build());
        productList.add( Product.builder()
                .name("Fanta")
                .price(BigDecimal.valueOf(1.5))
                .available(true)
                .category(Category.DRINK)
                .img("fanta.jpg")
                .build());
        productList.stream().forEach(product -> productRepository.save(product));


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
