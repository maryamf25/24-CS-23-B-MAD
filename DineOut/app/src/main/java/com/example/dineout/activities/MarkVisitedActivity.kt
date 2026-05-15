package com.example.dineout.activities

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.dineout.R
import com.example.dineout.data.DataManager
import java.io.File
import java.io.FileOutputStream

class MarkVisitedActivity : AppCompatActivity() {

    private val photoPaths = mutableListOf<String>()

    companion object {
        private const val PHOTO_SEPARATOR = "||"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_visited)

        val resName = intent.getStringExtra("RES_NAME") ?: run { finish(); return }

        val ivPhoto = findViewById<ImageView>(R.id.ivPhoto)
        val btnCamera = findViewById<Button>(R.id.btnCamera)
        val btnGallery = findViewById<Button>(R.id.btnGallery)
        val sbRating = findViewById<SeekBar>(R.id.sbMarkRating)
        val tvRatingValue = findViewById<TextView>(R.id.tvMarkRatingValue)
        val rgWorth = findViewById<RadioGroup>(R.id.rgWorth)
        val rbWorthYes = findViewById<RadioButton>(R.id.rbWorthYes)
        val etSpend = findViewById<EditText>(R.id.etMarkSpend)
        val etNotes = findViewById<EditText>(R.id.etMarkNotes)
        val btnSave = findViewById<Button>(R.id.btnMarkSave)

        DataManager.loadData(this)
        val existing = DataManager.getRestaurantByName(resName)
        photoPaths.addAll(parsePhotoPaths(existing?.photoPath.orEmpty()))
        showLastPhoto(ivPhoto)

        val initialRating = if ((existing?.rating ?: 0) in 1..5) existing!!.rating else 3
        sbRating.progress = initialRating - 1
        tvRatingValue.text = initialRating.toString()
        sbRating.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvRatingValue.text = (progress + 1).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        if ((existing?.worthRating ?: 5) >= 3) {
            rbWorthYes.isChecked = true
        } else {
            rgWorth.check(R.id.rbWorthNo)
        }

        val takePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                val path = saveBitmapToInternal(bitmap)
                photoPaths.add(path)
                ivPhoto.setImageBitmap(bitmap)
            }
        }

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val path = copyUriToInternal(uri)
                if (path != null) {
                    photoPaths.add(path)
                    ivPhoto.setImageURI(Uri.fromFile(File(path)))
                }
            }
        }

        btnCamera.setOnClickListener { takePreview.launch(null) }
        btnGallery.setOnClickListener { pickImage.launch("image/*") }

        btnSave.setOnClickListener {
            val rating = sbRating.progress + 1
            val worth = if (rgWorth.checkedRadioButtonId == R.id.rbWorthYes) 5 else 1
            val spend = etSpend.text.toString().toIntOrNull() ?: 0
            val notes = etNotes.text.toString()
            val joinedPhotoPaths = photoPaths.distinct().joinToString(PHOTO_SEPARATOR)

            DataManager.markAsVisited(this, resName, rating, notes, spend, worth, joinedPhotoPaths)
            Toast.makeText(this, "Added to Visited!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun parsePhotoPaths(raw: String): List<String> {
        if (raw.isBlank()) return emptyList()
        return raw.split(PHOTO_SEPARATOR).map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun showLastPhoto(ivPhoto: ImageView) {
        val last = photoPaths.lastOrNull() ?: return
        val file = File(last)
        if (file.exists()) {
            ivPhoto.setImageURI(Uri.fromFile(file))
        }
    }

    private fun saveBitmapToInternal(bitmap: Bitmap): String {
        val dir = File(filesDir, "photos")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "photo_" + System.currentTimeMillis() + ".jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
        return file.absolutePath
    }

    private fun copyUriToInternal(uri: Uri): String? {
        return try {
            val input = contentResolver.openInputStream(uri) ?: return null
            val dir = File(filesDir, "photos")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "photo_" + System.currentTimeMillis() + ".jpg")
            FileOutputStream(file).use { out ->
                input.copyTo(out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}