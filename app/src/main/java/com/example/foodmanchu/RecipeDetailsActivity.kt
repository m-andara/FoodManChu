package com.example.foodmanchu

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodmanchu.databinding.ActivityRecipeDetailsBinding
import com.example.foodmanchu.dialogs.EditRecipeDialog
import com.example.foodmanchu.models.Ingredient
import com.example.foodmanchu.models.Recipe
import com.example.foodmanchu.repository.FoodManChuDatabase
import com.example.foodmanchu.repository.FoodManChuRepository

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailsBinding
    private val database = FoodManChuRepository.getDb()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateRecipe()
    }

    private fun setUI(recipe: Recipe, ingredients: List<Ingredient>) {
        binding.apply {
            detailsName.text = recipe.name
            detailsCategory.text = recipe.category
            detailsPreptime.text = getString(R.string.details_prep_time_value, "${recipe.prepTime.toString()} minutes")
            detailsDescription.text = recipe.description
            detailsInstructions.text = recipe.instructions
            detailsIngredients.text =
                ingredients.joinToString(", ") { ingredient -> ingredient.ingredient }
            editButton.setOnClickListener {
                editDialog()
            }
            deleteButton.setOnClickListener {
                deleteRecipe()
            }
            copyButton.setOnClickListener {
                copyRecipe()
            }
        }
    }

    private fun editDialog() {
        EditRecipeDialog.create{
            updateRecipe()
        }.show(supportFragmentManager, "Edit Recipe Dialog")
    }

    private fun deleteRecipe() {
        AsyncTask.execute {
            database.apply {
                foodManChuDao().delete(FoodManChuRepository.getCurrentRecipe())
            }
        }
        finish()
    }

    private fun copyRecipe() {
        val currentRecipe = FoodManChuRepository.getCurrentRecipe()
        val copyRecipe = Recipe(
            name = currentRecipe.name,
            description = currentRecipe.description,
            prepTime = currentRecipe.prepTime,
            instructions = currentRecipe.instructions,
            category = currentRecipe.category
        )
        AsyncTask.execute {
            database.apply {
                foodManChuDao().add(copyRecipe)
            }
        }
        finish()
    }

    private fun updateRecipe() {
        super.onResume()
        UpdateRecipe{
            GetRecipeIngredients() { ingredients ->
                setUI(FoodManChuRepository.getCurrentRecipe(), ingredients)
            }.execute()
        }.execute()
    }
}

class UpdateRecipe(private val onFinished: () -> Unit): AsyncTask<Void, Void, Recipe>() {

    private var database: FoodManChuDatabase = FoodManChuRepository.getDb()
    override fun doInBackground(vararg params: Void?): Recipe? {
        val recipe = database?.foodManChuDao()?.getRecipe(FoodManChuRepository.getCurrentRecipe().recipeId)
        return recipe
    }

    override fun onPostExecute(result: Recipe?) {
        super.onPostExecute(result)
        if (result != null) {
            FoodManChuRepository.setCurrentRecipe(result)
        }
        onFinished()
    }
}