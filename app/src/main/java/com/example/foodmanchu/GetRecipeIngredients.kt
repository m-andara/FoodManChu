package com.example.foodmanchu

import android.os.AsyncTask
import com.example.foodmanchu.databinding.ActivityRecipeDetailsBinding
import com.example.foodmanchu.models.Ingredient
import com.example.foodmanchu.models.Recipe
import com.example.foodmanchu.repository.FoodManChuDatabase
import com.example.foodmanchu.repository.FoodManChuRepository

class GetRecipeIngredients(private val onFinished: (List<Ingredient>) -> Unit): AsyncTask<Void, Void, List<Ingredient>>() {

    private var database: FoodManChuDatabase = FoodManChuRepository.getDb()
    private var ingredients = listOf<Ingredient>()

    override fun doInBackground(vararg params: Void?): List<Ingredient>? {
        ingredients = database?.foodManChuDao()?.getAllRecipeIngredients(
                FoodManChuRepository.getCurrentRecipe().recipeId
        )!!
        return ingredients
    }

    override fun onPostExecute(result: List<Ingredient>?) {
        super.onPostExecute(result)
        if (result != null) {
            onFinished(result)
        }
    }
}