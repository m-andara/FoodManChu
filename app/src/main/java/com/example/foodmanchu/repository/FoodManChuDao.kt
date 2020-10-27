package com.example.foodmanchu.repository

import androidx.room.*
import com.example.foodmanchu.models.Ingredient
import com.example.foodmanchu.models.Recipe
import com.example.foodmanchu.models.RecipeWithIngredients

@Dao
interface FoodManChuDao {

    @Insert
    fun add(recipe: Recipe): Long

    @Update
    fun update(recipe: Recipe)

    @Delete
    fun delete(recipe: Recipe)

    @Query("DELETE FROM recipewithingredients WHERE ingredientId = :ingredient AND recipeId = :recipe")
    fun deleteRecipeIngredients(recipe: Int, ingredient: Int)

    @Query("SELECT * FROM recipe")
    fun getAllRecipes(): List<Recipe>

    @Insert
    fun addAll(ingredients: List<Ingredient>)

    @Insert
    fun add(ingredient: Ingredient)

    @Delete
    fun delete(ingredient: Ingredient)

    @Query("SELECT * FROM ingredient")
    fun getAllIngredients(): List<Ingredient>

    @Query("SELECT * FROM recipe WHERE recipeId = :recipeId")
    fun getRecipe(recipeId: Int): Recipe

    @Insert
    fun setRecipeIngredients(recipe: RecipeWithIngredients)

    @Query("SELECT * FROM ingredient WHERE ingredientId IN (SELECT ingredientId FROM recipewithingredients WHERE recipeId = :recipeId)")
    fun getAllRecipeIngredients(recipeId: Int): List<Ingredient>

    @Query("SELECT * FROM ingredient WHERE LOWER(ingredient) = :name")
    fun getIngredientByName(name: String): Ingredient

    @Query("DELETE FROM recipewithingredients WHERE ingredientId = :ingredient")
    fun deleteIngredientFromRecipes(ingredient: Int)

    @Query("SELECT * FROM recipe WHERE recipeId IN (SELECT recipeId FROM recipewithingredients WHERE ingredientId = :ingredient)")
    fun filterByIngredients(ingredient: Int): List<Recipe>

    @Query("SELECT * FROM recipe WHERE prep_time <= :time")
    fun filterRecipesByTime(time: Int): List<Recipe>
}