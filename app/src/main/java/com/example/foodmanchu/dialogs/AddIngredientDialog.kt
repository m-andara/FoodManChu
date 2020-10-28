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
import com.example.foodmanchu.adapters.IngredientsAdapter
import com.example.foodmanchu.databinding.FragmentAddIngredientBinding
import com.example.foodmanchu.models.Ingredient
import com.example.foodmanchu.repository.FoodManChuDatabase
import com.example.foodmanchu.repository.FoodManChuRepository

class AddIngredientDialog(): DialogFragment() {

    companion object {
        fun create(onIngredientsAddedListener: () -> Unit): AddIngredientDialog {
            return AddIngredientDialog().apply {
                this.onIngredientsAddedListener = onIngredientsAddedListener
            }
        }
    }

    private var onIngredientsAddedListener: () -> Unit = {}
    private val ingredientsAdapter = IngredientsAdapter() { ingredient, name ->
        onIngredientClicked(ingredient, name)
    }
    private var database: FoodManChuDatabase? = null
    private var ingredients = mutableListOf<Ingredient>()
    private lateinit var binding: FragmentAddIngredientBinding
    private var selectedIngredients = FoodManChuRepository.getIngredientsToAdd()
    private var selectedIngredientsNames = FoodManChuRepository.getIngredientsToAddNames()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(requireContext())
        binding = FragmentAddIngredientBinding.inflate(inflater)
        database = FoodManChuRepository.getDb()
        binding.ingredientsList.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
            adapter = ingredientsAdapter
        }
        AsyncTask.execute {
            ingredients.clear()
            ingredients.addAll(database?.foodManChuDao()?.getAllIngredients()!!)
            ingredientsAdapter.submitList(ingredients)
        }
        return setUpUi()
    }

    private fun setUpUi(): Dialog {
        binding.searchEditText.addTextChangedListener {
            val search = binding.searchEditText.text.toString()
            var filteredIngredients: List<Ingredient>
            filteredIngredients = ingredients.filter { ingredient ->
                ingredient.ingredient.toLowerCase().contains(search.toLowerCase())
            }
            ingredientsAdapter.submitList(filteredIngredients)
            ingredientsAdapter.notifyDataSetChanged()
            binding.addIngredients.apply {
                visibility = if(filteredIngredients.isEmpty()) View.VISIBLE
                else View.INVISIBLE
                setOnClickListener {
                    binding.addIngredients.visibility = View.INVISIBLE
                    addIngredient(binding.searchEditText.text.toString())
                }
            }
        }

        return AlertDialog.Builder(requireContext())
                .setView(binding.root)
                .setPositiveButton("Save") { _, _ ->
                    if(selectedIngredients.size > 0) {
                        FoodManChuRepository.setIngredientsToAdd(selectedIngredients, selectedIngredientsNames)
                        onIngredientsAddedListener()
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()
    }

    private fun onIngredientClicked(ingredient: Int, name: String) {

        val message = if(selectedIngredients.contains(ingredient)) {
            "Ingredient already added"
        } else {
            "$name has been added"
        }
        selectedIngredients.add(ingredient)
        selectedIngredientsNames.add(name)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun addIngredient(ingredientName: String) {
        binding.searchEditText.setText("")
        AsyncTask.execute {
            val newIngredient = Ingredient(
                    ingredient = ingredientName.capitalize(),
                    isBaseIngredient = false
            )
            database?.foodManChuDao()?.add(newIngredient)
            ingredients.clear()
            ingredients.addAll(database?.foodManChuDao()?.getAllIngredients()!!)
            ingredientsAdapter.submitList(ingredients)
        }
    }

    private fun refreshIngredients() {
        ingredients.clear()
        ingredients.addAll(database?.foodManChuDao()?.getAllIngredients()!!)
        ingredientsAdapter.submitList(ingredients)
    }
}