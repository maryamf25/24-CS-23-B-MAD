package com.example.dineout.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dineout.R
import com.example.dineout.data.DataManager

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        DataManager.loadData(this)

        val resName = intent.getStringExtra("RES_NAME") ?: ""
        val restaurant = DataManager.getRestaurantByName(resName)

        if (restaurant == null) {
            Toast.makeText(this, "Error loading details", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val tvName = findViewById<TextView>(R.id.tvDetailName)
        val tvLocation = findViewById<TextView>(R.id.tvDetailLocation)
        val tvCuisine = findViewById<TextView>(R.id.tvDetailCuisine)
        val tvPrice = findViewById<TextView>(R.id.tvDetailPrice)
        val tvDish = findViewById<TextView>(R.id.tvDetailDish)
        val tvTags = findViewById<TextView>(R.id.tvDetailTags)
        val btnMarkVisited = findViewById<Button>(R.id.btnMarkVisited)

        val layoutReview = findViewById<LinearLayout>(R.id.layoutReview)
        val tvDetailRating = findViewById<TextView>(R.id.tvDetailRating)
        val tvDetailSpend = findViewById<TextView>(R.id.tvDetailSpend)
        val tvDetailWorth = findViewById<TextView>(R.id.tvDetailWorth)
        val tvDetailNotes = findViewById<TextView>(R.id.tvDetailNotes)
        val layoutNotesContainer = findViewById<LinearLayout>(R.id.layoutNotesContainer)

        tvName.text = restaurant.name
        tvLocation.text = "📍 ${restaurant.address}"

        tvLocation.setOnClickListener {
            val address = "${restaurant.name} ${restaurant.address}"
            val gmmIntentUri = android.net.Uri.parse("geo:0,0?q=${android.net.Uri.encode(address)}")
            val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)

            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri))
            }
        }
        tvCuisine.text = restaurant.cuisine
        tvPrice.text = restaurant.priceRange
        tvDish.text = restaurant.mustTryDish
        tvTags.text = restaurant.occasions

        if (restaurant.status == "visited") {
            btnMarkVisited.visibility = View.GONE
            layoutReview.visibility = View.VISIBLE

            tvDetailRating.text = "⭐ ${restaurant.rating}/5"
            tvDetailSpend.text = "Rs. ${restaurant.spendAmount}"
            tvDetailWorth.text = getStarString(restaurant.worthRating)

            if (restaurant.notes.isNotEmpty()) {
                layoutNotesContainer.visibility = View.VISIBLE
                tvDetailNotes.text = restaurant.notes
            } else {
                layoutNotesContainer.visibility = View.GONE
            }
        } else {
            btnMarkVisited.visibility = View.VISIBLE
            layoutReview.visibility = View.GONE
        }

        val btnOptions = findViewById<ImageView>(R.id.btnOptions)
        btnOptions.setOnClickListener { view ->
            val wrapper = android.view.ContextThemeWrapper(this, R.style.CustomPopupMenu)
            val popup = androidx.appcompat.widget.PopupMenu(wrapper, view)
            popup.inflate(R.menu.detail_menu)

            // Force show icons in PopupMenu
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                popup.setForceShowIcon(true)
            } else {
                try {
                    val fieldMPopup = androidx.appcompat.widget.PopupMenu::class.java.getDeclaredField("mPopup")
                    fieldMPopup.isAccessible = true
                    val mPopup = fieldMPopup.get(popup)
                    mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(mPopup, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_share -> {
                        val shareText = "Hey! Check out ${restaurant.name} (${restaurant.cuisine}). We should definitely go there! 🍕🍽️\n📍 Location: ${restaurant.address}"
                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                        startActivity(android.content.Intent.createChooser(shareIntent, "Share with friends"))
                        true
                    }
                    R.id.action_edit -> {
                        val editIntent = android.content.Intent(this, AddEditActivity::class.java)
                        editIntent.putExtra("EDIT_RES_NAME", restaurant.name)
                        startActivity(editIntent)
                        finish()
                        true
                    }
                    R.id.action_delete -> {
                        DataManager.deleteRestaurant(this, restaurant.name)
                        Toast.makeText(this, "Restaurant Deleted!", Toast.LENGTH_SHORT).show()
                        finish()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        btnMarkVisited.setOnClickListener {
            showReviewDialog(restaurant.name)
        }
    }

    private fun showReviewDialog(name: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_review, null)
        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val sbRating = dialogView.findViewById<SeekBar>(R.id.sbDialogRating)
        val tvRatingVal = dialogView.findViewById<TextView>(R.id.tvDialogRatingVal)
        val etSpend = dialogView.findViewById<EditText>(R.id.etDialogSpend)
        val sbWorth = dialogView.findViewById<SeekBar>(R.id.sbDialogWorth)
        val tvWorthVal = dialogView.findViewById<TextView>(R.id.tvDialogWorthVal)
        val etNotes = dialogView.findViewById<EditText>(R.id.etDialogNotes)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmitReview)

        sbRating.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvRatingVal.text = "⭐ $progress/5"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sbWorth.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvWorthVal.text = "⭐ $progress/5"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSubmit.setOnClickListener {
            val spendText = etSpend.text.toString()

            if (spendText.isEmpty()) {
                etSpend.error = "Please enter the amount spent"
                return@setOnClickListener
            }

            val spendValue = spendText.toInt()
            val rating = sbRating.progress
            val notes = etNotes.text.toString()
            val worthValue = sbWorth.progress

            DataManager.markAsVisited(this, name, rating, notes, spendValue, worthValue)

            Toast.makeText(this, "Added to Visited!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun getStarString(rating: Int): String {
        val fullStars = "★".repeat(rating)
        val emptyStars = "☆".repeat(5 - rating)
        return fullStars + emptyStars
    }
}