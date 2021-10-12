package com.example.taiwantouristspots.spotsmodel

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

//主要介面個別景點的class，也是操作Retrofit抓取Api資料所需的class，也是此資料存在Room DB的Entity
@Entity(tableName = "SpotsList")
data class Info(
    @SerializedName("Add")
    val spotAddress: String?,
    @SerializedName("Description")
    val spotDescription: String?,
    @SerializedName("Id")
    val spotId: String?,
    @SerializedName("Keyword")
    val spotKeyword: String?,
    @SerializedName("Name")
    val spotName: String?,
    @SerializedName("Opentime")
    val spotOpenTime: String?,
    @SerializedName("Region")
    val spotRegion: String?,
    @SerializedName("Tel")
    val spotTel: String?,
    @SerializedName("Travellinginfo")
    val spotTravellingInfo: String?,
    @SerializedName("Picture1")
    val spotPictureUrl: String?
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
}