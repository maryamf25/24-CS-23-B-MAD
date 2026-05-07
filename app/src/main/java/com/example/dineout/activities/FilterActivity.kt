package com.example.dineout.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.dineout.R
import com.google.android.material.button.MaterialButton

class FilterActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CUISINE = "extra_filter_cuisine"
        const val EXTRA_BEST_FOR = "extra_filter_best_for"
        const val EXTRA_PRICE = "extra_filter_price"
    }

    private lateinit var spinnerCuisine: Spinner
    private lateinit var spinnerBestFor: Spinner
    private lateinit var spinnerPrice: Spinner

    private val anyLabel: String by lazy { getString(R.string.filter_any) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_filter)

        findViewById<Toolbar>(R.id.toolbarFilter).setNavigationOnClickListener { finish() }

        spinnerCuisine = findViewById(R.id.spinnerFilterCuisine)
        spinnerBestFor = findViewById(R.id.spinnerFilterBestFor)
        spinnerPrice = findViewById(R.id.spinnerFilterPrice)

        val cuisineChoices = buildCuisineChoices()
        val bestForChoices = resources.getStringArray(R.array.filter_best_for_options).toList()
        val priceChoices = resources.getStringArray(R.array.filter_price_options).toList()

        // Create styled adapters for each spinner
        val cuisineAdapter = ArrayAdapter(this, R.layout.spinner_item, cuisineChoices)
        cuisineAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerCuisine.adapter = cuisineAdapter

        val bestForAdapter = ArrayAdapter(this, R.layout.spinner_item, bestForChoices)
        bestForAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerBestFor.adapter = bestForAdapter

        val priceAdapter = ArrayAdapter(this, R.layout.spinner_item, priceChoices)
        priceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerPrice.adapter = priceAdapter

        val initialCuisine = intent.getStringExtra(EXTRA_CUISINE).orEmpty()
        val initialBest = intent.getStringExtra(EXTRA_BEST_FOR).orEmpty()
        val initialPrice = intent.getStringExtra(EXTRA_PRICE).orEmpty()

        setSpinnerToValue(spinnerCuisine, cuisineChoices, initialCuisine)
        setSpinnerToValue(spinnerBestFor, bestForChoices, initialBest)
        setSpinnerToValue(spinnerPrice, priceChoices, initialPrice)

        findViewById<MaterialButton>(R.id.btnFilterApply).setOnClickListener { applyAndFinish() }
        findViewById<MaterialButton>(R.id.btnFilterClear).setOnClickListener { clearAndFinish() }
    }

    private fun buildCuisineChoices(): List<String> {
        val list = mutableListOf(anyLabel)
        val fromRes = resources.getStringArray(R.array.cuisine_options)
        if (fromRes.isNotEmpty()) {
            list.addAll(fromRes.drop(1))
        }
        return list
    }

    private fun setSpinnerToValue(spinner: Spinner, choices: Array<out String>, current: String) {
        val idx = choices.indexOfFirst { it.equals(current, ignoreCase = true) }
        spinner.setSelection(if (idx >= 0) idx else 0)
    }

    private fun setSpinnerToValue(spinner: Spinner, choices: List<String>, current: String) {
        val idx = choices.indexOfFirst { it.equals(current, ignoreCase = true) }
        spinner.setSelection(if (idx >= 0) idx else 0)
    }

    private fun applyAndFinish() {
        val cuisine = spinnerCuisine.selectedItem?.toString().orEmpty()
        val best = spinnerBestFor.selectedItem?.toString().orEmpty()
        val price = spinnerPrice.selectedItem?.toString().orEmpty()

        setResult(
            RESULT_OK,
            Intent().apply {
                putExtra(EXTRA_CUISINE, if (cuisine == anyLabel) "" else cuisine)
                putExtra(EXTRA_BEST_FOR, if (best == anyLabel) "" else best)
                putExtra(EXTRA_PRICE, if (price == anyLabel) "" else price)
            }
        )
        finish()
    }

    private fun clearAndFinish() {
        setResult(
            RESULT_OK,
            Intent().apply {
                putExtra(EXTRA_CUISINE, "")
                putExtra(EXTRA_BEST_FOR, "")
                putExtra(EXTRA_PRICE, "")
            }
        )
        finish()
    }
}
