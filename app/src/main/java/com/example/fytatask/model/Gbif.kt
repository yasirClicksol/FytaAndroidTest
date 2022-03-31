package com.example.fytatask.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Gbif {
    @SerializedName("id")
    @Expose
    var id: String? = null
}