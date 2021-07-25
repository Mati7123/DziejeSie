package com.example.dziejesie.repository

import com.example.dziejesie.models.Event
import com.google.firebase.firestore.CollectionReference

class EventsRepository(
    val collection: CollectionReference,
) {

    fun add(event: Event) {
        collection.document(event.id).set(event.fields)
    }

    fun getAll(onResponse: (List<Event>) -> Unit) {
        collection.get().addOnSuccessListener { querySnapshot ->
            querySnapshot.documents.mapNotNull { documentSnapshot ->
                documentSnapshot.data?.let { fields ->
                    Event.fromDocumentFields(fields)
                }
            }.let { onResponse(it) }
        }
    }

    fun remove(id: String) {
        collection.document(id).delete()
    }

}