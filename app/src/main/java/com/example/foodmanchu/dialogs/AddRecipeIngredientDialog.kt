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
import com.example.foodmanchu.adapters.IngredientsAdapter
import com.example.foodmanchu.databinding.FragmentAddIngredientBinding
import com.example.foodmanchu.models.Ingredient
import com.example.foodmanchu.repository.FoodManChuDatabase
import com.example.foodmanchu.repository.FoodManChuRepository

class AddRecipeIngredientDialog(): DialogFragment() {

    companion object {
        fun create(onIngredientsAddedListener: (Int) -> Unit): AddRecipeIngredientDialog {
            return AddRecipeIngredientDialog().apply {
                this.onIngredientsAddedListener = onIngredientsAddedListener
            }
        }
    }

    private var onIngredientsAddedListener: (Int) -> Unit = {}
    private val ingredientsAdapter = IngredientsAdapter() { ingredient, name ->
        onIngredientClicked(ingredient, name)
    }
    private var database: FoodManChuDatabase? = null
    private var ingredients = mutableListOf<Ingredient>()
    private lateinit var binding: FragmentAddIngredientBinding
    private var selectedIngredients = mutableListOf<Ingredient>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(requireContext())
        binding = FragmentAddIngredientBinding.inflate(inflater)
        database = FoodManChuRepository.getDb()
        binding.ingredientsList.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
            adapter = ingredientsAdapter
        }
        GetAllIngredients{  allIngredients ->
            ingredients.addAll(allIngredients)
            ingredientsAdapter.submitList(allIngredients)
            refreshSelectedIngredients()
        }.execute()
        setUpUi()
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Done", null)
            .create()
    }

    private fun refreshSelectedIngredients() {
        GetRecipeIngredients{ingredients ->
            selectedIngredients.clear()
            selectedIngredients.addAll(ingredients)
        }.execute()
    }

    private fun setUpUi() {
        binding.searchEditText.addTextChangedListener {
            val search = binding.searchEditText.text.toString()
            var filteredIngredients = ingredients.filter { ingredient ->
                ingredient.ingredient.toLowerCase().contains(search.toLowerCase())
            }
            ingredientsAdapter.submitList(filteredIngredients)
            ingredientsAdapter.notifyDataSetChanged()
        }
    }

    private fun onIngredientClicked(ingredient: Int, name: String) {
        val found = selectedIngredients.find { ing -> ing.ingredientId == ingredient }
        if(found != null){
            Toast.makeText(requireContext(), "Ingredient already added", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "$name added", Toast.LENGTH_SHORT).show()
            onIngredientsAddedListener(ingredient)
            refreshSelectedIngredients()
        }
    }
}

class GetAllIngredients(private val onFinished: (List<Ingredient>) -> Unit): AsyncTask<Void, Void, List<Ingredient>>() {

    private var database: FoodManChuDatabase = FoodManChuRepository.getDb()
    private var ingredients = listOf<Ingredient>()

    override fun doInBackground(vararg params: Void?): List<Ingredient>? {
        return database?.foodManChuDao()?.getAllIngredients()!!
    }

    override fun onPostExecute(result: List<Ingredient>?) {
        super.onPostExecute(result)
        if (result != null) {
            onFinished(result)
        }
    }
}