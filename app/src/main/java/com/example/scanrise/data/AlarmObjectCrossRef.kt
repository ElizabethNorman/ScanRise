package com.example.scanrise.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "alarm_object_cross_ref",
    primaryKeys = ["alarmId", "objectId"],
    foreignKeys = [
        ForeignKey(
            entity = AlarmEntity::class,
            parentColumns = ["id"],
            childColumns = ["alarmId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ScanObjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["objectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("alarmId"),
        Index("objectId")
    ]
)
data class AlarmObjectCrossRef(
    val alarmId: Long,
    val objectId: Long
)