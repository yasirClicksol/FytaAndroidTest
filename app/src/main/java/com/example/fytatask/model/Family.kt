package com.example.fytatask.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Family {
    @SerializedName("scientificNameWithoutAuthor")
    @Expose
    var scientificNameWithoutAuthor: String? = null

    @SerializedName("scientificNameAuthorship")
    @Expose
    var scientificNameAuthorship: String? = null

    @SerializedName("scientificName")
    @Expose
    var scientificName: String? = null
}