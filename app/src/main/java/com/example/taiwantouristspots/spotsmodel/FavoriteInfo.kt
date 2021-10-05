package com.example.taiwantouristspots.spotsmodel

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favoriteSpotsList")
data class FavoriteInfo(
    val spotAddress: String?,
    val spotDescription: String?,
    val spotId: String?,
    val spotKeyword: String?,
    val spotName: String?,
    val spotOpenTime: String?,
    val spotRegion: String?,
    val spotTel: String?,
    val spotTravellingInfo: String?,
    val spotPictureUrl: String?
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
}