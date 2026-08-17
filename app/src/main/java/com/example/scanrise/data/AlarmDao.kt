package com.example.scanrise.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AlarmDao {

    @Transaction
    @Query(
        """
        SELECT * 
        FROM alarms 
        ORDER BY hour, minute
        """
    )
    abstract fun getAllWithObjects(): Flow<List<AlarmWithObjects>>

    @Insert
    protected abstract suspend fun insertAlarm(
        alarm: AlarmEntity
    ): Long

    @Insert
    protected abstract suspend fun insertCrossRefs(
        crossRefs: List<AlarmObjectCrossRef>
    )

    @Update
    protected abstract suspend fun updateAlarm(
        alarm: AlarmEntity
    )

    @Query("DELETE FROM alarm_object_cross_ref WHERE alarmId = :alarmId")
    protected abstract suspend fun deleteCrossRefs(
        alarmId: Long
    )

    @Transaction
    open suspend fun insertAlarmWithObjects(
        alarm: AlarmEntity,
        objectIds: Set<Long>
    ): Long {

        val alarmId = insertAlarm(alarm)

        val crossRefs =
            objectIds.map { objectId ->
                AlarmObjectCrossRef(
                    alarmId = alarmId,
                    objectId = objectId
                )
            }

        insertCrossRefs(crossRefs)

        return alarmId
    }

    @Transaction
    open suspend fun updateAlarmWithObjects(
        alarm: AlarmEntity,
        objectIds: Set<Long>
    ) {
        updateAlarm(alarm)
        deleteCrossRefs(alarm.id)
        insertCrossRefs(
            objectIds.map { objectId ->
                AlarmObjectCrossRef(
                    alarmId = alarm.id,
                    objectId = objectId
                )
            }
        )
    }

    @Transaction
    @Query("SELECT * FROM alarms WHERE enabled = 1")
    abstract suspend fun getAllEnabledWithObjects(): List<AlarmWithObjects>

    @Query(
        """
        UPDATE alarms
        SET enabled = :enabled
        WHERE id = :alarmId
        """
    )
    abstract suspend fun setEnabled(
        alarmId: Long,
        enabled: Boolean
    )

    @Query(
        """
        DELETE FROM alarms
        WHERE id = :alarmId
        """
    )
    abstract suspend fun delete(
        alarmId: Long
    )

    @Transaction
    @Query("SELECT * FROM alarms WHERE id = :alarmId")
    abstract suspend fun getByIdWithObjects(
        alarmId: Long
    ): AlarmWithObjects?
}
