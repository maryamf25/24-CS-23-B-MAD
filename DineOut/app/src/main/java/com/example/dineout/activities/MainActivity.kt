package com.example.dineout.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dineout.R
import com.example.dineout.adapters.RestaurantAdapter
import com.example.dineout.data.DataManager
import com.example.dineout.models.Restaurant
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var layoutBudgetDashboard: View
    private lateinit var tvTotalSpend: TextView
    private lateinit var tvSpendStats: TextView
    private lateinit var fabAdd: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DataManager.loadSampleData()

        recyclerView = findViewById(R.id.recyclerView)
        tabLayout = findViewById(R.id.tabLayout)
        layoutBudgetDashboard = findViewById(R.id.layoutBudgetDashboard)
        tvTotalSpend = findViewById(R.id.tvTotalSpend)
        tvSpendStats = findViewById(R.id.tvSpendStats)
        fabAdd = findViewById(R.id.fabAdd)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RestaurantAdapter(ArrayList()) { position ->
            val currentStatus = if (tabLayout.selectedTabPosition == 0) "wishlist" else "visited"
            val currentList = DataManager.getByStatus(currentStatus)

            val clickedRestaurant = currentList[position]
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("RES_NAME", clickedRestaurant.name)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                loadFilteredData()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        fabAdd.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            startActivity(intent)
        }

        loadFilteredData()
    }

    override fun onResume() {
        super.onResume()
        loadFilteredData()
    }

    private fun loadFilteredData() {
        val currentStatus = if (tabLayout.selectedTabPosition == 0) "wishlist" else "visited"
        var currentList = DataManager.getByStatus(currentStatus)

        // Hide FAB on Visited tab, show on Wishlist tab
        if (currentStatus == "visited") {
            layoutBudgetDashboard.visibility = View.VISIBLE
            fabAdd.visibility = View.GONE

            val totalSpend = currentList.sumOf { it.spendAmount }
            val visitedCount = currentList.size
            val avgSpend = if (visitedCount > 0) totalSpend / visitedCount else 0

            tvTotalSpend.text = "Rs. $totalSpend"
            tvSpendStats.text = "$visitedCount outings · avg Rs. $avgSpend"
        } else {
            layoutBudgetDashboard.visibility = View.GONE
            fabAdd.visibility = View.VISIBLE
        }

        adapter.updateData(currentList)
    }
}