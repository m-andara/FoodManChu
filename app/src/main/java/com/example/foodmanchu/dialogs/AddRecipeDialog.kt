package com.example.foodmanchu.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import com.example.foodmanchu.R
import com.example.foodmanchu.databinding.FragmentAddRecipeBinding
import com.example.foodmanchu.models.Category
import com.example.foodmanchu.models.Recipe
import com.example.foodmanchu.repository.FoodManChuRepository

class AddRecipeDialog: DialogFragment() {

    companion object {
        fun create(onRecipeAddedListener: (Recipe, List<Int>) -> Unit): AddRecipeDialog {
            return AddRecipeDialog().apply {
                this.onRecipeAddedListener = onRecipeAddedListener
            }
        }
    }

    private var onRecipeAddedListener: (Recipe, List<Int>) -> Unit = { _, _ ->}
    private lateinit var binding: FragmentAddRecipeBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(requireContext())
        binding = FragmentAddRecipeBinding.inflate(inflater)

        val categories = Category.values().map { it.description }
        val adapter = ArrayAdapter(requireContext(), R.layout.category_listing, categories)
        (binding.categoryInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.addIngredients.setOnClickListener {
            onAddIngredientsClicked()
        }
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Add") { _, _ ->
                if(binding.nameEditText.text.toString().isNotEmpty() &&
                        binding.nameEditText.text.toString().isNotBlank()) {
                    val recipe = Recipe(
                        name = binding.nameEditText.text?.toString() ?: "",
                        description = binding.descriptionEditText.text?.toString() ?: "",
                        instructions = binding.instructionsEditText.text?.toString() ?: "",
                        category = binding.categoryInputLayout.editText?.text?.toString() ?: "",
                        prepTime = binding.prepTimeInputLayout.editText?.text?.toString()?.toInt() ?: 0
                    )
                    onRecipeAddedListener(recipe, FoodManChuRepository.getIngredientsToAdd().toList())
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun onAddIngredientsListener() {
        binding.ingredients.text = getString(R.string.add_ingredients,
                FoodManChuRepository
                        .getIngredientsToAddNames().joinToString(", "))
    }

    private fun onAddIngredientsClicked() {
        AddIngredientDialog.create() {
            onAddIngredientsListener()
        }.show(requireFragmentManager(), "Add Recipe Dialog")
    }
}