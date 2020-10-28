package com.example.foodmanchu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodmanchu.databinding.ItemEditIngredientBinding
import com.example.foodmanchu.models.Ingredient

class EditIngredientsAdapter(private val onClick: (Int, String) -> Unit): ListAdapter<Ingredient, EditIngredientsAdapter.EditIngredientsViewHolder>(diff) {
    companion object {
        val diff = object: DiffUtil.ItemCallback<Ingredient>() {
            override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditIngredientsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEditIngredientBinding.inflate(inflater, parent, false)
        return EditIngredientsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EditIngredientsViewHolder, position: Int) {
        holder.onBind(getItem(position), position, onClick)
    }

    class EditIngredientsViewHolder(private val binding: ItemEditIngredientBinding): RecyclerView.ViewHolder(binding.root) {

        fun onBind(ingredient: Ingredient, position: Int, onClick: (Int, String) -> Unit) {
            binding.apply {
                ingredientTitle.apply {
                    text = ingredient.ingredient
                }
                editIngredientDeleteButton.apply {
                    setOnClickListener {
                        onClick(ingredient.ingredientId, ingredient.ingredient)
                    }
                }
            }
        }
    }
}