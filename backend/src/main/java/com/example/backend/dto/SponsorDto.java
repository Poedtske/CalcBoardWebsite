package com.example.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class SponsorDto {
    private Long id;
    private String title;
    private String logo;
    private String url;
    private int rank;
    private BigDecimal sponsored;
    private Boolean active;

    //one without sponsored on
    public SponsorDto(Long id, String title, String logo, String url, BigDecimal sponsored) {
        this.id = id;
        this.title = title;
        this.logo = logo;
        this.url = url;
        this.sponsored=sponsored;
        this.active=true;
    }
}
