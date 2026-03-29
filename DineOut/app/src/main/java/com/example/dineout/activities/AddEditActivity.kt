package com.example.dineout.activities

import android.os.Bundle
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dineout.R
import com.example.dineout.data.DataManager
import com.example.dineout.models.Restaurant
import java.util.*

class AddEditActivity : AppCompatActivity() {
    private var selectedDate = ""
    private var selectedTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit)

        // Initialize UI Elements
        val etName = findViewById<EditText>(R.id.etName)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val spinnerCuisine = findViewById<Spinner>(R.id.spinnerCuisine)
        val rgPrice = findViewById<RadioGroup>(R.id.rgPrice)
        val seekBarRating = findViewById<SeekBar>(R.id.seekBarRating)
        val tvRatingLabel = findViewById<TextView>(R.id.tvRatingLabel)
        val btnDate = findViewById<Button>(R.id.btnDate)
        val btnTime = findViewById<Button>(R.id.btnTime)
        val etDish = findViewById<EditText>(R.id.etDish)
        val etNotes = findViewById<EditText>(R.id.etNotes)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Occasion Checkboxes
        val cbFamily = findViewById<CheckBox>(R.id.cbFamily)
        val cbFriends = findViewById<CheckBox>(R.id.cbFriends)
        val cbDate = findViewById<CheckBox>(R.id.cbDate)
        val cbSolo = findViewById<CheckBox>(R.id.cbSolo)

        // Rating SeekBar Link
        seekBarRating.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvRatingLabel.text = "Expected Rating: $progress/10"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Date Picker
        btnDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                selectedDate = "$day/${month + 1}/$year"
                btnDate.text = selectedDate
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Time Picker
        btnTime.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                selectedTime = String.format("%02d:%02d", hour, minute)
                btnTime.text = selectedTime
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show()
        }

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

            val rating = seekBarRating.progress
            val dish = etDish.text.toString()
            val notes = etNotes.text.toString()

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

            // Create Restaurant object (Using Member A's spelling)
            val newRestaurant = Restaurant(
                name = name,
                address = addr,
                cuisine = cuisine,
                priceRange = priceRange,
                mustTryDish = dish,
                rating = rating,
                status = "wishlist", // Default to wishlist
                visitDate = selectedDate,
                mealTime = selectedTime,
                occasions = tagsList.joinToString(", "),
                notes = notes
            )


            // Add to DataManager
            DataManager.addRestaurant(newRestaurant)

            Toast.makeText(this, "Restaurant Saved!", Toast.LENGTH_SHORT).show()
            
            // Go back to MainActivity
            finish()
        }
    }
}