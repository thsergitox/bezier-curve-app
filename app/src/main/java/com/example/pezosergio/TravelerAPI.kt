package com.example.pezosergio

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TravelerAPI {
    @POST("/predict/")
    fun predict(@Body request: RequestData): Call<ResponseData>
}