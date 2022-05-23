package cr.ac.ubicacioncurso

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import cr.ac.ubicacioncurso.databinding.ActivityMapsBinding
import android.Manifest
import cr.ac.ubicacioncurso.db.LocationDatabase
import cr.ac.ubicacioncurso.entity.Location

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val SOLICITA_GPS = 1
    private lateinit var mLocationClient: FusedLocationProviderClient //Proveedor de localizacion de google
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var locationDatabase: LocationDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        locationDatabase = LocationDatabase.getInstance(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                if (mMap != null) {
                    if (locationResult.equals(null))
                        return

                }// Dibujar en el Mapa los Puntos
                for (location in locationResult.locations) {
                    val sydney = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

                    locationDatabase.locationDao.insert(Location(null,location.latitude,location.latitude))
                }

            }

        }
        mLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 500
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getLocation()
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
        var locations = locationDatabase.locationDao.query()
        for (location in locations){
            val sydney = LatLng(location.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }

    fun getLocation() {
        // Tengo permisos de gps?

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //No tengo permisos
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                SOLICITA_GPS
            )

        }else {
            mLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback, null
            )
        }

    }
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SOLICITA_GPS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationClient.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback, null
                    )

                } else {//el usuario no dio permisos
                    System.exit(1)
                }
            }
        }
    }
}

