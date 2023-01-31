package com.example.android.google_sol.ui
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.example.android.google_sol.DataClass.SellerDto
import com.example.android.google_sol.R
import com.example.android.google_sol.daos.SellerViewModal
import com.example.android.google_sol.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.ktx.markerClickEvents


class MainActivity : AppCompatActivity()  , OnMapReadyCallback , GoogleMap.OnMarkerClickListener{
    lateinit var googleMap : GoogleMap
    private lateinit var lastLocation : Location
    private val db = FirebaseFirestore.getInstance()
    private val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}
    private val viewModel : SellerViewModal by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
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

    private fun setUpMap(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(this , arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )

            return
        }
        googleMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) {location ->
            if(location!=null){
                lastLocation = location
                val currentLatLong = LatLng(location.latitude , lastLocation.longitude)
                placeMarkerOnMap(currentLatLong)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong,13f))

            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        //googleMap.addMarker(markerOptions.)
    }

    private fun showBottomSheet(headingTextInput : String , subTextInput : String){
        val dialog = BottomSheetDialog(this)
        val view =  layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)
        val orderButton = view.findViewById<Button>(R.id.btnToOrderNow)
        val headingText = view.findViewById<TextView>(R.id.headText)
        val subText = view.findViewById<TextView>(R.id.subText)
        headingText.text = headingTextInput
        subText.text = subTextInput
        orderButton.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun fetchDataFromFirebase(){
         db.collection("vendors")
            .get()
            .addOnSuccessListener { result ->
                for (document in result){
                    viewModel.setSellerData(
                        SellerDto(
                            document.getString("Name").toString(),
                            document.getString("Type").toString(),
                            document.getString("Lat")!!.toString(),
                            document.getString("Lng")!!.toString()
                        )
                    )
                    setData()
                    //Log.d("Data",viewModel.sellerData.toString())
                }
            }
            .addOnFailureListener{exception ->
                Log.d("Error","Error getting Document",exception)
            }
    }



    private fun setData(){
        viewModel.sellerData.observe(this){
            val modal = it as SellerDto
            //Log.d("Name",modal.Name)
            val latitude = modal.Lat.toDouble()
            val longitude = modal.Lng.toDouble()
            val directions = LatLng(latitude,longitude)
            val name  = modal.Name
            val type = modal.Type
            viewModel.setSellerDisplayForBottomNavigation(
                SellerDto(
                    modal.Name,
                    modal.Type,
                    modal.Lat,
                    modal.Lng
                )
            )
            googleMap.addMarker(MarkerOptions().position(directions).title(modal.Name+  modal.Type))
            googleMap.setOnMarkerClickListener { marker ->
                Log.d("Position",marker.position.latitude.toString())
                db.collection("vendors")
                    .whereEqualTo("Lat",marker.position.latitude.toString())
                    .get()
                    .addOnSuccessListener { documents->
                        for(document in documents){
                            val Name = document.getString("Name")
                            Toast.makeText(this,Name.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                //showBottomSheet(name,type)
                false
            }

        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }

}
