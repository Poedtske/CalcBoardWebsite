package com.example.backend.dto;

import com.example.backend.enums.Category;
import com.example.backend.model.Invoice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private String img;
    private Boolean available;
    private int quantity;
    private Category category;
    private List<InvoiceDto> invoices;

    public ProductDto(Long id, String name, BigDecimal price, String img, Category category, Boolean available, List<Invoice> invoices) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.img = img;
        this.category = category;
        this.available=available;
        this.invoices=invoices.stream()
                .map(invoice -> new InvoiceDto(
                        invoice.getId(),
                        invoice.getPaid(),
                        invoice.getConfirmed(),
                        invoice.getClosed(),
                        invoice.getDescription(),
                        invoice.getUser())).collect(Collectors.toList());
    }

    public ProductDto(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }


}
