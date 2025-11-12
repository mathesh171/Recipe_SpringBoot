package com.maddy.recipes;

import com.maddy.recipes.repository.RecipeRepository;
import com.maddy.recipes.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @PostConstruct
    public void seedRecipes() {
        if (recipeRepository.count() == 0) {
            recipeService.importRecipesFromJson();
        }
    }
}
