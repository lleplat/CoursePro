package com.example.coursepro

import com.example.coursepro.api.ServiceAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DataProvider {

    private val BASE_URL = "https://api.barcodelookup.com/v2/"
    private val KEY = "4jlp2982uqxrntxrrpt9xuo314mbqz"

    private val service = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ServiceAPI::class.java)

    suspend fun getProductInfo(barcode : String) : String = service.getProductInfo(barcode, KEY).product[0].product_name
}