package com.example.dineout.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dineout.R
import com.example.dineout.adapters.RestaurantAdapter
import com.example.dineout.data.DataManager
import com.example.dineout.data.RestaurantFilter
import com.example.dineout.models.Restaurant
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // Filter state variables
    private var filterCuisine: String = ""
    private var filterMustTry: String = ""
    private var filterPrice: String = ""

    // Launcher for FilterActivity
    private val filterLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            filterCuisine = data?.getStringExtra(FilterActivity.EXTRA_CUISINE).orEmpty()
            filterMustTry = data?.getStringExtra(FilterActivity.EXTRA_BEST_FOR).orEmpty()
            filterPrice = data?.getStringExtra(FilterActivity.EXTRA_PRICE).orEmpty()
            loadFilteredData()
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var layoutBudgetDashboard: View
    private lateinit var tvTotalSpend: TextView
    private lateinit var tvSpendStats: TextView
    private lateinit var tvNoResults: TextView // FIX 1: Declared the missing variable
    private lateinit var fabAdd: ExtendedFloatingActionButton
    private lateinit var btnDecideForMe: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Data
        DataManager.loadData(this)

        // View Binding
        recyclerView = findViewById(R.id.recyclerView)
        tabLayout = findViewById(R.id.tabLayout)
        layoutBudgetDashboard = findViewById(R.id.layoutBudgetDashboard)
        tvTotalSpend = findViewById(R.id.tvTotalSpend)
        tvSpendStats = findViewById(R.id.tvSpendStats)
        tvNoResults = findViewById(R.id.tvNoResults) // FIX 2: Linked to XML ID
        fabAdd = findViewById(R.id.fabAdd)
        btnDecideForMe = findViewById(R.id.btnDecideForMe)

        // Filter Button Setup
        findViewById<ImageButton>(R.id.btnOpenFilters).setOnClickListener {
            val i = Intent(this, FilterActivity::class.java).apply {
                putExtra(FilterActivity.EXTRA_CUISINE, filterCuisine)
                putExtra(FilterActivity.EXTRA_BEST_FOR, filterMustTry)
                putExtra(FilterActivity.EXTRA_PRICE, filterPrice)
            }
            filterLauncher.launch(i)
        }

        // RecyclerView Setup
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RestaurantAdapter(ArrayList()) { position ->
            val list = displayedListForCurrentTab()
            if (position < list.size) {
                val clickedRestaurant = list[position]
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("RES_NAME", clickedRestaurant.name)
                startActivity(intent)
            }
        }
        recyclerView.adapter = adapter

        setupTouchHelper()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) { loadFilteredData() }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }

        btnDecideForMe.setOnClickListener { showDecideForMeDialog() }

        loadFilteredData()
    }

    private fun setupTouchHelper() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.bindingAdapterPosition
                val currentList = displayedListForCurrentTab()
                if (pos != RecyclerView.NO_POSITION && pos < currentList.size) {
                    val name = currentList[pos].name
                    DataManager.deleteRestaurant(this@MainActivity, name)
                    Toast.makeText(this@MainActivity, "$name Deleted!", Toast.LENGTH_SHORT).show()
                    loadFilteredData()
                }
            }
        }).attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        // FIX 3: Force DataManager to reload the file to catch new/edited entries
        DataManager.loadData(this)
        loadFilteredData()
    }

    private fun displayedListForCurrentTab(): ArrayList<Restaurant> {
        val currentStatus = if (tabLayout.selectedTabPosition == 0) "wishlist" else "visited"
        val base = DataManager.getByStatus(currentStatus)

        if (filterCuisine.isEmpty() && filterMustTry.isEmpty() && filterPrice.isEmpty()) {
            return base
        }

        return RestaurantFilter.filterResults(base, filterCuisine, filterMustTry, filterPrice)
    }

    private fun loadFilteredData() {
        val currentList = displayedListForCurrentTab()
        val currentStatus = if (tabLayout.selectedTabPosition == 0) "wishlist" else "visited"

        // Logic for Empty State Message
        if (currentList.isEmpty()) {
            tvNoResults.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            tvNoResults.text = if (currentStatus == "wishlist")
                "Nothing matches in your Wishlist! ✨"
            else
                "No visited spots match these filters! 📍"
        } else {
            tvNoResults.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.updateData(currentList)
        }

        // Handle Dashboard Visibility
        if (currentStatus == "visited") {
            layoutBudgetDashboard.visibility = View.VISIBLE
            fabAdd.visibility = View.GONE
            btnDecideForMe.visibility = View.GONE

            val totalSpend = currentList.sumOf { it.spendAmount.toLong() }
            tvTotalSpend.text = "Rs. $totalSpend"
            tvSpendStats.text = "${currentList.size} outings"
        } else {
            layoutBudgetDashboard.visibility = View.GONE
            fabAdd.visibility = View.VISIBLE
            btnDecideForMe.visibility = View.VISIBLE
        }
    }

    private fun showDecideForMeDialog() {
        val wishlist = RestaurantFilter.filterResults(
            DataManager.getByStatus("wishlist"),
            filterCuisine,
            filterMustTry,
            filterPrice
        )

        if (wishlist.isEmpty()) {
            Toast.makeText(this, "Wishlist is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val pick = wishlist[Random.nextInt(wishlist.size)]

        AlertDialog.Builder(this)
            .setTitle("DineOut")
            .setMessage("Today you're going to ${pick.name}!")
            .setPositiveButton("Let's Go!") { dialog, _ ->
                dialog.dismiss()
                startActivity(
                    Intent(this, DetailActivity::class.java).putExtra("RES_NAME", pick.name)
                )
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}