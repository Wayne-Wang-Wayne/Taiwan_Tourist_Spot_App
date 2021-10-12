package com.example.taiwantouristspots.roomModel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.taiwantouristspots.spotsmodel.Info


//主頁所有景點資料Room Database的Dao
@Dao
interface SpotsListDao {
    @Insert
    suspend fun insertAll(infos: List<Info>): List<Long>

    @Query("SELECT * FROM SpotsList")
    suspend fun getAllSpotsList(): List<Info>

    @Query("SELECT * FROM SpotsList WHERE uuid = :spotId")
    suspend fun getSpot(spotId: Int): Info

    @Query("DELETE FROM SpotsList")
    suspend fun deleteAllSpots()


}