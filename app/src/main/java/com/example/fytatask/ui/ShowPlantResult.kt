package com.example.fytatask.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fytatask.R
import com.example.fytatask.adapter.PlantResponseAdapter
import com.example.fytatask.model.PlantResponseModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_show_plant_result.*
import java.io.File
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import androidx.recyclerview.widget.RecyclerView





class ShowPlantResult : AppCompatActivity() {
    var response: String? = ""
    var imagePath: String? = null
    private var mAdapter: PlantResponseAdapter? = null
    var plantsList: PlantResponseModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_plant_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val gson = Gson()
        intent?.apply {
            response = getStringExtra("plantResult")
            imagePath = getStringExtra("imagePath")
            plantsList = gson.fromJson(response, PlantResponseModel::class.java)
        }
        mAdapter = PlantResponseAdapter(plantsList , imagePath)
        rv_plant.layoutManager = LinearLayoutManager(this)
        rv_plant.adapter = mAdapter
        tv_retake.setOnClickListener {
            finish()
        }
        rv_plant.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    group_retake.visibility = View.VISIBLE
                }
                else{
                    group_retake.visibility = View.GONE
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}