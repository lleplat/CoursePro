package com.example.coursepro.api.model

import com.google.gson.annotations.SerializedName

data class APIResponse (

    @SerializedName("products")
    val product : List<ProductResponse>

)

data class ProductResponse (

    @SerializedName("product_name")
    val product_name : String

)