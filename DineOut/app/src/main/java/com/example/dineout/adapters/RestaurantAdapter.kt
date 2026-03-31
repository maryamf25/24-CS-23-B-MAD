package com.example.dineout.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dineout.R
import com.example.dineout.models.Restaurant

class RestaurantAdapter(
    private var restaurantList: ArrayList<Restaurant>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStatus: TextView = itemView.findViewById(R.id.tvCardStatus)
        val tvCuisine: TextView = itemView.findViewById(R.id.tvCardCuisine)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCardPrice)
        val tvSpendBadge: TextView = itemView.findViewById(R.id.tvCardSpendBadge)
        val tvName: TextView = itemView.findViewById(R.id.tvCardName)
        val tvAddress: TextView = itemView.findViewById(R.id.tvCardAddress)
        val layoutTags: LinearLayout = itemView.findViewById(R.id.layoutTags)
        val tvTag1: TextView = itemView.findViewById(R.id.tvTag1)
        val tvTag2: TextView = itemView.findViewById(R.id.tvTag2)
        val pbRating: ProgressBar = itemView.findViewById(R.id.pbCardRating)
        val tvRating: TextView = itemView.findViewById(R.id.tvCardRating)
        val tvStars: TextView = itemView.findViewById(R.id.tvCardStars)
        val layoutBestDish: LinearLayout = itemView.findViewById(R.id.layoutBestDish)
        val tvBestDishName: TextView = itemView.findViewById(R.id.tvBestDishName)
        val tvBestDishStars: TextView = itemView.findViewById(R.id.tvBestDishStars)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val currentRes = restaurantList[position]

        // Name and Address
        holder.tvName.text = currentRes.name
        holder.tvAddress.text = currentRes.address

        // Cuisine and Price Badges
        holder.tvCuisine.text = currentRes.cuisine
        holder.tvPrice.text = currentRes.priceRange

        // Status Badge (only for visited)
        if (currentRes.status == "visited") {
            holder.tvStatus.visibility = View.VISIBLE
        } else {
            holder.tvStatus.visibility = View.GONE
        }

        // Spend Badge (only for visited)
        if (currentRes.status == "visited" && currentRes.spendAmount > 0) {
            holder.tvSpendBadge.visibility = View.VISIBLE
            holder.tvSpendBadge.text = "Rs. ${currentRes.spendAmount}"
        } else {
            holder.tvSpendBadge.visibility = View.GONE
        }

        // Tags (occasions)
        val occasions = currentRes.occasions.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (occasions.isNotEmpty()) {
            holder.layoutTags.visibility = View.VISIBLE
            holder.tvTag1.text = occasions.getOrNull(0) ?: ""
            holder.tvTag1.visibility = if (occasions.size > 0) View.VISIBLE else View.GONE
            holder.tvTag2.text = occasions.getOrNull(1) ?: ""
            holder.tvTag2.visibility = if (occasions.size > 1) View.VISIBLE else View.GONE
        } else {
            holder.layoutTags.visibility = View.GONE
        }

        // Rating Progress Bar and Stars (only for visited)
        if (currentRes.status == "visited") {
            holder.pbRating.visibility = View.VISIBLE
            holder.tvRating.visibility = View.VISIBLE
            holder.tvStars.visibility = View.VISIBLE
            holder.pbRating.progress = (currentRes.rating * 20) // Convert 5-point to 100-point scale
            holder.tvRating.text = "${currentRes.rating}/5"
            holder.tvStars.text = getStarString(currentRes.rating)
        } else {
            holder.pbRating.visibility = View.GONE
            holder.tvRating.visibility = View.GONE
            holder.tvStars.visibility = View.GONE
        }

        // Best Dish (only for visited)
        if (currentRes.status == "visited") {
            val dishes = currentRes.dishes
            if (dishes.isNotEmpty()) {
                val bestDish = dishes.maxByOrNull { it.rating } ?: dishes[0]
                holder.layoutBestDish.visibility = View.VISIBLE
                holder.tvBestDishName.text = bestDish.name
                holder.tvBestDishStars.text = getStarString(bestDish.rating)
            } else {
                holder.layoutBestDish.visibility = View.GONE
            }
        } else {
            holder.layoutBestDish.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    fun updateData(newList: ArrayList<Restaurant>) {
        restaurantList = newList
        notifyDataSetChanged()
    }

    private fun getStarString(rating: Int): String {
        val fullStars = "★".repeat(rating)
        val emptyStars = "☆".repeat(5 - rating)
        return fullStars + emptyStars
    }
}