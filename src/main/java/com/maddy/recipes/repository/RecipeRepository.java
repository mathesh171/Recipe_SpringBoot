package com.maddy.recipes.repository;

import com.maddy.recipes.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Page<Recipe> findAllByOrderByRatingDesc(Pageable pageable);
}
