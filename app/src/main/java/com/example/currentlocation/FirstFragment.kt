package com.example.currentlocation

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.mobiledeveloperblog.geolocation1.geolocation.GeoLocationManager
import java.util.*
import kotlin.properties.Delegates

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private lateinit var locationManager: GeoLocationManager
    private var locationTrackingRequested = false
    private val LOCATION_PERMISSION_CODE = 1000
    private lateinit var latitudeTextView: TextView
    private lateinit var longitudeTextView: TextView
    private lateinit var statusTextView: TextView
    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Create GeoLocationManager
        locationManager = GeoLocationManager(activity as Context)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations){
                // Update UI
                latitude = location.latitude
                longitude = location.longitude
            }
            var gc = Geocoder(context, Locale.getDefault())
            var addresses = gc.getFromLocation(latitude, longitude, 2)
            var address = addresses.get(0)
            longitudeTextView.text= address.getAddressLine(0)
            latitudeTextView.text = address.locality
        }
    }

    private fun requestLocationPermission(): Boolean {
        var permissionGranted = false

        // If system os is Marshmallow or Above, we need to request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val cameraPermissionNotGranted = ContextCompat.checkSelfPermission(
                activity as Context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
            if (cameraPermissionNotGranted){
                val permission = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

                // Display permission dialog
                requestPermissions(permission, LOCATION_PERMISSION_CODE)
            }
            else{
                // Permission already granted
                permissionGranted = true
            }
        }
        else{
            // Android version earlier than M -> no need to request permission
            permissionGranted = true
        }

        return permissionGranted
    }

    // Handle Allow or Deny response from the permission dialog
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode === LOCATION_PERMISSION_CODE) {
            if (grantResults.size === 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED){
                // Permission was granted
                locationManager.startLocationTracking(locationCallback)
                statusTextView.text = "Started"
            }
            else{
                // Permission was denied
                showAlert("Location permission was denied. Unable to track location.")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.stopLocationTracking()
        statusTextView.text = "Stopped"
    }

    override fun onResume() {
        super.onResume()

        if  (locationTrackingRequested) {
            locationManager.startLocationTracking(locationCallback)
            statusTextView.text = "Started"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Register text views
        latitudeTextView = view.findViewById(R.id.textview_latitude)
        longitudeTextView = view.findViewById(R.id.textview_longitude)
        statusTextView = view.findViewById(R.id.textview_status)

        // Register button click listeners
        view.findViewById<Button>(R.id.button_start_location_scan).setOnClickListener {

            val permissionGranted = requestLocationPermission();
            if (permissionGranted) {
                locationManager.startLocationTracking(locationCallback)
                locationTrackingRequested = true
                statusTextView.text = "Started"
            }
        }

        view.findViewById<Button>(R.id.button_stop_location_scan).setOnClickListener {
            locationManager.stopLocationTracking()
            locationTrackingRequested = false
            statusTextView.text = "Stopped"
        }
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(activity as Context)
        builder.setMessage(message)
        val dialog = builder.create()
        dialog.show()
    }
}