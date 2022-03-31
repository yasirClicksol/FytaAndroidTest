package com.example.fytatask.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Species {
    @SerializedName("scientificNameWithoutAuthor")
    @Expose
    var scientificNameWithoutAuthor: String? = null

    @SerializedName("scientificNameAuthorship")
    @Expose
    var scientificNameAuthorship: String? = null

    @SerializedName("genus")
    @Expose
    var genus: Genus? = null

    @SerializedName("family")
    @Expose
    var family: Family? = null

    @SerializedName("commonNames")
    @Expose
    var commonNames: List<String>? = null

    @SerializedName("scientificName")
    @Expose
    var scientificName: String? = null
}