package com.example.dineout.activities

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dineout.R
import com.example.dineout.data.DataManager
import com.example.dineout.models.Restaurant

class AddEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit)

        // Initialize UI Elements for Wishlist
        val etName = findViewById<EditText>(R.id.etName)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val spinnerCuisine = findViewById<Spinner>(R.id.spinnerCuisine)
        val rgPrice = findViewById<RadioGroup>(R.id.rgPrice)
        val etDish = findViewById<EditText>(R.id.etDish)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Occasion Checkboxes
        val cbFamily = findViewById<CheckBox>(R.id.cbFamily)
        val cbFriends = findViewById<CheckBox>(R.id.cbFriends)
        val cbDate = findViewById<CheckBox>(R.id.cbDate)
        val cbSolo = findViewById<CheckBox>(R.id.cbSolo)

        // Save Button Logic
        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val addr = etAddress.text.toString()
            val cuisine = spinnerCuisine.selectedItem.toString()

            val priceRange = when (rgPrice.checkedRadioButtonId) {
                R.id.rbCheap -> "$"
                R.id.rbMedium -> "$$"
                R.id.rbExpensive -> "$$$"
                else -> "$"
            }

            val dish = etDish.text.toString()

            // Collect Tags/Occasions
            val tagsList = mutableListOf<String>()
            if (cbFamily.isChecked) tagsList.add("Family")
            if (cbFriends.isChecked) tagsList.add("Friends")
            if (cbDate.isChecked) tagsList.add("Date")
            if (cbSolo.isChecked) tagsList.add("Solo")

            if (name.isEmpty() || addr.isEmpty()) {
                Toast.makeText(this, "Please fill name and location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create Restaurant object (Wishlist Phase)
            // Rating, Date, Time, aur Notes ko default/khali chhor diya hai
            val newRestaurant = Restaurant(
                name = name,
                address = addr,
                cuisine = cuisine,
                priceRange = priceRange,
                mustTryDish = dish,
                rating = 0,               // Unvisited hai isliye 0
                status = "wishlist",      // Default status
                visitDate = "",           // Abhi visit nahi kiya
                mealTime = "",            // Abhi visit nahi kiya
                occasions = tagsList.joinToString(", "),
                notes = ""                // Koi review nahi
            )

            // Add to DataManager
            DataManager.addRestaurant(newRestaurant)

            Toast.makeText(this, "Added to Wishlist!", Toast.LENGTH_SHORT).show()

            // Go back to MainActivity
            finish()
        }
    }
}