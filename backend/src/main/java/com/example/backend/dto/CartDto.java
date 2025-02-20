package com.example.backend.dto;

import com.example.backend.model.Product;
import com.example.backend.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Set<ProductDto> products;
    private Set<Ticket> tickets;

    public CartDto(Iterable<Product> productList, Iterable<Ticket> ticketSet) {
        this.tickets = new HashSet<>();
        this.products=new HashSet<>();
        productList.forEach(product -> {
            ProductDto productDto = new ProductDto(
                    product.getId(),
                    product.getName(),
                    product.getPrice()
            );
            products.add(productDto);
        });

        ticketSet.forEach(ticket -> {
            tickets.add(ticket);
        });
    }


}
