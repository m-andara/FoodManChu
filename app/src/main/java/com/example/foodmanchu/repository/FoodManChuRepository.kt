package com.example.foodmanchu.repository

import android.content.Context
import androidx.room.Room
import com.example.foodmanchu.MainActivity
import com.example.foodmanchu.models.Ingredient
import com.example.foodmanchu.models.Recipe

object FoodManChuRepository {

    private lateinit var currentRecipe: Recipe
    private const val DATABASE_NAME = "FoodManChu-Database"
    private lateinit var db: FoodManChuDatabase
    private var ingredientsToAdd = mutableSetOf<Int>()
    private var ingredientsToAddNames = mutableSetOf<String>()

    fun setIngredientsToAdd(ingredients: MutableSet<Int>, names: MutableSet<String>) {
        ingredientsToAdd.addAll(ingredients)
        ingredientsToAddNames.addAll(names)
    }

    fun getIngredientsToAdd(): MutableSet<Int> {
        return ingredientsToAdd
    }

    fun getIngredientsToAddNames(): MutableSet<String> {
        return ingredientsToAddNames
    }

    fun clearIngredientsToAdd() {
        ingredientsToAdd.clear()
        ingredientsToAddNames.clear()
    }

    fun setCurrentRecipe(recipe: Recipe) {
        currentRecipe = Recipe(
            recipeId = recipe.recipeId,
            name = recipe.name,
            description = recipe.description,
            prepTime = recipe.prepTime,
            category = recipe.category,
            instructions = recipe.instructions
        )
    }

    fun getCurrentRecipe(): Recipe = currentRecipe

    fun inItDb(context: Context): FoodManChuDatabase {
        db = Room.databaseBuilder(
                context,
                FoodManChuDatabase::class.java,
                DATABASE_NAME
        )
                .fallbackToDestructiveMigration()
                .build()

        return db
    }

    fun getDb(): FoodManChuDatabase {
        return db
    }
}

object IngredientsBaseList {

    private val allRecipeIngredients = mutableListOf<Ingredient>()

    fun getBaseIngredients(): List<Ingredient> {
        return listOf(
            Ingredient(
                ingredient = "Chicken"
            ),
            Ingredient(
                ingredient = "Fish"
            ),
            Ingredient(
                ingredient = "Beef"
            ),
            Ingredient(
                ingredient = "Turkey"
            ),
            Ingredient(
                ingredient = "Ham"
            ),
            Ingredient(
                ingredient = "Sausage"
            ),
            Ingredient(
                ingredient = "Bacon"
            ),
            Ingredient(
                ingredient = "Lettuce"
            ),
            Ingredient(
                ingredient = "Tomato"
            ),
            Ingredient(
                ingredient = "Cilantro"
            ),
            Ingredient(
                ingredient = "Asparagus"
            ),
            Ingredient(
                ingredient = "Onion"
            ),
            Ingredient(
                ingredient = "Beans"
            ),
            Ingredient(
                ingredient = "Romaine Lettuce"
            ),
            Ingredient(
                ingredient = "Broccoli"
            ),
            Ingredient(
                ingredient = "Brussels Sprouts"
            ),
            Ingredient(
                ingredient = "Basil"
            ),
            Ingredient(
                ingredient = "Oregano"
            ),
            Ingredient(
                ingredient = "Salt"
            ),
            Ingredient(
                ingredient = "Pepper"
            ),
            Ingredient(
                ingredient = "Cyan Pepper"
            ),
            Ingredient(
                ingredient = "Cinnamon"
            ),
            Ingredient(
                ingredient = "Kale"
            ),
            Ingredient(
                ingredient = "Spinach"
            ),
            Ingredient(
                ingredient = "Bell Pepper"
            ),
            Ingredient(
                ingredient = "Potato"
            ),
            Ingredient(
                ingredient = "Lentils"
            ),
            Ingredient(
                ingredient = "Green Beans"
            ),
            Ingredient(
                ingredient = "Corn"
            ),
            Ingredient(
                ingredient = "Sour Cream"
            ),
            Ingredient(
                ingredient = "Shrimp"
            ),
            Ingredient(
                ingredient = "Clams"
            ),
            Ingredient(
                ingredient = "Lobster"
            ),
            Ingredient(
                ingredient = "Crab"
            ),
            Ingredient(
                ingredient = "White Rice"
            ),
            Ingredient(
                ingredient = "Brown Rice"
            ),
            Ingredient(
                ingredient = "Avocado"
            ),
            Ingredient(
                ingredient = "Eggplant"
            ),
            Ingredient(
                ingredient = "Squash"
            ),
            Ingredient(
                ingredient = "Carrots"
            ),
            Ingredient(
                ingredient = "Cauliflower"
            ),
            Ingredient(
                ingredient = "Apples"
            ),
            Ingredient(
                ingredient = "Melon"
            ),
            Ingredient(
                ingredient = "Artichokes"
            ),
            Ingredient(
                ingredient = "Mushrooms"
            ),
            Ingredient(
                ingredient = "Cheese"
            ),
            Ingredient(
                ingredient = "Butter"
            ),
            Ingredient(
                ingredient = "Milk"
            ),
            Ingredient(
                ingredient = "Cream"
            ),
            Ingredient(
                ingredient = "Yogurt"
            )
        )
    }

//    fun setAllIngredients(ingredients: List<Ingredient>) {
//        allRecipeIngredients.clear()
//        allRecipeIngredients.addAll(ingredients)
//    }
//
//    fun getAllIngredients(): List<Ingredient> {
//        return allRecipeIngredients
//    }
}