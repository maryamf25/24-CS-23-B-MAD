package com.example.dineout.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dineout.R
import com.example.dineout.adapters.RestaurantAdapter
import com.example.dineout.data.DataManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RestaurantAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var layoutBudgetDashboard: View
    private lateinit var tvTotalSpend: TextView
    private lateinit var tvSpendStats: TextView
    private lateinit var fabAdd: ExtendedFloatingActionButton
    private lateinit var btnDecideForMe: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DataManager.loadData(this)

        recyclerView = findViewById(R.id.recyclerView)
        tabLayout = findViewById(R.id.tabLayout)
        layoutBudgetDashboard = findViewById(R.id.layoutBudgetDashboard)
        tvTotalSpend = findViewById(R.id.tvTotalSpend)
        tvSpendStats = findViewById(R.id.tvSpendStats)
        fabAdd = findViewById(R.id.fabAdd)
        btnDecideForMe = findViewById(R.id.btnDecideForMe)

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

        ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.bindingAdapterPosition
                    if (pos == RecyclerView.NO_POSITION) return
                    val currentStatus = if (tabLayout.selectedTabPosition == 0) "wishlist" else "visited"
                    val currentList = DataManager.getByStatus(currentStatus)
                    if (pos !in currentList.indices) return
                    val name = currentList[pos].name
                    DataManager.deleteRestaurant(this@MainActivity, name)
                    Toast.makeText(this@MainActivity, "$name Deleted!", Toast.LENGTH_SHORT).show()
                    loadFilteredData()
                }
            }
        ).attachToRecyclerView(recyclerView)

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

        btnDecideForMe.setOnClickListener { showDecideForMeDialog() }

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
            btnDecideForMe.visibility = View.GONE

            val totalSpend = currentList.sumOf { it.spendAmount }
            val visitedCount = currentList.size
            val avgSpend = if (visitedCount > 0) totalSpend / visitedCount else 0

            tvTotalSpend.text = "Rs. $totalSpend"
            tvSpendStats.text = "$visitedCount outings · avg Rs. $avgSpend"
        } else {
            layoutBudgetDashboard.visibility = View.GONE
            fabAdd.visibility = View.VISIBLE
            btnDecideForMe.visibility = View.VISIBLE
        }

        adapter.updateData(currentList)
    }

    private fun showDecideForMeDialog() {
        val wishlist = DataManager.getByStatus("wishlist")
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