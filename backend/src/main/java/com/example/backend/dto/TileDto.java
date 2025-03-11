package com.example.backend.dto;

import java.util.List;

public class TileDto {
    private String Img;
    private List<String> Words;
    private Long Id;

    // Getters and Setters
    public String getImg() { return Img; }
    public void setImg(String img) { this.Img = img; }
    public List<String> getWords() { return Words; }
    public void setWords(List<String> words) { this.Words = words; }
    public Long getId() { return Id; }
    public void setId(Long id) { this.Id = id; }
}
