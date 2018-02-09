package com.example.mansigoel.findmylocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onMapReady(p0: GoogleMap?) {
        val longitude = myLocation?.longitude as Double
        val latitude = myLocation?.latitude as Double

        val currentPlace = LatLng(latitude, longitude)

        p0?.addMarker(MarkerOptions().position(currentPlace)
                .title("Pune"))
        val target = CameraPosition.builder().target(currentPlace).zoom(17f).build()
        p0?.animateCamera(CameraUpdateFactory.newCameraPosition(target), 1000, null)

        Log.d("Mansi", "Latitude: " + latitude)
        Log.d("Mansi", "Longitude: " + longitude)
    }

    private var myLocation: Location? = null
    private val ACCESS_FINE_LOCATION_PERMISSION_CONSTANT: Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_find_my_loc.bringToFront()

        btn_find_my_loc.setOnClickListener {
            requestPermission()

            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //permission is not granted then ask
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Information about why you need permission when user has declined permission once
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Need Location permission")
                alertDialog.setMessage("This app need location permission to continue")
                alertDialog.setPositiveButton("GRANT", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, p1: Int) {
                        //Permission is granted now
                        dialog?.cancel()
                        //actually request permission now
                        ActivityCompat.requestPermissions(this@MainActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET), 1)
                    }
                })
                alertDialog.setNegativeButton("CANCEL", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, p1: Int) {
                        dialog?.cancel()
                    }
                })
                alertDialog.show()
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET),
                        ACCESS_FINE_LOCATION_PERMISSION_CONSTANT)
            }
        } else {
            //You already have permission
            myLocation = fetchLocationAfterPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: kotlin.Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_CONSTANT) {
            if (grantResults.size > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                myLocation = fetchLocationAfterPermission()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Show Information about why you need the permission
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Need Location Permission")
                    builder.setMessage("This app needs location permission")
                    builder.setPositiveButton("Grant") { dialog, which ->
                        dialog.cancel()
                        ActivityCompat.requestPermissions(this@MainActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET),
                                ACCESS_FINE_LOCATION_PERMISSION_CONSTANT)
                    }
                    builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
                    builder.show()
                } else {
                    Toast.makeText(baseContext, "Unable to get Permission", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchLocationAfterPermission(): Location? {
        val mLocationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true);
        var bestLocation: Location? = null;
        for (provider in providers) {
            val l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
