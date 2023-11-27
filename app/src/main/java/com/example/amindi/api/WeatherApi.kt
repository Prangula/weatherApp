package com.example.amindi.api

import com.example.amindi.models.WeatherResponse
import com.example.amindi.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("2.5/weather")

   suspend fun getWeather(

        @Query("lat")lat:Double,
        @Query("lon")lon:Double,
        @Query("units")units:String,
        @Query("appid")appId:String = Constants.APP_ID,

    ):Response<WeatherResponse>

}