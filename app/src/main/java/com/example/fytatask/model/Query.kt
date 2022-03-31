package com.example.fytatask.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Query {
    @SerializedName("project")
    @Expose
    var project: String? = null

    @SerializedName("images")
    @Expose
    var images: List<String>? = null

    @SerializedName("organs")
    @Expose
    var organs: List<String>? = null

    @SerializedName("includeRelatedImages")
    @Expose
    var includeRelatedImages: Boolean? = null
}