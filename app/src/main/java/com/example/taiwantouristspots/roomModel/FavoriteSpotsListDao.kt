package com.example.taiwantouristspots.roomModel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taiwantouristspots.spotsmodel.FavoriteInfo

@Dao
interface FavoriteSpotsListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(FavoriteInfo: List<FavoriteInfo>): List<Long>

    @Query("SELECT * FROM favoriteSpotsList")
    suspend fun getAllFavoriteSpotsList(): List<FavoriteInfo>

    @Query("SELECT * FROM favoriteSpotsList WHERE uuid = :favoriteSpotId")
    suspend fun getFavoriteSpot(favoriteSpotId: Int): FavoriteInfo

    @Query("DELETE FROM favoriteSpotsList")
    suspend fun deleteAllFavoriteSpots()
}