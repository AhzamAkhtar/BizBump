package com.example.android.google_sol.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.android.google_sol.R
import com.example.android.google_sol.databinding.ActivityMainBinding
import com.example.android.google_sol.util.*
import com.example.android.google_sol.util.CheckoutDTO.AddressDTO
import com.example.android.google_sol.util.CheckoutDTO.SellerInfoDTO
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList


class FragmentMainScreen : Fragment() , OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var googleMap: GoogleMap
    private var hashMap: HashMap<String, String> = HashMap<String, String>()
    private lateinit var lastLocation: Location
    private val db = FirebaseFirestore.getInstance()
    private val viewModel: SellerViewModal by activityViewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.maps) as SupportMapFragment
//        mapFragment.getMapAsync(this)

        val mapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())
        //binding.progressBar.visibility = View.GONE

        fetchDataFromFirebase()
        binding.cards1.setOnClickListener{
            setDataForVegetableOnly()
        }

        binding.cards2.setOnClickListener{
            setDataForFruitsOnly()

        }

        viewModel.userDetails.observe(requireActivity()){
            val modal = it as UserDetailsDto
            val name = modal.userName
            val profileUrl = modal.photoUrl
            val email = modal.userEmail
            hashMap["userName"] = name
            hashMap["userEmail"] = email
            binding.headingCard.text = name
            Glide.with(requireActivity())
                .load(profileUrl)
                .placeholder(R.drawable.arrow)
                .into(binding.ivUserProfile)
        }


    }

    override fun onMapReady(GoogleMap: GoogleMap) {
        googleMap = GoogleMap

        val location1 = LatLng(28.551652534321633, 77.30066167895848)
        //googleMap.addMarker(MarkerOptions().position(location1).title("My Location"))

        val location2 = LatLng(28.56059429542479, 77.31594540563349)
        //googleMap.addMarker(MarkerOptions().position(location2).title("My Location2"))

        //googleMap.setOnMarkerClickListener(requireivity()::class.java)
        setUpMap()

    }



      private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )

            return
        }
        googleMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, lastLocation.longitude)
                val currentUserAddress = getSellerAddress(location.latitude.toString(),location.longitude.toString())
                viewModel.setAddressForCheckout(
                    AddressDTO(
                        currentUserAddress
                    )
                )
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
        profileUrl: String,
        open:String,
        phoneNumber:String,
    ) {
        val dialog = BottomSheetDialog(requireActivity())
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)

        val distance = getDistanceBTSellerAnsUser(
            hashMap["sellerLat"].toString(),
            hashMap["sellerLng"].toString(),
            hashMap["userLat"].toString(),
            hashMap["userLng"].toString()
        )
        //Toast.makeText(requireActivity(),distance.toString(),Toast.LENGTH_SHORT).show()
        val distanceTextView = view.findViewById<TextView>(R.id.distanceText)
        val distanceFinal = "Distance"+ "-" +(distance/1000).toString().subSequence(0,3)+"Km"
        distanceTextView.text = distanceFinal
        val orderButton = view.findViewById<Button>(R.id.btnToOrderNow)
        val headingText = view.findViewById<TextView>(R.id.headText)
        val subText = view.findViewById<TextView>(R.id.subText)
        val openText = view.findViewById<TextView>(R.id.openText)
        val subAddress = view.findViewById<TextView>(R.id.tvAddress)
        val userImage = view.findViewById<ImageView>(R.id.ivUserProfile)
        val callImage = view.findViewById<ImageView>(R.id.ivCall)
        callImage.setOnClickListener{
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + phoneNumber)
            startActivity(dialIntent)
        }
        headingText.text = headingTextInput
        subText.text = subTextInput
        subAddress.text = subTextAddress
        if(open=="Open"){
            openText.text = open
            openText.setTextColor(Color.parseColor("#80ed99"))
        }
        if(open=="Closed"){
            openText.text = open
            openText.setTextColor(Color.parseColor("#d90429"))
        }
        if (userImage != null) {
            Glide.with(requireActivity())
                .load(profileUrl)
                .into(userImage)
        }

        viewModel.setDataForBuying(
            BuyingDTO(
                headingTextInput,
                subTextInput,
                open,
                subTextAddress,
                distanceFinal
            )
        )

        orderButton.setOnClickListener {
            dialog.dismiss()
            viewModel.setScreenState(MainActivity.BUYING_SCREEN)
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
                            document.getString("Lng")!!.toString(),
                            document.getString("open").toString()
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
        viewModel.sellerData.observe(requireActivity()) {
            val modal = it as SellerDto
            val latitude = modal.Lat.toDouble()
            val longitude = modal.Lng.toDouble()
            val directions = LatLng(latitude, longitude)
            googleMap.addMarker(MarkerOptions().position(directions).title(modal.Name + modal.Type))
            addDataToBottomSheet()

        }
    }

    private fun setDataForVegetableOnly(){
        googleMap.clear()
        db.collection("vendors")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    viewModel.setSellerData(
                        SellerDto(
                            document.getString("Name").toString(),
                            document.getString("Type").toString(),
                            document.getString("Lat")!!.toString(),
                            document.getString("Lng")!!.toString(),
                            document.getString("open").toString()
                        )
                    )
                    HelpergetOnlyVegtableSeller()

                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "Error getting Document", exception)
            }
    }

    private fun setDataForFruitsOnly(){
        googleMap.clear()
        db.collection("vendors")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    viewModel.setSellerData(
                        SellerDto(
                            document.getString("Name").toString(),
                            document.getString("Type").toString(),
                            document.getString("Lat")!!.toString(),
                            document.getString("Lng")!!.toString(),
                            document.getString("open").toString()
                        )
                    )
                    HelpergetOnlyFruitSeller()

                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "Error getting Document", exception)
            }
    }

    private fun HelpergetOnlyVegtableSeller(){
       ///////
        Toast.makeText(requireActivity(),"ok",Toast.LENGTH_LONG).show()
        viewModel.sellerData.observe(requireActivity()){
            val modal = it as SellerDto
            if(modal.Type=="Vegetable Seller"){
                val latitude = modal.Lat.toDouble()
                val longitude = modal.Lng.toDouble()
                val directions = LatLng(latitude, longitude)
                googleMap.addMarker(MarkerOptions().position(directions).title(modal.Name + modal.Type))
                addDataToBottomSheet()
                Toast.makeText(requireActivity(),"ok",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun HelpergetOnlyFruitSeller(){
        viewModel.sellerData.observe(requireActivity()){
            val modal = it as SellerDto
            if(modal.Type=="Fruits Seller"){
                val latitude = modal.Lat.toDouble()
                val longitude = modal.Lng.toDouble()
                val directions = LatLng(latitude, longitude)
                googleMap.addMarker(MarkerOptions().position(directions).title(modal.Name + modal.Type))
                addDataToBottomSheet()
                Toast.makeText(requireActivity(),"ok",Toast.LENGTH_LONG).show()
            }
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
                        val open  = document.getString("open")
                        val phoneNumber = document.get("phoneNumber")


                        if (Name != null) {
                            //binding.progressBar.visibility = View.VISIBLE
                            showBottomSheet(Name, Type.toString(), address, UserImage.toString(),open.toString(),phoneNumber.toString())
                            viewModel.setSellerDataForCheckout(
                                SellerInfoDTO(
                                    Name,
                                    phoneNumber.toString()
                                )
                            )
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