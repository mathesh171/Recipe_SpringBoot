package com.maddy.recipes.dto;

import lombok.Data;

@Data
public class RecipeResponse {
    private Long id;
    private String title;
    private String cuisine;
    private Float rating;
    private Integer prepTime;
    private Integer cookTime;
    private Integer totalTime;
    private String description;
    private String nutrients;
    private String serves;
}
