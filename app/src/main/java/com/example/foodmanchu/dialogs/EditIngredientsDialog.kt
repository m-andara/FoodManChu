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
import com.example.foodmanchu.adapters.EditIngredientsAdapter
import com.example.foodmanchu.databinding.FragmentEditIngredientsBinding
import com.example.foodmanchu.models.Ingredient
import com.example.foodmanchu.repository.FoodManChuDatabase
import com.example.foodmanchu.repository.FoodManChuRepository

class EditIngredientsDialog: DialogFragment() {

    companion object {
        fun create(onUserIngredientsEditedListener: () -> Unit): EditIngredientsDialog {
            return EditIngredientsDialog().apply {
                this.onUserIngredientsEditedListener = onUserIngredientsEditedListener
            }
        }
    }

    private var onUserIngredientsEditedListener: () -> Unit = {}
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
        GetUserIngredients{
            refreshIngredients(it)
        }.execute()
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
                visibility = if (filteredIngredients.isEmpty()) View.VISIBLE
                else View.INVISIBLE
                setOnClickListener {
                    addIngredient(binding.searchEditText.text.toString())
                    binding.searchEditText.setText("")
                    binding.addIngredients.visibility = View.INVISIBLE
                }
            }
        }

        return AlertDialog.Builder(requireContext())
                .setView(binding.root)
                .setPositiveButton("Done", null)
                .create()
    }

    private fun onIngredientClicked(ingredient: Int, name: String) {

        AsyncTask.execute {
            database?.foodManChuDao()?.deleteIngredientFromRecipes(ingredient)
            val selectedIngredient = database?.foodManChuDao()?.getIngredientById(ingredient)
            if (selectedIngredient != null) {
                database?.foodManChuDao()?.delete(selectedIngredient)
            }
            GetUserIngredients{
                refreshIngredients(it)
                onUserIngredientsEditedListener()
            }.execute()
        }
    }

    private fun addIngredient(ingredientName: String) {
        AddIngredient(ingredientName){result ->
            if(result) {
                GetUserIngredients{
                    refreshIngredients(it)
                }.execute()
            } else {
                Toast.makeText(requireContext(), "Ingredient already exist as user ingredient or base ingredient. Base ingredients cannot be deleted", Toast.LENGTH_LONG).show()
            }
        }.execute()
    }

    private fun refreshIngredients(userIngredients: List<Ingredient>) {
        ingredients.clear()
        ingredients.addAll(userIngredients)
        ingredientsAdapter.submitList(ingredients)
        ingredientsAdapter.notifyDataSetChanged()
    }
}

class GetUserIngredients(private val onFinished: (List<Ingredient>) -> Unit): AsyncTask<Void, Void, List<Ingredient>>() {

    private var database: FoodManChuDatabase = FoodManChuRepository.getDb()
    private var ingredients = listOf<Ingredient>()

    override fun doInBackground(vararg params: Void?): List<Ingredient>? {
        ingredients = database?.foodManChuDao()?.getAllUserIngredients()
        return ingredients
    }

    override fun onPostExecute(result: List<Ingredient>?) {
        super.onPostExecute(result)
        if (result != null) {
            onFinished(result)
        }
    }
}

class AddIngredient(private val ingredientName: String, private val onFinished: (Boolean) -> Unit): AsyncTask<Void, Void, Boolean>() {

    private var database: FoodManChuDatabase = FoodManChuRepository.getDb()

    override fun doInBackground(vararg params: Void?): Boolean? {
        val ingredient = database.foodManChuDao().getIngredientByName(ingredientName)
        return if(ingredient == null) {
            val newIngredient = Ingredient(
                    ingredient = ingredientName,
                    isBaseIngredient = false
            )
            database?.foodManChuDao()?.add(newIngredient)
            true
        } else {
            false
        }
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (result != null) {
            onFinished(result)
        }
    }
}