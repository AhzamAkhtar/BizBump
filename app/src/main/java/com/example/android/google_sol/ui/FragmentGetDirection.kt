package com.example.android.google_sol.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.android.google_sol.R
import com.example.android.google_sol.databinding.FragmentGetDirectionBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class FragmentGetDirection : Fragment() , OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    val binding by lazy { FragmentGetDirectionBinding.inflate(layoutInflater) }
    lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapsdir) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        //geocoder = Geocoder(requireActivity(), Locale.getDefault())
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

    fun direction(){
        val requestQueue : RequestQueue =  Volley.newRequestQueue(requireActivity())
        val url : String = Uri.parse("https://maps.googleapis.com/maps/api/direction/json")
            .buildUpon()
            .appendQueryParameter("destination","-6.9218571 , 107.6048254")
            .appendQueryParameter("origin","-6.9249233 , 107.6345122")
            .appendQueryParameter("mode","driving")
            .appendQueryParameter("key","")
            .toString()

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, // or Request.Method.POST for a POST request
            url,
            null,
            Response.Listener { response ->
                // Handle the response here
                try{
                    val status : String = response.getString("status")
                    if(status.equals("OK")){
                        val routes : JSONArray = response.getJSONArray("routes")
                        var points: ArrayList<LatLng>
                        var polylineOptions: PolylineOptions? = null

                        for (i in 0 until routes.length()){
                            points = ArrayList()
                            polylineOptions = PolylineOptions()
                            val legs : JSONArray = routes.getJSONObject(i).getJSONArray("legs")

                            for (j in 0 until legs.length()){
                                val steps : JSONArray = legs.getJSONObject(j).getJSONArray("steps")

                                for (k in 0 until steps.length()){
                                    val polyline = steps.getJSONObject(k).getJSONObject("polyline").toString()
                                    val list : List<LatLng> = decodePoly(polyline)

                                    for (l in 0 until list.size){
                                        val position : LatLng = LatLng((list.get(l)).latitude , (list.get(l)).longitude)
                                        points.add(position)
                                    }
                                }
                            }

                            polylineOptions.addAll(points)
                            polylineOptions.width(10F)
                            polylineOptions.color(ContextCompat.getColor(requireActivity() , R.color.darkBlue))
                            polylineOptions.geodesic(true)
                        }
                        polylineOptions?.let { googleMap.addPolyline(it) }
                        googleMap.addMarker( MarkerOptions().position(LatLng(-6.9218571 , 107.6048254)).title("Marker 1"))
                        googleMap.addMarker( MarkerOptions().position(LatLng(-6.9249233 , 107.6345122)).title("Marker 2"))

                        val bounds : LatLngBounds = LatLngBounds.Builder()
                            .include(LatLng(-6.9218571 , 107.6048254))
                            .include(LatLng(-6.9249233 , 107.6345122)).build()

                        //val point : Point = Point()
                        //googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds))
                    }
                } catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                // Handle the error here
            }
        )

        val retryPolicy : RetryPolicy = DefaultRetryPolicy(30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES , DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        jsonObjectRequest.setRetryPolicy(retryPolicy)
        requestQueue.add(jsonObjectRequest)
    }

    fun decodePoly(encode: String): List<LatLng> {
        val poly: MutableList<LatLng> = mutableListOf()
        var index = 0
        val len = encode.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encode[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encode[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
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
                placeMarkerOnMap(currentLatLong)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 13f))

            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }

}