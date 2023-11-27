package com.example.amindi

import com.example.amindi.api.RetrofitInstance

class Repository() {

    suspend fun getWeather(lat:Double,lon:Double,units:String) =

        RetrofitInstance.api.getWeather(lat,lon,units)


}