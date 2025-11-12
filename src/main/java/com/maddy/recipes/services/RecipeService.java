package com.maddy.recipes.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maddy.recipes.dto.PageResponse;
import com.maddy.recipes.dto.RecipeResponse;
import com.maddy.recipes.model.Recipe;
import com.maddy.recipes.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    public void importRecipesFromJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/US_recipes_null.json");
            if (inputStream == null) {
                throw new RuntimeException("US_recipes_null.json not found in resources");
            }
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            List<Recipe> recipes = new ArrayList<>();
            Iterator<String> fieldNames = jsonNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode node = jsonNode.get(fieldName);
                Recipe recipe = new Recipe();
                recipe.setId(null);
                recipe.setCuisine(getText(node, "cuisine"));
                recipe.setTitle(getText(node, "title"));
                recipe.setRating(getValidFloat(node, "rating"));
                recipe.setPrepTime(getValidInt(node, "prep_time"));
                recipe.setCookTime(getValidInt(node, "cook_time"));
                recipe.setTotalTime(getValidInt(node, "total_time"));
                recipe.setDescription(getText(node, "description"));
                recipe.setServes(getText(node, "serves"));
                JsonNode nutrientsNode = node.get("nutrients");
                if (nutrientsNode != null && !nutrientsNode.isNull()) {
                    recipe.setNutrients(nutrientsNode.toString());
                    recipe.setCalories(nutrientsNode.has("calories") ? nutrientsNode.get("calories").asText() : null);
                }
                recipes.add(recipe);
            }
            int batchSize = 500;
            for (int i = 0; i < recipes.size(); i += batchSize) {
                int toIndex = Math.min(i + batchSize, recipes.size());
                List<Recipe> batch = recipes.subList(i, toIndex);
                recipeRepository.saveAll(batch);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to import recipes: " + e.getMessage(), e);
        }
    }

    private String getText(JsonNode node, String key) {
        return node.has(key) && !node.get(key).isNull() ? node.get(key).asText() : null;
    }

    private Integer getValidInt(JsonNode node, String key) {
        try {
            return node.has(key) && node.get(key).isInt() ? node.get(key).asInt() : null;
        } catch(Exception e) {
            return null;
        }
    }

    private Float getValidFloat(JsonNode node, String key) {
        try {
            if (node.has(key) && node.get(key).isNumber()) {
                float value = node.get(key).floatValue();
                if (Float.isNaN(value)) return null;
                return value;
            }
            return null;
        } catch(Exception e) {
            return null;
        }
    }

    public PageResponse<RecipeResponse> getAllRecipes(int offSet, int pageSize) {
        if (pageSize <= 0) pageSize = 10;
        if (offSet <= 0) offSet = 1;
        Pageable pageable = PageRequest.of(offSet - 1, pageSize);
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        List<RecipeResponse> data = recipePage.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        PageResponse<RecipeResponse> response = new PageResponse<>();
        response.setOffSet(offSet);
        response.setPageSize(pageSize);
        response.setTotal(recipePage.getTotalElements());
        response.setData(data);
        return response;
    }


    public PageResponse<RecipeResponse> getAllRecipesByRating(int offSet, int pageSize) {
        if (pageSize <= 0) pageSize = 10;
        if (offSet <= 0) offSet = 1;
        Pageable pageable = PageRequest.of(offSet - 1, pageSize, Sort.by("rating").descending());
        Page<Recipe> pageResult = recipeRepository.findAllByOrderByRatingDesc(pageable);
        List<RecipeResponse> data = pageResult.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        PageResponse<RecipeResponse> resp = new PageResponse<>();
        resp.setOffSet(offSet);
        resp.setPageSize(pageSize);
        resp.setTotal(pageResult.getTotalElements());
        resp.setData(data);
        return resp;
    }

    public List<RecipeResponse> searchRecipes(
            Double calGr, Double calLe, Double calEq,
            String title, String cuisine,
            Integer totalTimeEq,
            Float ratingGr, Float ratingLe, Float ratingEq,
            Integer totalTimeGr, Integer totalTimeLe) {

        List<Recipe> allRecipes = recipeRepository.findAll();
        return allRecipes.stream()
                .filter(r -> title == null || (r.getTitle() != null && r.getTitle().toLowerCase().contains(title.toLowerCase())))
                .filter(r -> cuisine == null || (r.getCuisine() != null && r.getCuisine().equalsIgnoreCase(cuisine)))
                .filter(r -> totalTimeEq == null || (r.getTotalTime() != null && r.getTotalTime().equals(totalTimeEq)))
                .filter(r -> totalTimeLe == null || (r.getTotalTime() != null && r.getTotalTime() <= totalTimeLe))
                .filter(r -> totalTimeGr == null || (r.getTotalTime() != null && r.getTotalTime() >= totalTimeGr))
                .filter(r -> ratingGr == null || (r.getRating() != null && r.getRating() >= ratingGr))
                .filter(r -> ratingLe == null || (r.getRating() != null && r.getRating() <= ratingLe))
                .filter(r -> ratingEq == null || (r.getRating() != null && r.getRating().equals(ratingEq)))
                .filter(r -> {
                    if (calGr == null && calLe == null && calEq == null) return true;
                    if (r.getCalories() == null) return false;
                    String calStr = r.getCalories().replaceAll("[^0-9.]", "");
                    if (calStr.isEmpty()) return false;
                    Double cal;
                    try { cal = Double.valueOf(calStr); } catch (Exception ex) { return false; }
                    if (calGr != null && cal < calGr) return false;
                    if (calLe != null && cal > calLe) return false;
                    if (calEq != null && !Objects.equals(cal, calEq)) return false;
                    return true;
                })
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PageResponse<RecipeResponse> searchRecipesWithPagination(
            Double calGr, Double calLe, Double calEq,
            String title, String cuisine,
            Integer totalTimeEq,
            Float ratingGr, Float ratingLe, Float ratingEq,
            Integer totalTimeGr, Integer totalTimeLe,
            int offSet, int pageSize) {

        List<RecipeResponse> filtered = searchRecipes(
                calGr, calLe, calEq,
                title, cuisine,
                totalTimeEq,
                ratingGr, ratingLe, ratingEq,
                totalTimeGr, totalTimeLe);

        int total = filtered.size();
        int fromIndex = Math.max(0, (offSet - 1) * pageSize);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<RecipeResponse> pageData = filtered.subList(fromIndex, toIndex);

        PageResponse<RecipeResponse> resp = new PageResponse<>();
        resp.setOffSet(offSet);
        resp.setPageSize(pageSize);
        resp.setTotal(total);
        resp.setData(pageData);
        return resp;
    }



    private RecipeResponse toDto(Recipe recipe) {
        RecipeResponse resp = new RecipeResponse();
        resp.setId(recipe.getId());
        resp.setTitle(recipe.getTitle());
        resp.setCuisine(recipe.getCuisine());
        resp.setRating(recipe.getRating());
        resp.setPrepTime(recipe.getPrepTime());
        resp.setCookTime(recipe.getCookTime());
        resp.setTotalTime(recipe.getTotalTime());
        resp.setDescription(recipe.getDescription());
        resp.setNutrients(recipe.getNutrients());
        resp.setServes(recipe.getServes());
        return resp;
    }
}
