package com.example.coursepro.api

import com.example.coursepro.api.model.APIResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceAPI {


    @GET("products")
    suspend fun getProductInfo(@Query("barcode") barcode : String, @Query("key") key : String) : APIResponse

}