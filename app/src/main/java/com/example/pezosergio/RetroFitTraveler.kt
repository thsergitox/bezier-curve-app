package com.example.pezosergio

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroFitTraveler {
    private const val BASE_URL = "https://thsergitox-agviajerop1.hf.space/\n"
    val aTravelerAPI:TravelerAPI by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(TravelerAPI::class.java)
    }
}