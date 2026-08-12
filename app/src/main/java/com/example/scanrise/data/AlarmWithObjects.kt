package com.example.scanrise.data


import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class AlarmWithObjects(

    @Embedded
    val alarm: AlarmEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = AlarmObjectCrossRef::class,
            parentColumn = "alarmId",
            entityColumn = "objectId"
        )
    )
    val objects: List<ScanObjectEntity>
)