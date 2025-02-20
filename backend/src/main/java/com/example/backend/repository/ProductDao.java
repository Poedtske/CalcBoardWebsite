package com.example.backend.repository;

import com.example.backend.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductDao  extends CrudRepository<Product,Long> {
    Optional<Product> getProductsById(Long id);
}
