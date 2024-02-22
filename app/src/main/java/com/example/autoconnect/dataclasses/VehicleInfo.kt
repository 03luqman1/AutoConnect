package com.example.autoconnect.dataclasses

// Data class to represent vehicle information
data class VehicleInfo(
    val registrationNumber: String,
    val taxStatus: String,
    val taxDueDate: String,
    val motStatus: String,
    val make: String,
    val yearOfManufacture: Int,
    val engineCapacity: Int,
    val co2Emissions: Int,
    val fuelType: String,
    val markedForExport: Boolean,
    val colour: String,
    val typeApproval: String,
    val dateOfLastV5CIssued: String,
    val motExpiryDate: String,
    val wheelplan: String,
    val monthOfFirstRegistration: String
)