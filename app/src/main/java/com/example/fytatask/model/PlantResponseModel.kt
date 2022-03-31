package com.example.fytatask.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class PlantResponseModel {
    @SerializedName("query")
    @Expose
    var query: Query? = null

    @SerializedName("language")
    @Expose
    var language: String? = null

    @SerializedName("preferedReferential")
    @Expose
    var preferedReferential: String? = null

    @SerializedName("bestMatch")
    @Expose
    var bestMatch: String? = null

    @SerializedName("results")
    @Expose
    var results: List<Result>? = null

    @SerializedName("version")
    @Expose
    var version: String? = null

    @SerializedName("remainingIdentificationRequests")
    @Expose
    var remainingIdentificationRequests: Int? = null
}