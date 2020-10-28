package com.example.foodmanchu

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodmanchu.adapters.RecipesAdapter
import com.example.foodmanchu.databinding.ActivityMainBinding
import com.example.foodmanchu.dialogs.AddRecipeDialog
import com.example.foodmanchu.dialogs.FilterRecipeDialog
import com.example.foodmanchu.models.Recipe
import com.example.foodmanchu.models.RecipeWithIngredients
import com.example.foodmanchu.repository.FoodManChuDatabase
import com.example.foodmanchu.repository.FoodManChuRepository
import com.example.foodmanchu.repository.IngredientsBaseList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val recipesAdapter = RecipesAdapter() {recipe ->
        onRecipeClicked(recipe)
    }
    private var database: FoodManChuDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setUpAdapter(recipesList)
            add.setOnClickListener {
                AddRecipeDialog.create { recipe, ingredients ->
                    addRecipe(recipe, ingredients)
                }.show(supportFragmentManager, "Add Recipe Dialog")
            }
            filterRecipes.setOnClickListener {
                onFilterRecipe()
            }
        }
        database = FoodManChuRepository.inItDb(applicationContext)
    }

    private fun setUpAdapter(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = recipesAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        AsyncTask.execute {
            if(database?.foodManChuDao()?.getAllIngredients()?.size == 0) {
                database?.foodManChuDao()?.addAll(IngredientsBaseList.getBaseIngredients())
            }
            val recipes = database?.foodManChuDao()?.getAllRecipes()
            recipesAdapter.submitList(recipes)
        }
    }

    private fun addRecipe(recipe: Recipe, ingredients: List<Int>) = AsyncTask.execute {
        database?.apply {
            val addedRecipe = foodManChuDao().add(recipe)
            ingredients.forEach {ingredient ->
                val recipeWithIngredients = RecipeWithIngredients(
                    recipeId = addedRecipe.toInt(),
                        ingredientId = ingredient
                )
                foodManChuDao().setRecipeIngredients(recipeWithIngredients)
            }
            val items = foodManChuDao().getAllRecipes()
            recipesAdapter.submitList(items)
        }
        FoodManChuRepository.clearIngredientsToAdd()
    }

    private fun onRecipeClicked(recipe: Recipe) {
        FoodManChuRepository.setCurrentRecipe(recipe)
        val intent = Intent(this@MainActivity, RecipeDetailsActivity::class.java)
        startActivity(intent)
    }

    private fun onFilterRecipe() {
        FilterRecipeDialog.create { type, search ->
            filterDb(type, search)
        }.show(supportFragmentManager, "Filter Recipe Dialog")
    }

    private fun filterDb(filterType: String, filter: String) {
        AsyncTask.execute {
            when  (filterType) {
                "keywords" -> {
                    val recipes = database?.foodManChuDao()?.getAllRecipes()?.filter { recipe ->
                        recipe.category.toLowerCase().contains(filter) ||
                                recipe.description.toLowerCase().contains(filter) ||
                                recipe.name.toLowerCase().contains(filter) ||
                                recipe.instructions.toLowerCase().contains(filter)
                    }
                    recipesAdapter.submitList(recipes)
                }
                "ingredients" -> {
                    val ingredient = database?.foodManChuDao()?.getIngredientByName(filter)
                    if (ingredient != null) {
                        val recipes = database?.foodManChuDao()?.filterByIngredients(ingredient.ingredientId)
                        recipesAdapter.submitList(recipes)
                    }
                }
                "time" -> {
                    val recipes = database?.foodManChuDao()?.filterRecipesByTime(filter.toInt())
                        recipesAdapter.submitList(recipes)
                }
                else -> {
                    val recipes = database?.foodManChuDao()?.getAllRecipes()
                    recipesAdapter.submitList(recipes)
                }
            }
        }
    }
}