package com.example.currentlocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var lm: LocationManager
    lateinit var loc: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 111)

            lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!

        var ll = object: LocationListener{
            override fun onLocationChanged(p0: Location) {
               reverseGeocode(p0)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                super.onStatusChanged(provider, status, extras)
            }

            override fun onProviderEnabled(provider: String) {
                super.onProviderEnabled(provider)
            }

            override fun onProviderDisabled(provider: String) {
                super.onProviderDisabled(provider)
            }

        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100.2f, ll)
    }

    private fun reverseGeocode(loc: Location) {
        var textView2 = findViewById<TextView>(R.id.textView2)
        var gc = Geocoder(this, Locale.getDefault())
        var addresses = gc.getFromLocation(loc.latitude, loc.longitude, 2)
        var address = addresses.get(0)
        textView2.setText("Current Location of Yout Device is \n${address.getAddressLine(0)}\n${address.locality}")
    }
}