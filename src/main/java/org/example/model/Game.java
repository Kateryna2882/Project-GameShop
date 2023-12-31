package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    private int id;
    private String name;
    private LocalDate releaseDate;
    private int rating;
    private double price;
    private String description;
    private String type;
    private double cost;
}

