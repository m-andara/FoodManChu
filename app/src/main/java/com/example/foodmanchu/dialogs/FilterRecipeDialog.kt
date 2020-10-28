package com.example.foodmanchu.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import com.example.foodmanchu.R
import com.example.foodmanchu.databinding.FragmentFilterRecipesBinding

class FilterRecipeDialog: DialogFragment() {

    companion object {
        fun create(onFilterAddedListener: (String, String) -> Unit): FilterRecipeDialog {
            return FilterRecipeDialog().apply {
                this.onFilterAddedListener = onFilterAddedListener
            }
        }
    }

    private lateinit var binding: FragmentFilterRecipesBinding
    private var onFilterAddedListener:(String, String) -> Unit = { _, _ ->}
    private var type = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(requireContext())
        binding = FragmentFilterRecipesBinding.inflate(inflater)
        binding.filterAllRecipes.isChecked = true
        binding.filterSearchInputLayout.visibility = View.INVISIBLE
        binding.apply {
            filterOptions.setOnCheckedChangeListener { group, checkedId ->
                binding.filterSearchInputLayout.visibility = View.VISIBLE
                when (checkedId) {
                    R.id.filter_by_keywords -> {
                        binding.filterSearchEditText.inputType = InputType.TYPE_CLASS_TEXT
                        type = "keywords"
                    }
                    R.id.filter_by_ingredients -> {
                        binding.filterSearchEditText.inputType = InputType.TYPE_CLASS_TEXT
                        type = "ingredients"
                    }
                    R.id.filter_by_time -> {
                        binding.filterSearchEditText.inputType = InputType.TYPE_CLASS_NUMBER
                        type = "time"
                    }
                    else -> {
                        binding.filterSearchInputLayout.visibility = View.INVISIBLE
                        type = "all"
                    }
                }
            }
        }
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Apply Filters") { _, _ ->
                onFilterAddedListener(type, binding.filterSearchEditText.text.toString().toLowerCase())
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}