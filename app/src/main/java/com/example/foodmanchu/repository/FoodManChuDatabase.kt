package com.example.foodmanchu.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.foodmanchu.models.Ingredient
import com.example.foodmanchu.models.Recipe
import com.example.foodmanchu.models.RecipeWithIngredients

@Database(entities = [Recipe::class, Ingredient::class, RecipeWithIngredients::class], version = 1)
abstract class FoodManChuDatabase: RoomDatabase() {

    abstract fun foodManChuDao(): FoodManChuDao
}