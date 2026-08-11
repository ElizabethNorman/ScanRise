package com.example.scanrise.data


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scan_objects",
    indices = [
        Index(
            value = ["barcodeValue"],
            unique = true
        )
    ]
)
data class ScanObjectEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val emoji: String,

    val barcodeValue: String,

    val barcodeFormat: Int
)