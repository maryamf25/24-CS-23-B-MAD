package com.example.dineout.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.dineout.R
import java.io.File

class PhotoCarouselAdapter : RecyclerView.Adapter<PhotoCarouselAdapter.PhotoViewHolder>() {

    private val photoPaths = mutableListOf<String>()

    fun submit(paths: List<String>) {
        photoPaths.clear()
        photoPaths.addAll(paths)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detail_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val file = File(photoPaths[position])
        if (file.exists()) {
            holder.imageView.setImageURI(Uri.fromFile(file))
        } else {
            holder.imageView.setImageDrawable(null)
        }
    }

    override fun getItemCount(): Int = photoPaths.size

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.ivCarouselPhoto)
    }
}
