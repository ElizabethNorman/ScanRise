package com.example.scanrise.data


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanObjectDao {

    @Query("SELECT * FROM scan_objects ORDER BY name")
    fun getAll(): Flow<List<ScanObjectEntity>>


    @Query("SELECT * FROM scan_objects WHERE barcodeValue = :barcode LIMIT 1")
    suspend fun getByBarcode(barcode: String): ScanObjectEntity?

    @Query("SELECT * FROM scan_objects WHERE id = :id")
    suspend fun getById(id: Long): ScanObjectEntity?

    @Query("DELETE FROM scan_objects")
    suspend fun deleteAll()

    @Insert
    suspend fun insert(scanObject: ScanObjectEntity): Long

    @Update
    suspend fun update(scanObject: ScanObjectEntity)

    @Delete
    suspend fun delete(scanObject: ScanObjectEntity)
}