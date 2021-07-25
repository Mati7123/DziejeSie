package com.example.dziejesie

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.example.dziejesie.models.Event
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapEventFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    private val events = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment =
            childFragmentManager.fragments[0] as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1
            )
        } else {
            map.isMyLocationEnabled = true
        }

        ServiceLocator.repository.getAll {
            events.apply {
                clear()
                addAll(it)
            }
            for (event in events) {
                if (event.latitude != null && event.longitude != null) {
                    drawCircle(LatLng(event.latitude, event.longitude))
                    addMarker(LatLng(event.latitude, event.longitude), event.name)
                }
            }
        }
    }

    private fun drawCircle(lating: LatLng) {
        val circle = CircleOptions()
            .strokeColor(Color.RED)
            .radius(500f.toDouble())
            .center(lating)
            .strokeWidth(10f)
        map.apply {
            addCircle(circle)
        }
    }

    private fun addMarker(lating: LatLng, eventName: String) {
        map.addMarker(
            MarkerOptions()
                .position(lating)
                .title(eventName)
        )
    }
}