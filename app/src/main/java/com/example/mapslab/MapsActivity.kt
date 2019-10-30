package com.example.mapslab

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private lateinit var fusedLocationClient: FusedLocationProviderClient // member var
    private lateinit var locationCallback : LocationCallback

    private lateinit var locationRequest : LocationRequest

    private val DEFAULZOOM = 13.0f








    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        subscribeforUpdates()


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)





    }

    public override fun onStart() {
        super.onStart()
        if (!hasLocationPermissions()) {
            requestPermissions()
        } else {
            getAddress()
        }
    }

    public fun requestPermissions(){
        ActivityCompat.requestPermissions(this@MapsActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    /* Checking for permissions */
    fun hasLocationPermissions() = hasFineLocationPermission() && hasCoarseLocationPermission()
    fun hasFineLocationPermission() = ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    fun hasCoarseLocationPermission() = ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED



    /* Request onSuccess callback snippet */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != REQUEST_PERMISSIONS_REQUEST_CODE) return
        if (!grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getAddress()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//mapLAB
    }

    fun getAddress()
    {
        /* Below is getAddress() function snippet */
        fusedLocationClient.lastLocation.addOnSuccessListener(this, OnSuccessListener {
                location ->
            if (location != null) {
                location.getLatitude()
                location.getLongitude()

                val sydney = LatLng(location.getLatitude(), location.getLongitude())
                mMap.addMarker(MarkerOptions().position(sydney).title("My Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,DEFAULZOOM ))

            }
        }).addOnFailureListener(this) { e -> Log.w( "getLastLocation:onFailure", e) }
    }

    private fun subscribeforUpdates()
    {
        fusedLocationClient.lastLocation.addOnSuccessListener(this, OnSuccessListener {
                location ->
            if (location == null) {
                createLocationRequest()
                startLocationUpdates()
            }
        }).addOnFailureListener(this) { e -> Log.w("getLastLocation:onFailure", e)}
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
            Looper.getMainLooper())
    }


}
