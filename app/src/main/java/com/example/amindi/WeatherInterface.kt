package com.example.amindi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {

    @GET("2.5/weather")

    fun getWeather(

        @Query("lat")lat:Double,
        @Query("lon")lon:Double,
        @Query("appid")appId:String,
        @Query("units")units:String

    ):Call<WeatherResponse>

}