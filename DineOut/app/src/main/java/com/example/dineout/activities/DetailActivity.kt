package com.example.dineout.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.dineout.R
import com.example.dineout.adapters.PhotoCarouselAdapter
import com.example.dineout.data.DataManager

class DetailActivity : AppCompatActivity() {

    private lateinit var resName: String
    private lateinit var tvName: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvCuisine: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvDish: TextView
    private lateinit var tvTags: TextView
    private lateinit var btnMarkVisited: Button
    private lateinit var layoutReview: LinearLayout
    private lateinit var tvDetailRating: TextView
    private lateinit var tvDetailSpend: TextView
    private lateinit var tvDetailWorth: TextView
    private lateinit var tvDetailNotes: TextView
    private lateinit var layoutNotesContainer: LinearLayout
    private lateinit var rvPhotos: RecyclerView
    private lateinit var photoCarouselAdapter: PhotoCarouselAdapter

    companion object {
        private const val PHOTO_SEPARATOR = "||"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        
        // Set up back button
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarDetail)
        toolbar.setNavigationOnClickListener { finish() }
        
        resName = intent.getStringExtra("RES_NAME") ?: ""

        tvName = findViewById(R.id.tvDetailName)
        tvLocation = findViewById(R.id.tvDetailLocation)
        tvCuisine = findViewById(R.id.tvDetailCuisine)
        tvPrice = findViewById(R.id.tvDetailPrice)
        tvDish = findViewById(R.id.tvDetailDish)
        tvTags = findViewById(R.id.tvDetailTags)
        btnMarkVisited = findViewById(R.id.btnMarkVisited)
        layoutReview = findViewById(R.id.layoutReview)
        tvDetailRating = findViewById(R.id.tvDetailRating)
        tvDetailSpend = findViewById(R.id.tvDetailSpend)
        tvDetailWorth = findViewById(R.id.tvDetailWorth)
        tvDetailNotes = findViewById(R.id.tvDetailNotes)
        layoutNotesContainer = findViewById(R.id.layoutNotesContainer)
        rvPhotos = findViewById(R.id.rvDetailPhotos)
        photoCarouselAdapter = PhotoCarouselAdapter()
        rvPhotos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvPhotos.adapter = photoCarouselAdapter
        rvPhotos.isNestedScrollingEnabled = false
        PagerSnapHelper().attachToRecyclerView(rvPhotos)

        tvLocation.setOnClickListener {
            val currentRestaurant = DataManager.getRestaurantByName(resName)
            val address = if (currentRestaurant != null) {
                "${currentRestaurant.name} ${currentRestaurant.address}"
            } else {
                tvLocation.text.toString()
            }
            val gmmIntentUri = android.net.Uri.parse("geo:0,0?q=${android.net.Uri.encode(address)}")
            val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)

            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri))
            }
        }
        renderRestaurant()

        val btnOptions = findViewById<ImageView>(R.id.btnOptions)
        btnOptions.setOnClickListener { view ->
            val wrapper = android.view.ContextThemeWrapper(this, R.style.CustomPopupMenuTheme)
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
                val current = DataManager.getRestaurantByName(resName)
                    ?: return@setOnMenuItemClickListener false
                when (item.itemId) {
                    R.id.action_share -> {
                        val shareText = "Hey! Check out ${current.name} (${current.cuisine}). We should definitely go there!\nLocation: ${current.address}"
                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                        startActivity(android.content.Intent.createChooser(shareIntent, "Share with friends"))
                        true
                    }
                    R.id.action_edit -> {
                        val editIntent = android.content.Intent(this, AddEditActivity::class.java)
                        editIntent.putExtra("EDIT_RES_NAME", current.name)
                        startActivity(editIntent)
                        finish()
                        true
                    }
                    R.id.action_delete -> {
                        DataManager.deleteRestaurant(this, current.name)
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
            val i = android.content.Intent(this, MarkVisitedActivity::class.java)
            i.putExtra("RES_NAME", resName)
            startActivity(i)
        }
    }

    override fun onResume() {
        super.onResume()
        renderRestaurant()
    }

    private fun renderRestaurant() {
        DataManager.loadData(this)
        val restaurant = DataManager.getRestaurantByName(resName)

        if (restaurant == null) {
            Toast.makeText(this, "Error loading details", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvName.text = restaurant.name
        tvLocation.text = "📍 ${restaurant.address}"
        tvCuisine.text = restaurant.cuisine
        tvPrice.text = restaurant.priceRange
        tvDish.text = restaurant.mustTryDish
        tvTags.text = restaurant.occasions

        val photos = parsePhotoPaths(restaurant.photoPath).filter { java.io.File(it).exists() }
        if (photos.isNotEmpty()) {
            rvPhotos.visibility = View.VISIBLE
            photoCarouselAdapter.submit(photos)
        } else {
            rvPhotos.visibility = View.GONE
            photoCarouselAdapter.submit(emptyList())
        }

        if (restaurant.status == "visited") {
            btnMarkVisited.visibility = View.GONE
            layoutReview.visibility = View.VISIBLE
            tvDetailRating.text = "${restaurant.rating}/5"
            tvDetailSpend.text = "Rs. ${restaurant.spendAmount}"
            tvDetailWorth.text = if (restaurant.worthRating >= 3) "Yes" else "No"

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
    }

    private fun parsePhotoPaths(raw: String): List<String> {
        if (raw.isBlank()) return emptyList()
        return raw.split(PHOTO_SEPARATOR).map { it.trim() }.filter { it.isNotEmpty() }
    }

}