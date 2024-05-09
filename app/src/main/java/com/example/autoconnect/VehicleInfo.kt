package com.example.autoconnect

import java.io.Serializable

// Data class to represent vehicle information
data class VehicleInfo(
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
    var motExpiryDate: String = "",
    val wheelplan: String = "",
    val monthOfFirstRegistration: String = "",
    var insuranceExpiry: String = "",
    var serviceDue: String = "",
    var model: String = "",
    var odometer: String = "0",
    var odometerUnit: String = "mi"
): Serializable