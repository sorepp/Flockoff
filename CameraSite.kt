package com.example.flockavoid

data class CameraSite(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val label: String,
    val notes: String = "",
    val reportedBy: String = "anonymous"
)
