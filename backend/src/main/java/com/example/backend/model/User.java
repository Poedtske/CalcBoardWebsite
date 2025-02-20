package com.example.backend.model;

import com.example.backend.dto.ProductPaymentDTO;
import com.example.backend.enums.Role;
import com.example.backend.exceptions.AppException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.*;

@Entity
@Builder

public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    @Column(unique = true) // Ensure email is unique in the database
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // Ensure a role is always set
    private Role role = Role.USER; // Default role

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Invoice> invoices = new HashSet<>();

    public User() {

    }

    public User(Long id, String firstName, String lastName, String password, String email, Role role, Set<Invoice> invoices) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.role = role;


    }

    public User(String firstName, String lastName, String password, String email, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //importand for authorization
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    public Set<Invoice> getInvoices() {
        return invoices;
    }

    public Invoice addInvoice(Invoice i){
        this.invoices.add(i);
        return i;
    }

    public Invoice removeInvoice(Invoice i){
        this.invoices.remove(i);
        return i;
    }

    //gets the invoice that isn't paid
    public Invoice getActiveInvoice(){
        return invoices.stream().filter(invoice -> invoice.getPaid()==false).findFirst().orElse(null);
    }

    //sets not paid invoice on paid and creates new unpaid invoice
    public void pay(){
        Invoice i= getActiveInvoice();
        if(i.getProducts().isEmpty()&&i.getTickets().isEmpty()){
            throw new AppException("Cart is empty", HttpStatus.BAD_REQUEST);
        }


        i.setPaid(true);
        this.addInvoice(new Invoice(this));
    }
}
