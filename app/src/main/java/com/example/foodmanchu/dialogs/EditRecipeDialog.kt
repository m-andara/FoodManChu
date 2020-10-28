package com.example.foodmanchu.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import com.example.foodmanchu.GetRecipeIngredients
import com.example.foodmanchu.R
import com.example.foodmanchu.databinding.FragmentEditRecipeBinding
import com.example.foodmanchu.models.Category
import com.example.foodmanchu.models.Ingredient
import com.example.foodmanchu.models.Recipe
import com.example.foodmanchu.repository.FoodManChuDatabase
import com.example.foodmanchu.repository.FoodManChuRepository

class EditRecipeDialog: DialogFragment() {

    companion object {
        fun create(onEditedListener: () -> Unit): EditRecipeDialog {
            return EditRecipeDialog().apply {
                this.onEditedListener = onEditedListener
            }
        }
    }

    private lateinit var binding: FragmentEditRecipeBinding
    private var onEditedListener: () -> Unit = {}
    private var database: FoodManChuDatabase = FoodManChuRepository.getDb()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(requireContext())
        binding = FragmentEditRecipeBinding.inflate(inflater)

        val categories = Category.values().map { it.description }
        val adapter = ArrayAdapter(requireContext(), R.layout.category_listing, categories)
        (binding.categoryInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.apply {
            editRecipeIngredients.setOnClickListener {
                EditRecipeIngredientsDialog.create{
                    refreshRecipeIngredients()
                }.show(requireFragmentManager(), "Edit Recipe Ingredients Dialog")
            }
            editIngredientsList.setOnClickListener {
                EditIngredientsDialog.create{
                    refreshRecipeIngredients()
                }.show(requireFragmentManager(), "Edit Ingredients Dialog")
            }
        }

        initializeUi()
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Done") { _, _ ->
                val recipe = Recipe(
                    recipeId = FoodManChuRepository.getCurrentRecipe().recipeId,
                    name = binding.nameEditText.text?.toString() ?: "",
                    description = binding.descriptionEditText.text?.toString() ?: "",
                    instructions = binding.instructionsEditText.text?.toString() ?: "",
                    category = binding.categoryInputLayout.editText?.text?.toString() ?: "",
                    prepTime = binding.prepTimeInputLayout.editText?.text?.toString()?.toInt() ?: 0
                )
                AsyncTask.execute {
                    database.foodManChuDao().update(recipe)
                }
            }.create()
    }

    private fun initializeUi() {
        GetRecipeIngredients() { ingredients ->
            binding.ingredients.text =
                ingredients.joinToString(", ") { ingredient -> ingredient.ingredient }
        }.execute()
        val recipe = FoodManChuRepository.getCurrentRecipe()
        binding.apply {
            nameEditText.setText(recipe.name)
            descriptionEditText.setText(recipe.description)
            instructionsEditText.setText(recipe.instructions)
            prepTimeEditText.setText(recipe.prepTime.toString())
        }
    }

    private fun refreshRecipeIngredients() {
        GetRecipeIngredients() { ingredients ->
            binding.ingredients.text =
                ingredients.joinToString(", ") { ingredient -> ingredient.ingredient }
        }.execute()
    }
}