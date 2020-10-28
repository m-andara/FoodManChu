package com.example.foodmanchu.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodmanchu.GetRecipeIngredients
import com.example.foodmanchu.adapters.EditIngredientsAdapter
import com.example.foodmanchu.databinding.FragmentEditIngredientsBinding
import com.example.foodmanchu.models.Ingredient
import com.example.foodmanchu.models.RecipeWithIngredients
import com.example.foodmanchu.repository.FoodManChuDatabase
import com.example.foodmanchu.repository.FoodManChuRepository

class EditRecipeIngredientsDialog: DialogFragment() {

    companion object {
        fun create(onRecipeIngredientsEditedListener: () -> Unit): EditRecipeIngredientsDialog {
            return EditRecipeIngredientsDialog().apply {
                this.onRecipeIngredientsEditedListener = onRecipeIngredientsEditedListener
            }
        }
    }

    private var onRecipeIngredientsEditedListener: () -> Unit = {}
    private val ingredientsAdapter = EditIngredientsAdapter() { ingredient, name ->
        onIngredientClicked(ingredient, name)
    }
    private var database: FoodManChuDatabase? = null
    private var ingredients = mutableListOf<Ingredient>()
    private lateinit var binding: FragmentEditIngredientsBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(requireContext())
        binding = FragmentEditIngredientsBinding.inflate(inflater)
        database = FoodManChuRepository.getDb()
        binding.ingredientsList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = ingredientsAdapter
        }
        GetRecipeIngredients {ingredients ->
            refreshIngredients(ingredients)
        }.execute()
        return setUpUi()
    }

    private fun setUpUi(): Dialog {
        binding.addIngredients.visibility = View.VISIBLE
        binding.addIngredients.apply {
            setOnClickListener {
                AddRecipeIngredientDialog.create{ingredient ->
                    addIngredientToRecipe(ingredient)
                }.show(requireFragmentManager(), "Add Recipe Ingredients Dialog")
            }
        }
        binding.searchEditText.addTextChangedListener {
            val search = binding.searchEditText.text.toString()
            var filteredIngredients: List<Ingredient>
            filteredIngredients = ingredients.filter { ingredient ->
                ingredient.ingredient.toLowerCase().contains(search.toLowerCase())
            }
            ingredientsAdapter.submitList(filteredIngredients)
            ingredientsAdapter.notifyDataSetChanged()
        }

        return AlertDialog.Builder(requireContext())
                .setView(binding.root)
                .setPositiveButton("Done", null)
                .create()
    }

    private fun onIngredientClicked(ingredient: Int, name: String) {
        binding.searchEditText.setText("")
        AsyncTask.execute {
            database?.foodManChuDao()?.deleteRecipeIngredients(
                    FoodManChuRepository.getCurrentRecipe().recipeId,
                    ingredient
            )
            GetRecipeIngredients{
                refreshIngredients(it)
                onRecipeIngredientsEditedListener()
            }.execute()
        }
    }

    private fun refreshIngredients(recipeIngredients: List<Ingredient>) {
        ingredients.clear()
        ingredients.addAll(recipeIngredients)
        ingredientsAdapter.submitList(ingredients)
        ingredientsAdapter.notifyDataSetChanged()
    }

    private fun addIngredientToRecipe(ingredient: Int) {
        AddRecipeIngredient(ingredient, FoodManChuRepository.getCurrentRecipe().recipeId) {
            GetRecipeIngredients{
                refreshIngredients(it)
                onRecipeIngredientsEditedListener()
            }.execute()
        }.execute()
    }
}

class AddRecipeIngredient(private val ingredient: Int, private val recipe: Int, private val onFinished: () -> Unit): AsyncTask<Void, Void, Int>() {

    private var database: FoodManChuDatabase = FoodManChuRepository.getDb()
    override fun doInBackground(vararg params: Void?): Int? {
        val recipeWithIngredients = RecipeWithIngredients(
            recipeId = recipe,
            ingredientId = ingredient
        )
        database.foodManChuDao().setRecipeIngredients(recipeWithIngredients)
        return 1
    }

    override fun onPostExecute(result: Int?) {
        super.onPostExecute(result)
        onFinished()
    }
}