package com.example.autoconnect.dataclasses

// Data class to represent vehicle information
data class VehicleInfo2(
    val registrationNumber: String = "",
    val taxStatus: String = "",
    val taxDueDate: String = "",
    val motStatus: String = "",
    val make: String = "",
    val yearOfManufacture: Int = 0,
    val engineCapacity: Int = 0,
    val co2Emissions: Int = 0,
    val fuelType: String = "",
    val markedForExport: Boolean = false,
    val colour: String = "",
    val typeApproval: String = "",
    val dateOfLastV5CIssued: String = "",
    val motExpiryDate: String = "",
    val wheelplan: String = "",
    val monthOfFirstRegistration: String = ""
)