package com.example.dziejesie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dziejesie.databinding.ActivityDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

private const val REQUEST_EDIT_EVENT = 62;

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var filePath: String
    private lateinit var eventId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
        checkUser()
    }

    private fun checkUser() {
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        if (currentFirebaseUser?.email == binding.dUser.text) {
            binding.editButton.show()
            binding.deleteButton.show()
            setEditButton()
            setDeleteButton()
        }
    }

    private fun setView() {
        binding.editButton.hide()
        binding.deleteButton.hide()
        eventId = intent.getStringExtra("id").toString()
        binding.dDate.text = intent.getStringExtra("date")
        binding.dName.text = intent.getStringExtra("name")
        binding.dLoc.text = intent.getStringExtra("localization")
        binding.dDescription.text = intent.getStringExtra("description")
        binding.dUser.text = intent.getStringExtra("user")
        binding.lati.text = intent.getStringExtra("lati")
        binding.longt.text = intent.getStringExtra("long")
        filePath = intent.getStringExtra("file").toString()

        if (filePath.isNotEmpty()) {
            setImage()
        }
    }

    private fun setDeleteButton() = binding.deleteButton.setOnClickListener {
        ServiceLocator.repository.remove(eventId)
        val intent = Intent(this, MainActivity::class.java)
        startActivityForResult(
            intent, REQUEST_EDIT_EVENT
        )
    }

    private fun setEditButton() = binding.editButton.setOnClickListener {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("id", eventId)
        intent.putExtra("name", binding.dName.text)
        intent.putExtra("localization", binding.dLoc.text)
        intent.putExtra("file", filePath)
        intent.putExtra("date", binding.dDate.text)
        intent.putExtra("description", binding.dDescription.text)
        intent.putExtra("long", binding.longt.text)
        intent.putExtra("lati", binding.lati.text)
        startActivityForResult(
            intent, REQUEST_EDIT_EVENT
        )
    }

    private fun setImage() {
        val storageRef = FirebaseStorage.getInstance().reference
        val dateRef = storageRef.storage.getReferenceFromUrl(filePath)
        dateRef.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(binding.photo)
        }
    }
}