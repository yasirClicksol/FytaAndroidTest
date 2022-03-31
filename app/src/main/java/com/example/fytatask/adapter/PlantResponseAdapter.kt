package com.example.fytatask.adapter


import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.fytatask.R
import com.example.fytatask.model.PlantResponseModel
import com.example.fytatask.utils.showDialog
import java.util.*



class PlantResponseAdapter(private val plantsResult: PlantResponseModel? , private val imagePath: String?) :
    RecyclerView.Adapter<PlantResponseAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var plantImage: AppCompatImageView = view.findViewById(R.id.iv_showImage)
        var plantName: TextView = view.findViewById(R.id.heading1)
        var plantAuthor: TextView = view.findViewById(R.id.heading2)
        var plantPerc: TextView = view.findViewById(R.id.tv_plant_perc)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.li_plant_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(imagePath)
            .apply(RequestOptions().transform(CenterCrop(), GranularRoundedCorners(24f, 0f, 0f, 24f)))
            .into(holder.plantImage)
        val percent = String.format(Locale.US, "%.2f", plantsResult?.results?.get(position)?.score)
        holder.plantPerc.text = "$percent%"
        holder.plantName.text = plantsResult?.results?.get(position)?.species?.scientificName
        holder.plantAuthor.text = plantsResult?.results?.get(position)?.species?.scientificNameAuthorship
        holder.itemView.setOnClickListener{
            holder.itemView.context.showDialog(plantsResult?.results?.get(position)?.species?.scientificName, imagePath)
        }

    }

    override fun getItemCount(): Int {

        if (plantsResult?.results?.isNotEmpty() == true) {
            return plantsResult.results?.size!!
        }
        return 0
    }

}