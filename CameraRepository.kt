package com.example.flockavoid

object CameraRepository {
    private val sites = mutableListOf(
        CameraSite("1", 40.7128, -74.0060, "Example camera", "Sample seed data"),
        CameraSite("2", 40.7306, -73.9866, "Example camera 2", "Sample seed data")
    )

    fun all(): List<CameraSite> = sites.toList()

    fun add(site: CameraSite) {
        sites.add(site)
    }
}
