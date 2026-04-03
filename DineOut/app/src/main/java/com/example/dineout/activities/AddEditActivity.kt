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
        val tvToolbarTitle = findViewById<TextView>(R.id.tvToolbarTitle)
        val etName = findViewById<EditText>(R.id.etName)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val spinnerCuisine = findViewById<Spinner>(R.id.spinnerCuisine)
        val rgPrice = findViewById<RadioGroup>(R.id.rgPrice)
        val rbCheap = findViewById<RadioButton>(R.id.rbCheap)
        val rbMedium = findViewById<RadioButton>(R.id.rbMedium)
        val rbExpensive = findViewById<RadioButton>(R.id.rbExpensive)
        val etDish = findViewById<EditText>(R.id.etDish)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Review Details UI
        val reviewedSection = findViewById<LinearLayout>(R.id.reviewedSection)
        val tvRatingLabel = findViewById<TextView>(R.id.tvRatingLabel)
        val sbEditRating = findViewById<SeekBar>(R.id.sbEditRating)
        val etEditSpend = findViewById<EditText>(R.id.etEditSpend)
        val tvWorthLabel = findViewById<TextView>(R.id.tvWorthLabel)
        val sbEditWorth = findViewById<SeekBar>(R.id.sbEditWorth)
        val etEditNotes = findViewById<EditText>(R.id.etEditNotes)

        // Occasion Checkboxes
        val cbFamily = findViewById<CheckBox>(R.id.cbFamily)
        val cbFriends = findViewById<CheckBox>(R.id.cbFriends)
        val cbDate = findViewById<CheckBox>(R.id.cbDate)
        val cbSolo = findViewById<CheckBox>(R.id.cbSolo)

        val editRestaurantName = intent.getStringExtra("EDIT_RES_NAME")
        val existingRestaurant = editRestaurantName?.let { DataManager.getRestaurantByName(it) }

        if (existingRestaurant != null) {
            tvToolbarTitle.text = "Edit Restaurant"
            btnSave.text = "Update Details"
            etName.setText(existingRestaurant.name)
            etAddress.setText(existingRestaurant.address)
            
            val cuisines = resources.getStringArray(R.array.cuisine_options)
            val index = cuisines.indexOf(existingRestaurant.cuisine)
            if (index >= 0) spinnerCuisine.setSelection(index)

            when (existingRestaurant.priceRange) {
                "$" -> rbCheap.isChecked = true
                "$$" -> rbMedium.isChecked = true
                "$$$" -> rbExpensive.isChecked = true
            }

            etDish.setText(existingRestaurant.mustTryDish)

            val tags = existingRestaurant.occasions.split(", ")
            cbFamily.isChecked = tags.contains("Family")
            cbFriends.isChecked = tags.contains("Friends")
            cbDate.isChecked = tags.contains("Date Night") || tags.contains("Date")
            cbSolo.isChecked = tags.contains("Solo")

            if (existingRestaurant.status == "visited") {
                reviewedSection.visibility = android.view.View.VISIBLE
                
                sbEditRating.progress = existingRestaurant.rating
                tvRatingLabel.text = "${existingRestaurant.rating}/5"
                
                etEditSpend.setText(existingRestaurant.spendAmount.toString())
                
                sbEditWorth.progress = existingRestaurant.worthRating
                tvWorthLabel.text = "${existingRestaurant.worthRating}/5"
                
                etEditNotes.setText(existingRestaurant.notes)
            }
        }

        sbEditRating.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvRatingLabel.text = "$progress/5"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sbEditWorth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvWorthLabel.text = "$progress/5"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

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
            if (cbDate.isChecked) tagsList.add("Date Night")
            if (cbSolo.isChecked) tagsList.add("Solo")

            if (name.isEmpty() || addr.isEmpty()) {
                Toast.makeText(this, "Please fill name and location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val finalRating = if (reviewedSection.visibility == android.view.View.VISIBLE) sbEditRating.progress else (existingRestaurant?.rating ?: 0)
            val finalSpendText = etEditSpend.text.toString()
            val finalSpend = if (reviewedSection.visibility == android.view.View.VISIBLE && finalSpendText.isNotEmpty()) finalSpendText.toInt() else (existingRestaurant?.spendAmount ?: 0)
            val finalWorth = if (reviewedSection.visibility == android.view.View.VISIBLE) sbEditWorth.progress else (existingRestaurant?.worthRating ?: 0)
            val finalNotes = if (reviewedSection.visibility == android.view.View.VISIBLE) etEditNotes.text.toString() else (existingRestaurant?.notes ?: "")

            val newRestaurant = Restaurant(
                name = name,
                address = addr,
                cuisine = cuisine,
                priceRange = priceRange,
                mustTryDish = dish,
                rating = finalRating,
                status = existingRestaurant?.status ?: "wishlist",
                visitDate = existingRestaurant?.visitDate ?: "",
                mealTime = existingRestaurant?.mealTime ?: "",
                occasions = tagsList.joinToString(", "),
                notes = finalNotes,
                spendAmount = finalSpend,
                worthRating = finalWorth
            )

            if (existingRestaurant != null) {
                DataManager.updateRestaurant(existingRestaurant.name, newRestaurant)
                Toast.makeText(this, "Restaurant Updated!", Toast.LENGTH_SHORT).show()
            } else {
                DataManager.addRestaurant(newRestaurant)
                Toast.makeText(this, "Added to Wishlist!", Toast.LENGTH_SHORT).show()
            }

            // Go back
            finish()
        }
    }
}