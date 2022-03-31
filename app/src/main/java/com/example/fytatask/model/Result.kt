package com.example.fytatask.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Result {
    @SerializedName("score")
    @Expose
    var score: Double? = null

    @SerializedName("species")
    @Expose
    var species: Species? = null

    @SerializedName("gbif")
    @Expose
    var gbif: Gbif? = null
}