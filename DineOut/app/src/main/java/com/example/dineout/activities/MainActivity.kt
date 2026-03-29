package com.example.dineout.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dineout.R
import com.example.dineout.adapters.RestaurantAdapter
import com.example.dineout.data.DataManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Load sample data from DataManager on app start
        DataManager.loadSampleData()

        // 2. Find UI elements by their IDs
        recyclerView = findViewById(R.id.recyclerView)
        tabLayout = findViewById(R.id.tabLayout)
        val fabAdd = findViewById<ExtendedFloatingActionButton>(R.id.fabAdd)

        // 3. Configure RecyclerView with a vertical layout manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initially show only "Wishlist" restaurants
        val initialData = DataManager.getByStatus("wishlist")

        // Provide data to the Adapter and attach it to the RecyclerView
        adapter = RestaurantAdapter(initialData) { position ->
            // This runs when a card is clicked
            Toast.makeText(this, "Restaurant Clicked!", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        // 4. Handle Tab clicks (Wishlist vs Visited)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> adapter.updateData(DataManager.getByStatus("wishlist")) // First Tab
                    1 -> adapter.updateData(DataManager.getByStatus("visited"))  // Second Tab
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 5. Handle FAB (+) button click
        fabAdd.setOnClickListener {
            val intent = android.content.Intent(this, AddEditActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the list based on current tab
        val currentStatus = if (tabLayout.selectedTabPosition == 0) "wishlist" else "visited"
        adapter.updateData(DataManager.getByStatus(currentStatus))
    }
}