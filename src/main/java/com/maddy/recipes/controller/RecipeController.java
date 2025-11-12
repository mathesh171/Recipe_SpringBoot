package com.maddy.recipes.controller;

import com.maddy.recipes.dto.PageResponse;
import com.maddy.recipes.dto.RecipeResponse;
import com.maddy.recipes.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/recipes")
    public PageResponse<RecipeResponse> getAllRecipes(@RequestParam(value = "offSet", defaultValue = "1") int offSet,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return recipeService.getAllRecipes(offSet, pageSize);
    }

    @GetMapping("/recipes/rating")
    public PageResponse<RecipeResponse> getAllRecipesByRating(@RequestParam(value = "offSet", required = false, defaultValue = "1") int offSet,
                                                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return recipeService.getAllRecipesByRating(offSet, pageSize);
    }

    @GetMapping("/search")
    public List<RecipeResponse> searchRecipes(
            @RequestParam(value = "caloriesGreater", required = false) Double caloriesGreater,
            @RequestParam(value = "caloriesLess", required = false) Double caloriesLess,
            @RequestParam(value = "caloriesEqual", required = false) Double caloriesEqual,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "cuisine", required = false) String cuisine,
            @RequestParam(value = "totaltimeEqual", required = false) Integer totalTimeEq,
            @RequestParam(value = "ratingGreater", required = false) Float ratingGr,
            @RequestParam(value = "ratingLess", required = false) Float ratingLe,
            @RequestParam(value = "ratingEqual", required = false) Float ratingEq,
            @RequestParam(value = "totaltimeGreater", required = false) Integer totalTimeGr,
            @RequestParam(value = "totaltimeLess", required = false) Integer totalTimeLe) {
        return recipeService.searchRecipes(
                caloriesGreater, caloriesLess, caloriesEqual,
                title, cuisine,
                totalTimeEq,
                ratingGr, ratingLe, ratingEq,
                totalTimeGr, totalTimeLe);
    }

    @GetMapping("/search/page")
    public PageResponse<RecipeResponse> searchRecipesWithPagination(
            @RequestParam(value = "caloriesGreater", required = false) Double caloriesGreater,
            @RequestParam(value = "caloriesLess", required = false) Double caloriesLess,
            @RequestParam(value = "caloriesEqual", required = false) Double caloriesEqual,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "cuisine", required = false) String cuisine,
            @RequestParam(value = "totaltimeEqual", required = false) Integer totalTimeEq,
            @RequestParam(value = "ratingGreater", required = false) Float ratingGr,
            @RequestParam(value = "ratingLess", required = false) Float ratingLe,
            @RequestParam(value = "ratingEqual", required = false) Float ratingEq,
            @RequestParam(value = "totaltimeGreater", required = false) Integer totalTimeGr,
            @RequestParam(value = "totaltimeLess", required = false) Integer totalTimeLe,
            @RequestParam(value = "offSet", defaultValue = "1") int offSet,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return recipeService.searchRecipesWithPagination(
                caloriesGreater, caloriesLess, caloriesEqual,
                title, cuisine,
                totalTimeEq,
                ratingGr, ratingLe, ratingEq,
                totalTimeGr, totalTimeLe,
                offSet, pageSize);
    }


}
