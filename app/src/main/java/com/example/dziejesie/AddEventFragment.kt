package com.example.dziejesie

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.dziejesie.adapters.EventsAdapter
import com.example.dziejesie.databinding.FragmentAddBinding
import com.example.dziejesie.models.Event
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddEventFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var longitude: Double? = null
    private var latitude: Double? = null
    private var eventId: String? = null
    private lateinit var userid: String
    private lateinit var filePath: String
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return FragmentAddBinding.inflate(
            inflater, container, false
        ).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        eventsAdapter = EventsAdapter(ServiceLocator.repository, requireActivity())
        setupAddButton()
        setupPhotoButton()
        setupLocalizationButton()

        eventId = requireActivity().intent.getStringExtra("id")
        if (eventId != null) {
            setViews()
        }

        setUser()

        binding.addDate.setOnClickListener(View.OnClickListener { showDateDialog(binding.addDate) })
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    private fun setUser() {
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        if (currentFirebaseUser != null) {
            userid = currentFirebaseUser.email!!
        } else {
            userid = "UnknownUser"
        }
    }

    private fun setViews() {
        binding.addDate.setText(requireActivity().intent.getStringExtra("date"))
        binding.addName.setText(requireActivity().intent.getStringExtra("name"))
        binding.addLocalization.setText(requireActivity().intent.getStringExtra("localization"))
        binding.addDescription.setText(requireActivity().intent.getStringExtra("description"))
        binding.longt.text = requireActivity().intent.getStringExtra("long")
        binding.lati.text = requireActivity().intent.getStringExtra("lati")
        filePath = requireActivity().intent.getStringExtra("file").toString()
        latitude = requireActivity().intent.getStringExtra("lati")?.toDoubleOrNull()
        longitude = requireActivity().intent.getStringExtra("long")?.toDoubleOrNull()
        if (filePath.isNotEmpty()) {
            setImage()
        }
    }

    private fun setImage() {
        val storageRef = FirebaseStorage.getInstance().reference
        val dateRef = storageRef.storage.getReferenceFromUrl(filePath)
        dateRef.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(binding.photo)
        }
    }

    private fun setupLocalizationButton() = binding.buttonLocalization.setOnClickListener {
        if (ContextCompat.checkSelfPermission(
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
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        longitude = location.longitude
                        latitude = location.latitude
                        binding.lati.text = latitude.toString()
                        binding.longt.text = longitude.toString()
                    }
                }
        }
    }

    private fun setupPhotoButton() = binding.photoButton.setOnClickListener {
        dispatchTakePictureIntent()
    }



    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.photo.setImageBitmap(imageBitmap)
        }
    }

    private fun setupAddButton() = binding.saveButton.setOnClickListener {
        if (eventId == null) {
            eventId = userid + binding.addName.text.toString() + binding.addLocalization.text.toString() + binding.addDate.text.toString()
        }
        var filePath = ""
        if (binding.photo.drawable != null) {
            sendPhotoToStorage()
            filePath = "gs://dziejesie-7.appspot.com/" + eventId + ".jpg"
        }

        val event = Event(
            eventId.toString(),
            binding.addName.text.toString(),
            binding.addLocalization.text.toString(),
            binding.addDate.text.toString(),
            binding.addDescription.text.toString(),
            filePath,
            userid,
            longitude,
            latitude
        )
        eventsAdapter.addEvent(event)
        eventId = null
    }

    private fun showDateDialog(add_date: EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = month
                calendar[Calendar.DAY_OF_MONTH] = day
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                add_date.setText(simpleDateFormat.format(calendar.time))
            }

        context?.let {
            DatePickerDialog(
                it, dateSetListener,
                calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    private fun sendPhotoToStorage() {
        var storageRef = Firebase.storage.reference
        val photoRef = storageRef.child(eventId + ".jpg")
        val baos = ByteArrayOutputStream()
        val drawable = binding.photo.getDrawable() as BitmapDrawable
        var bitmap = drawable.bitmap
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()
        photoRef.putBytes(data)
    }
}