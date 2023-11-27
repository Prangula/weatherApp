package com.example.amindi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amindi.models.WeatherResponse
import com.example.amindi.utils.Constants
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel(val repository: Repository):ViewModel() {


    val getWeather:MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()
    val units = Constants.METRIC_UNIT



    fun getWeather(lat:Double,lon:Double){


        viewModelScope.launch {

            getWeather.postValue(Resource.Loading())
            val response = repository.getWeather(lat,lon,units)
            getWeather.postValue(handleweather(response))


        }


    }


    private fun handleweather(response: Response<WeatherResponse>):Resource<WeatherResponse>{

        if(response!!.isSuccessful){

            response.body().let {resultResponse->

                return Resource.Success(resultResponse)

            }


        }
        return Resource.Error(response.message())




    }




}