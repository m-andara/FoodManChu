package com.example.foodmanchu.models

import androidx.room.*

@Entity
data class Recipe (
    @PrimaryKey(autoGenerate = true) val recipeId: Int = 0,
    @ColumnInfo(name="name") val name: String,
    @ColumnInfo(name="description") val description: String,
    @ColumnInfo(name="instructions") val instructions: String,
    @ColumnInfo(name="prep_time") val prepTime: Int,
    @ColumnInfo(name="category") val category: String
)

@Entity(indices = [Index(value = ["ingredient"])])
data class Ingredient (
    @PrimaryKey(autoGenerate = true) val ingredientId: Int = 0,
    @ColumnInfo(name = "ingredient") val ingredient: String,
    @ColumnInfo(name = "isbaseingredient") val isBaseIngredient: Boolean = true
)

@Entity(tableName = "recipewithingredients")
data class RecipeWithIngredients (
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "recipeId") val recipeId: Int,
        @ColumnInfo(name = "ingredientId") val ingredientId: Int
)