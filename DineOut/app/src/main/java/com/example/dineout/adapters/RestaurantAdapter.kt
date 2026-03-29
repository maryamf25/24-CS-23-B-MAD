package com.example.dineout.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dineout.R
import com.example.dineout.models.Restaurant

class RestaurantAdapter(
    private var restaurantList: ArrayList<Restaurant>,
    private val onItemClick: (Int) -> Unit // Handle clicks on items
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    // 1. Finding UI elements in the layout
    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCardName)
        val tvAddress: TextView = itemView.findViewById(R.id.tvCardAddress)
        val tvCuisine: TextView = itemView.findViewById(R.id.tvCardCuisine)
        val tvMustTry: TextView = itemView.findViewById(R.id.tvCardMustTry)
        val tvRating: TextView = itemView.findViewById(R.id.tvCardRating)
    }

    // 2. Loading the CardView design/layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    // 3. Setting data into the CardView items
    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val currentRes = restaurantList[position]

        val context = holder.itemView.context
        holder.tvName.text = currentRes.name
        holder.tvAddress.text = currentRes.address
        holder.tvCuisine.text = context.getString(R.string.cuisine_price_format, currentRes.cuisine, currentRes.priceRange)
        holder.tvMustTry.text = context.getString(R.string.must_try_format, currentRes.mustTryDish)
        holder.tvRating.text = context.getString(R.string.rating_format, currentRes.rating)

        // Action when the user taps on a card
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    // 4. Returning the number of items in the list
    override fun getItemCount(): Int {
        return restaurantList.size
    }

    // 5. Function to refresh the list when switching tabs
    fun updateData(newList: ArrayList<Restaurant>) {
        restaurantList = newList
        notifyDataSetChanged()
    }
}