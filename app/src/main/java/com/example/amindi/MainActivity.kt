package com.example.amindi

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.amindi.api.WeatherApi
import com.example.amindi.models.WeatherResponse
import com.example.amindi.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var dialog:Dialog
    private lateinit var viewModel: WeatherViewModel
    val TAG = "Error"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val repository = Repository()
        val viewModelProviderFactory = ViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(WeatherViewModel::class.java)


        viewModel.getWeather.observe(this, androidx.lifecycle.Observer {response->

            when(response){

                is Resource.Success->{

                    hideDialog()
                    response.data?.let { weatherResponse ->

                        setUpUi(weatherResponse)
                    }



                }

                is Resource.Error->{
                    hideDialog()
                    response.message!!.let {message->

                        Log.e(TAG,"Error, $message")

                    }


                }

                is Resource.Loading->{

                    showDialog()
                }

            }




        })



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setSupportActionBar(toolbar)


        if(!isLocationEnabled()){

            Toast.makeText(this,
            "Your Location provider is turned off. Please Turn it on",Toast
                    .LENGTH_LONG).show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)

        }else{

            Dexter.withActivity(this)
                .withPermissions(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(object: MultiplePermissionsListener{
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if(report!!.areAllPermissionsGranted()){

                            requestLocationData()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                       showRationaleDialogForPermissions()
                    }


                }).onSameThread().check()
        }


    }


    private fun isLocationEnabled():Boolean{

        val locationManager: LocationManager =

            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)


    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData(){

      val locationRequest = com.google.android.gms.location.LocationRequest()
        locationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient.requestLocationUpdates(
            locationRequest,locationCallBack, Looper.myLooper()
        )


    }

    private val locationCallBack = object: LocationCallback(){

        override fun onLocationResult(locationResult: LocationResult) {

            val lastLocation:Location = locationResult.lastLocation!!
            val latitude = lastLocation.latitude
            val longitude = lastLocation.longitude
          viewModel.getWeather(latitude,longitude)


        }




    }



    private fun setUpUi(weatherList: WeatherResponse){

        for(i in weatherList.weather.indices){

            location.text = weatherList.name
            description.text = weatherList.weather[i].description
            celcius.text = weatherList.main.temp.toString() + "°C"
            feels_like.text = "Feels like : " + weatherList.main.feels_like.toString() + " ° C"
            date.text = SimpleDateFormat("EEE,HH:mm",Locale.getDefault()).format(Date())
            wind.text = "  Wind : " +weatherList.wind.speed.toString() + " km/h"
            humidity.text = " Humidity " +weatherList.main.humidity.toString() + "%"
            sunrise.text = unixTime(weatherList.sys.sunrise)
            sunset.text = unixTime(weatherList.sys.sunset)



            when (weatherList.weather[i].icon) {


                "01d" -> {
                    image.setImageResource(R.drawable.sunny)

                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.sunny
                        )
                    )




                }


                "02d" -> {

                    image.setImageResource(R.drawable.cloud)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.cloud
                        )
                    )

                }
                "03d" -> {

                    image.setImageResource(R.drawable.cloud)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.cloud
                        )
                    )

                }
                "04d" -> {

                    image.setImageResource(R.drawable.cloud)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.cloud
                        )
                    )

                }
                "04n" -> {

                    image.setImageResource(R.drawable.cloud)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.cloud
                        )
                    )

                }
                "10d" -> {

                    image.setImageResource(R.drawable.rainy)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.rainy
                        )
                    )

                }
                "11d" -> {

                    image.setImageResource(R.drawable.rainy)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.rainy
                        )
                    )

                }
                "13d" -> {

                    image.setImageResource(R.drawable.snow)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.snow
                        )
                    )

                }
                "01n" -> {

                    image.setImageResource(R.drawable.cloud)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.cloud
                        )
                    )

                }
                "02n" -> {

                    image.setImageResource(R.drawable.cloud)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.cloud
                        )
                    )

                }
                "03n" -> {

                    image.setImageResource(R.drawable.cloud)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.cloud
                        )
                    )

                }
                "10n" -> {

                    image.setImageResource(R.drawable.cloud)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.cloud
                        )
                    )

                }
                "11n" -> {

                    image.setImageResource(R.drawable.rainy)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.rainy
                        )
                    )

                }
                "13n" -> {

                    image.setImageResource(R.drawable.snow)
                    card.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.snow
                        )
                    )

                }
            }


        }

    }

    private fun unixTime(time:Long):String?{

        val date =Date(time*1000L)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    private fun showDialog(){

        dialog = Dialog(this)

        dialog.setCancelable(false)

        dialog.setContentView(R.layout.dialog)

        dialog.show()


    }

    private fun hideDialog(){

        if(dialog!=null){

            dialog.dismiss()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.recreate ->{

                recreate()
                return true

            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun showRationaleDialogForPermissions() {

        AlertDialog.Builder(this).setMessage("turn off")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }

            }.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()

            }.show()
    }


}