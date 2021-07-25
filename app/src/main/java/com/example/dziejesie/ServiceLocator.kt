package com.example.dziejesie

import com.example.dziejesie.repository.EventsRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ServiceLocator {
    private val collectionReference by lazy {
        Firebase.firestore.collection("All")
    }

    val repository: EventsRepository by lazy {
        EventsRepository(collectionReference)
    }
}