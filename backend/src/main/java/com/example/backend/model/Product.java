package com.example.backend.model;

import com.example.backend.enums.Category;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    private String img;


    //if it's available
    private Boolean available=true;

    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
    @Nullable
    @ManyToMany(mappedBy = "products",cascade = CascadeType.REMOVE)
    private List<Invoice> invoices =new ArrayList<>();

    //malajdndf_8.png
    public Product(String name, BigDecimal price, String img, Boolean available, int quantity, Category category) {
        this.name = name;
        this.price = price;
        this.img = img;
        this.available = available;
        this.quantity = quantity;
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }



    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public List<Invoice> getInvoices() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id); // Compare by id if it's not null
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0; // Use id hash code, fallback to 0 if id is null
    }

}
