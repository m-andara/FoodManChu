package com.example.foodmanchu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodmanchu.databinding.ItemRecipeBinding
import com.example.foodmanchu.models.Recipe

class RecipesAdapter(private val onClick: (Recipe) -> Unit): ListAdapter<Recipe, RecipesAdapter.RecipeViewHolder>(diff) {

    companion object {
        val diff = object: DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipeBinding.inflate(inflater, parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.onBind(getItem(position), onClick)
    }

    class RecipeViewHolder(private val binding: ItemRecipeBinding): RecyclerView.ViewHolder(binding.root) {

        fun onBind(recipe: Recipe, onClick: (Recipe) -> Unit) {
            binding.apply {
                title.text = recipe.name
                title.setOnClickListener {
                    onClick(recipe)
                }
            }
        }
    }
}