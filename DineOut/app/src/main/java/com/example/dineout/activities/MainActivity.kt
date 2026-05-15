package com.example.dineout.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dineout.R
import com.example.dineout.auth.AuthManager
import com.example.dineout.adapters.RestaurantAdapter
import com.example.dineout.data.DataManager
import com.example.dineout.data.RestaurantFilter
import com.example.dineout.models.Restaurant
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder // Correct Dialog Import
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // Filter state
    private var filterCuisine: String = ""
    private var filterMustTry: String = ""
    private var filterPrice: String = ""

    private val filterLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            filterCuisine = data?.getStringExtra(FilterActivity.EXTRA_CUISINE).orEmpty()
            filterMustTry = data?.getStringExtra(FilterActivity.EXTRA_BEST_FOR).orEmpty()
            filterPrice = data?.getStringExtra(FilterActivity.EXTRA_PRICE).orEmpty()
            loadFilteredData()
        }
    }

    // View Bindings
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var layoutBudgetDashboard: View
    private lateinit var tvTotalSpend: TextView
    private lateinit var tvSpendStats: TextView
    private lateinit var homeContent: View
    private lateinit var profileContent: View
    private lateinit var dashboardContent: View
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var tvProfileInitials: TextView
    private lateinit var tvProfileWishlistCount: TextView
    private lateinit var tvProfileVisitedCount: TextView
    private lateinit var tvNoResults: TextView // Fixed missing declaration
    private lateinit var fabAdd: ExtendedFloatingActionButton
    private lateinit var btnDecideForMe: MaterialButton
    private lateinit var btnProfileAdd: MaterialButton
    private var isProfileScreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!AuthManager.isLoggedIn(this)) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        DataManager.loadData(this)

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerView)
        tabLayout = findViewById(R.id.tabLayout)
        layoutBudgetDashboard = findViewById(R.id.layoutBudgetDashboard)
        tvTotalSpend = findViewById(R.id.tvTotalSpend)
        tvSpendStats = findViewById(R.id.tvSpendStats)
        homeContent = findViewById(R.id.homeContent)
        profileContent = findViewById(R.id.profileContent)
        dashboardContent = findViewById(R.id.dashboardContent)
        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.barChart)
        bottomNav = findViewById(R.id.bottomNav)
        tvProfileInitials = findViewById(R.id.tvProfileInitials)
        tvProfileName = findViewById(R.id.tvProfileName)
        tvProfileEmail = findViewById(R.id.tvProfileEmail)
        tvProfileWishlistCount = findViewById(R.id.tvProfileWishlistCount)
        tvProfileVisitedCount = findViewById(R.id.tvProfileVisitedCount)
        tvNoResults = findViewById(R.id.tvNoResults)
        fabAdd = findViewById(R.id.fabAdd)
        btnDecideForMe = findViewById(R.id.btnDecideForMe)
        btnProfileAdd = findViewById(R.id.btnProfileAdd)
        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            AuthManager.logout(this)
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
        btnProfileAdd.setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btnOpenFilters).setOnClickListener {
            val i = Intent(this, FilterActivity::class.java).apply {
                putExtra(FilterActivity.EXTRA_CUISINE, filterCuisine)
                putExtra(FilterActivity.EXTRA_BEST_FOR, filterMustTry)
                putExtra(FilterActivity.EXTRA_PRICE, filterPrice)
            }
            filterLauncher.launch(i)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RestaurantAdapter(ArrayList()) { position ->
            val list = displayedListForCurrentTab()
            if (position < list.size) {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("RES_NAME", list[position].name)
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

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> showHome()
                R.id.nav_dashboard -> showDashboard()
                R.id.nav_profile -> showProfile()
            }
            true
        }
        bottomNav.selectedItemId = R.id.nav_home

        loadFilteredData()
    }

    private fun setupTouchHelper() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
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
        if (!AuthManager.isLoggedIn(this)) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
            return
        }
        DataManager.loadData(this)
        if (isProfileScreen) {
            showProfile()
        } else {
            showHome()
        }
    }

    private fun showHome() {
        isProfileScreen = false
        tabLayout.visibility = View.VISIBLE
        homeContent.visibility = View.VISIBLE
        profileContent.visibility = View.GONE
        dashboardContent.visibility = View.GONE
        loadFilteredData()
    }

    private fun showDashboard() {
        isProfileScreen = false
        tabLayout.visibility = View.GONE
        homeContent.visibility = View.GONE
        profileContent.visibility = View.GONE
        dashboardContent.visibility = View.VISIBLE
        setupCharts()
    }

    private fun showProfile() {
        isProfileScreen = true
        tabLayout.visibility = View.GONE
        homeContent.visibility = View.GONE
        profileContent.visibility = View.VISIBLE
        dashboardContent.visibility = View.GONE
        tvNoResults.visibility = View.GONE
        fabAdd.visibility = View.GONE
        btnDecideForMe.visibility = View.GONE
        refreshProfile()
    }

    private fun setupCharts() {
        val visitedList = DataManager.getByStatus("visited")
        
        // Pie Chart: Spending by Cuisine
        val cuisineSpending = visitedList.groupBy { it.cuisine }
            .mapValues { entry -> entry.value.sumOf { it.spendAmount }.toFloat() }

        val pieEntries = cuisineSpending.map { PieEntry(it.value, it.key) }
        val pieDataSet = PieDataSet(pieEntries, "").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 14f
            valueTextColor = android.graphics.Color.parseColor("#4A362B")
        }
        
        pieChart.apply {
            data = PieData(pieDataSet)
            description.isEnabled = false
            centerText = "Spending by Cuisine"
            setCenterTextSize(16f)
            setHoleColor(android.graphics.Color.TRANSPARENT)
            legend.isEnabled = true
            legend.textColor = android.graphics.Color.parseColor("#4A362B")
            animateY(1000)
            invalidate()
        }

        // Bar Chart: Monthly Spending
        val monthlySpending = mutableMapOf<String, Float>()
        val sdfInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sdfOutput = SimpleDateFormat("MMM yyyy", Locale.getDefault())

        visitedList.forEach { res ->
            val dateStr = if (res.visitDate.isEmpty()) {
                sdfInput.format(java.util.Date()) // Fallback to today
            } else {
                res.visitDate
            }
            
            try {
                val date = sdfInput.parse(dateStr)
                if (date != null) {
                    val monthYear = sdfOutput.format(date)
                    monthlySpending[monthYear] = (monthlySpending[monthYear] ?: 0f) + res.spendAmount.toFloat()
                }
            } catch (e: Exception) {
                // Skip only if the date is truly unparseable
            }
        }

        val sortedMonths = monthlySpending.keys.sortedBy { sdfOutput.parse(it) }
        val barEntries = sortedMonths.mapIndexed { index, month ->
            BarEntry(index.toFloat(), monthlySpending[month] ?: 0f)
        }

        val barDataSet = BarDataSet(barEntries, "Monthly Spending (Rs.)").apply {
            color = android.graphics.Color.parseColor("#DF8130")
            valueTextColor = android.graphics.Color.parseColor("#4A362B")
            valueTextSize = 10f
        }

        barChart.apply {
            data = BarData(barDataSet)
            description.isEnabled = false
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(sortedMonths)
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                textColor = android.graphics.Color.parseColor("#4A362B")
                setDrawGridLines(false)
                granularity = 1f
            }
            axisLeft.textColor = android.graphics.Color.parseColor("#4A362B")
            axisRight.isEnabled = false
            legend.textColor = android.graphics.Color.parseColor("#4A362B")
            animateY(1000)
            invalidate()
        }
    }

    private fun refreshProfile() {
        val name = AuthManager.currentUserName(this).ifBlank { "Food Explorer" }
        tvProfileName.text = name
        tvProfileEmail.text = AuthManager.currentUserEmail(this)
        tvProfileInitials.text = getInitials(name)
        tvProfileWishlistCount.text = DataManager.getByStatus("wishlist").size.toString()
        tvProfileVisitedCount.text = DataManager.getByStatus("visited").size.toString()
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (parts.isEmpty()) return "U"
        return when {
            parts.size == 1 -> parts[0].take(2).uppercase(Locale.getDefault())
            else -> parts.take(2).joinToString("") { part -> part.first().uppercaseChar().toString() }
        }
    }

    private fun displayedListForCurrentTab(): ArrayList<Restaurant> {
        val currentStatus = if (tabLayout.selectedTabPosition == 0) "wishlist" else "visited"
        val base = DataManager.getByStatus(currentStatus)
        return if (filterCuisine.isEmpty() && filterMustTry.isEmpty() && filterPrice.isEmpty()) {
            base
        } else {
            RestaurantFilter.filterResults(base, filterCuisine, filterMustTry, filterPrice)
        }
    }

    private fun loadFilteredData() {
        val currentList = displayedListForCurrentTab()
        val currentStatus = if (tabLayout.selectedTabPosition == 0) "wishlist" else "visited"

        if (currentList.isEmpty()) {
            tvNoResults.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            tvNoResults.text = if (currentStatus == "wishlist") "No results in Wishlist" else "No results in Visited"
        } else {
            tvNoResults.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.updateData(currentList)
        }

        if (currentStatus == "visited") {
            layoutBudgetDashboard.visibility = View.VISIBLE
            fabAdd.visibility = View.GONE
            btnDecideForMe.visibility = View.GONE

            val allVisitedList = DataManager.getByStatus("visited")
            val totalSpend = allVisitedList.sumOf { it.spendAmount.toLong() }
            
            tvTotalSpend.text = "Rs. $totalSpend"
            tvSpendStats.text = "${allVisitedList.size} outings"
        } else {
            layoutBudgetDashboard.visibility = View.GONE
            fabAdd.visibility = View.VISIBLE
            btnDecideForMe.visibility = View.VISIBLE
        }
    }

    private fun showDecideForMeDialog() {
        val wishlist = RestaurantFilter.filterResults(
            DataManager.getByStatus("wishlist"),
            filterCuisine, filterMustTry, filterPrice
        )

        if (wishlist.isEmpty()) {
            Toast.makeText(this, "Wishlist is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val pick = wishlist[Random.nextInt(wishlist.size)]

        // Material 3 Dialog implementation with custom theme
        MaterialAlertDialogBuilder(this, R.style.DineOutDialog)
            .setTitle("DineOut")
            .setMessage("Today you're going to ${pick.name}!")
            .setPositiveButton("Let's Go!") { _, _ ->
                startActivity(Intent(this, DetailActivity::class.java).putExtra("RES_NAME", pick.name))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}