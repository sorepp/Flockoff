package com.example.flockavoid

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.flockavoid.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationOverlay: MyLocationNewOverlay

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                enableLocation()
            } else {
                Toast.makeText(this, "Location permission was not granted. The map still works.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = packageName
        Configuration.getInstance().tileDownloadThreads = 2
        Configuration.getInstance().tileFileSystemCacheMaxBytes = 80L * 1024L * 1024L
        Configuration.getInstance().tileFileSystemCacheTrimBytes = 60L * 1024L * 1024L

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)
        binding.map.setBuiltInZoomControls(false)
        binding.map.controller.setZoom(4.5)
        binding.map.controller.setCenter(GeoPoint(39.8283, -98.5795))

        binding.myLocation.setOnClickListener {
            if (hasLocationPermission()) enableLocation()
            else requestLocationPermission()
        }

        addMarkers()

        if (hasLocationPermission()) enableLocation()
    }

    private fun addMarkers() {
        CameraRepository.all().forEach { site ->
            val marker = Marker(binding.map).apply {
                position = GeoPoint(site.latitude, site.longitude)
                title = site.label
                snippet = site.notes
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            binding.map.overlays.add(marker)
        }
        binding.map.invalidate()
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun enableLocation() {
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), binding.map)
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        binding.map.overlays.add(locationOverlay)
        binding.map.controller.setZoom(13.0)

        val fused = LocationServices.getFusedLocationProviderClient(this)
        val token = CancellationTokenSource()
        try {
            fused.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, token.token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        binding.map.controller.animateTo(
                            GeoPoint(location.latitude, location.longitude)
                        )
                    }
                }
        } catch (_: SecurityException) {
            // Permission can change while the request is running.
        }
        binding.map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        binding.map.onPause()
        super.onPause()
    }
}
