package com.example.deliveryapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors

class Location1 : AppCompatActivity() {

    // View components
    private lateinit var btnGetLocation: Button
    private lateinit var tvLocation: TextView

    // Location services
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    // Geocoding
    private val geocoderExecutor = Executors.newSingleThreadExecutor()

    // Constants
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val REQUEST_CHECK_SETTINGS = 1002
        private const val LOCATION_REQUEST_TIMEOUT = 10000L // 10 seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location1)

        // Initialize views
        btnGetLocation = findViewById(R.id.currentLocation)
        tvLocation = findViewById(R.id.textView1)

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configure location request
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // Initialize location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.locations.firstOrNull()?.let {
                    handleLocationUpdate(it)
                }
            }
        }

        // Set button click listener
        btnGetLocation.setOnClickListener {
            checkLocationRequirements()
        }
    }

    private fun checkLocationRequirements() {
        when {
            !isOnline() -> showMessage("Internet connection required for address lookup")
            !checkLocationPermission() -> requestLocationPermission()
            !isLocationEnabled() -> enableLocationServices()
            else -> startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (checkLocationPermission() && isLocationEnabled()) {
            // Get last known location first
            getLastKnownLocation()

            // Request ongoing updates
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        showMessage("Getting location...")

        val locationTask = fusedLocationClient.lastLocation

        // Set timeout for location request
        Handler(Looper.getMainLooper()).postDelayed({
            if (!locationTask.isComplete) {
                locationTask.addOnCompleteListener {
                    if (!it.isSuccessful) {
                        showMessage("Location request timed out")
                    }
                }
            }
        }, LOCATION_REQUEST_TIMEOUT)

        locationTask.addOnSuccessListener { location ->
            location?.let {
                handleLocationUpdate(it)
            } ?: showMessage("No recent location available")
        }.addOnFailureListener { e ->
            Log.e("Location", "Error getting location", e)
            showMessage("Failed to get location")
        }
    }

    private fun handleLocationUpdate(location: Location) {
        geocoderExecutor.execute {
            try {
                val addresses = with(Geocoder(this, Locale.getDefault())) {
                    getFromLocation(location.latitude, location.longitude, 1)
                }

                val address = addresses?.firstOrNull()
                val city = address?.locality ?: address?.subAdminArea ?: address?.adminArea
                val country = address?.countryName
                val locationText = listOfNotNull(city, country).joinToString(", ")

                // تحديث بيانات المستخدم في Firestore
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                val locationMap = hashMapOf(
                    "lat" to location.latitude,
                    "lng" to location.longitude,
                    "city" to city,
                    "country" to country
                )
                val sharedPref = this.getSharedPreferences("UserLocation", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()

                editor.putString("lat", location.latitude.toString())
                editor.putString("lng", location.longitude.toString())
                editor.putString("city", city)
                editor.putString("country", country)

                editor.apply() // أو editor.commit()


                if (user != null) {
                    db.collection("users")
                        .document(user.uid)
                        .update("location", locationMap)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Location updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Failed to update location: ${e.message}")
                        }
                }

                runOnUiThread {
                    navigateToHomeActivity(location, city, country)
                }
            } catch (e: Exception) {
                Log.e("Geocoding", "Error getting address", e)
                runOnUiThread {
                    showMessage("Error getting address details")
                }
            }
        }

    }

    private fun navigateToHomeActivity(location: Location, city: String?, country: String?) {
        Intent(this, Home()::class.java).apply {
            putExtra("latitude", location.latitude)
            putExtra("longitude", location.longitude)
            putExtra("accuracy", location.accuracy)
            putExtra("city", city)
            putExtra("country", country)
            putExtra("full_address", "$city, $country")
            startActivity(this)
        }
    }

    private fun enableLocationServices() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        LocationServices.getSettingsClient(this)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener {
                startLocationUpdates()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Log.e("Location", "Error showing location settings", sendEx)
                    }
                } else {
                    showMessage("Location services unavailable")
                }
            }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun isOnline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationRequirements()
                } else {
                    showMessage("Location permission required")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                checkLocationRequirements()
            } else {
                showMessage("Location services must be enabled")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkLocationPermission() && isLocationEnabled()) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}