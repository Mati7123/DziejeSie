package com.example.dziejesie.models

data class Event(
    val id: String,
    val name: String,
    val localization: String,
    val date: String,
    val description: String,
    val filePath: String,
    val user: String,
    val longitude: Double?,
    val latitude: Double?


) {
    val fields = mapOf(
        "id" to id,
        "name" to name,
        "localization" to localization,
        "date" to date,
        "description" to description,
        "filePath" to filePath,
        "user" to user,
        "longitude" to longitude,
        "latitude" to latitude
    )

    companion object {
        fun fromDocumentFields(map: Map<String, Any>): Event {
            return Event(
                map["id"].toString(),
                map["name"].toString(),
                map["localization"].toString(),
                map["date"].toString(),
                map["description"].toString(),
                map["filePath"].toString(),
                map["user"].toString(),
                map["longitude"] as Double?,
                map["latitude"] as Double?

            )
        }
    }
}