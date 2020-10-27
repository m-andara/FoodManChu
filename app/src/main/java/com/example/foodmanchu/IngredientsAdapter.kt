package com.example.foodmanchu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodmanchu.databinding.ItemIngredientBinding
import com.example.foodmanchu.models.Ingredient

class IngredientsAdapter(private val onClick: (Int, String) -> Unit): ListAdapter<Ingredient, IngredientsAdapter.IngredientsViewHolder>(diff) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemIngredientBinding.inflate(inflater, parent, false)
        return IngredientsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        holder.onBind(getItem(position), position, onClick)
    }

    class IngredientsViewHolder(private val binding: ItemIngredientBinding): RecyclerView.ViewHolder(binding.root) {

        fun onBind(ingredient: Ingredient, position: Int, onClick: (Int, String) -> Unit) {
            binding.apply {
                ingredientTitle.apply {
                    text = ingredient.ingredient
                    setOnClickListener {
                        onClick(ingredient.ingredientId, ingredient.ingredient)
                    }
                }
            }
        }
    }
}