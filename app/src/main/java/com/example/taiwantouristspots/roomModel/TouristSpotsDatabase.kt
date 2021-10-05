package com.example.taiwantouristspots.roomModel

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.taiwantouristspots.spotsmodel.Info
import com.example.taiwantouristspots.spotsmodel.FavoriteInfo

@Database(entities = [Info::class, FavoriteInfo::class], version = 1)
abstract class TouristSpotsDatabase : RoomDatabase() {

    abstract fun spotsListDao(): SpotsListDao
    abstract fun favoriteSpotsListDao(): FavoriteSpotsListDao

    companion object {
        @Volatile
        private var instance: TouristSpotsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            TouristSpotsDatabase::class.java,
            "touristSpotsAppDatabase"
        ).build()

    }

}