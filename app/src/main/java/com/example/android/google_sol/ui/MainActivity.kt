package com.example.android.google_sol.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.android.google_sol.DataClass.SellerDto
import com.example.android.google_sol.R
import com.example.android.google_sol.daos.SellerViewModal
import com.example.android.google_sol.databinding.LayoutBottomSheetBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private val binding by lazy { LayoutBottomSheetBinding.inflate(layoutInflater) }
    lateinit var googleMap: GoogleMap
    private var hashMap: HashMap<String, String> = HashMap<String, String>()
    private lateinit var lastLocation: Location
    private val db = FirebaseFirestore.getInstance()
    private val viewModel: SellerViewModal by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        //binding.progressBar.visibility = View.GONE

        fetchDataFromFirebase()
    }

    override fun onMapReady(GoogleMap: GoogleMap) {
        googleMap = GoogleMap

        val location1 = LatLng(28.551652534321633, 77.30066167895848)
        //googleMap.addMarker(MarkerOptions().position(location1).title("My Location"))

        val location2 = LatLng(28.56059429542479, 77.31594540563349)
        //googleMap.addMarker(MarkerOptions().position(location2).title("My Location2"))

        googleMap.setOnMarkerClickListener(this)
        setUpMap()

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )

            return
        }
        googleMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, lastLocation.longitude)
                placeMarkerOnMap(currentLatLong)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 13f))
                hashMap["userLat"] = currentLatLong.latitude.toString()
                hashMap["userLng"] = currentLatLong.longitude.toString()
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
    }

    private fun showBottomSheet(
        headingTextInput: String,
        subTextInput: String,
        subTextAddress: String,
        profileUrl: String
    ) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)

        val distance = getDistanceBTSellerAnsUser(
            hashMap["sellerLat"].toString(),
            hashMap["sellerLng"].toString(),
            hashMap["userLat"].toString(),
            hashMap["userLng"].toString()
        )
        //Toast.makeText(this,distance.toString(),Toast.LENGTH_SHORT).show()
        val distanceTextView = view.findViewById<TextView>(R.id.distanceText)
        distanceTextView.text = "Distance"+ "-" +(distance/1000).toString().subSequence(0,3)+"Km"
        val orderButton = view.findViewById<Button>(R.id.btnToOrderNow)
        val headingText = view.findViewById<TextView>(R.id.headText)
        val subText = view.findViewById<TextView>(R.id.subText)
        val subAddress = view.findViewById<TextView>(R.id.tvAddress)
        val userImage = view.findViewById<ImageView>(R.id.ivUserProfile)
        headingText.text = headingTextInput
        subText.text = subTextInput
        subAddress.text = subTextAddress
        if (userImage != null) {
            Glide.with(this)
                .load(profileUrl)
                .into(userImage)
        }

        orderButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun fetchDataFromFirebase() {
        db.collection("vendors")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    viewModel.setSellerData(
                        SellerDto(
                            document.getString("Name").toString(),
                            document.getString("Type").toString(),
                            document.getString("Lat")!!.toString(),
                            document.getString("Lng")!!.toString()
                        )
                    )
                    setData()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "Error getting Document", exception)
            }
    }


    private fun setData() {
        viewModel.sellerData.observe(this) {
            val modal = it as SellerDto
            val latitude = modal.Lat.toDouble()
            val longitude = modal.Lng.toDouble()
            val directions = LatLng(latitude, longitude)
            viewModel.setSellerDisplayForBottomNavigation(
                SellerDto(
                    modal.Name,
                    modal.Type,
                    modal.Lat,
                    modal.Lng
                )
            )
            googleMap.addMarker(MarkerOptions().position(directions).title(modal.Name + modal.Type))
            addDataToBottomSheet()

        }
    }

    private fun addDataToBottomSheet() {
        googleMap.setOnMarkerClickListener { marker ->
            val address = getSellerAddress(
                marker.position.latitude.toString(),
                marker.position.longitude.toString()
            )
            hashMap["sellerLat"] = marker.position.latitude.toString()
            hashMap["sellerLng"] = marker.position.longitude.toString()
            Log.d("Position", marker.position.latitude.toString())
            db.collection("vendors")
                .whereEqualTo("Lat", marker.position.latitude.toString())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val Name = document.getString("Name")
                        val Type = document.getString("Type")
                        val UserImage = document.getString("ProfileUrl")

                        if (Name != null) {
                            //binding.progressBar.visibility = View.VISIBLE
                            showBottomSheet(Name, Type.toString(), address, UserImage.toString())
                            //binding.progressBar.visibility = View.GONE
                        }
                    }

                }
            false
        }
    }

    private fun getSellerAddress(latitude: String, longitude: String): String {
        val addressGeocoder = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
        val fullAddress = addressGeocoder?.get(0)?.getAddressLine(0)
        Log.d("Address", fullAddress.toString())
        return fullAddress.toString()
    }

    private fun getDistanceBTSellerAnsUser(
        sellerLat: String,
        sellerLng: String,
        userLat: String,
        userLng: String
    ): Double {
        val startLocation = Location("location1")
        startLocation.latitude = userLat.toDouble()
        startLocation.longitude = userLng.toDouble()

        val endLocation = Location("location2")
        endLocation.latitude = sellerLat.toDouble()
        endLocation.longitude = sellerLng.toDouble()

        return startLocation.distanceTo(endLocation).toDouble()
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }

}
