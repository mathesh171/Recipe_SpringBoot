package com.maddy.recipes.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cuisine;
    private String title;
    private Float rating;
    @Column(name = "prep_time")
    private Integer prepTime;
    @Column(name = "cook_time")
    private Integer cookTime;
    @Column(name = "total_time")
    private Integer totalTime;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "JSON")
    private String nutrients;
    private String serves;
    private String calories;
}
