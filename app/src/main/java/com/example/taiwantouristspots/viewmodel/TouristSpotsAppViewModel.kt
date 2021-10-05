package com.example.taiwantouristspots.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taiwantouristspots.retrofitmodel.RetrofitInstance
import com.example.taiwantouristspots.roomModel.TouristSpotsDatabase
import com.example.taiwantouristspots.spotsmodel.Info
import com.example.taiwantouristspots.spotsmodel.FavoriteInfo
import com.example.taiwantouristspots.util.SharedPreferencesHelper
import com.example.taiwantouristspots.weathermodel.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TouristSpotsAppViewModel(application: Application) : AndroidViewModel(application) {

    val touristSpotsList: LiveData<List<Info>> = MutableLiveData<List<Info>>()
    val listLoadError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val listLoading: LiveData<Boolean> = MutableLiveData<Boolean>()
    private val prefHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime = 60 * 24 * 60 * 1000 * 1000 * 1000L


    suspend fun refreshListData() {
        val updateTime = prefHelper.getUpdateTime()
        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchFromDatabase()
        } else {
            fetchFromRemote()
        }

    }

    suspend fun refreshBypassCache() {
        fetchFromRemote()
    }

    suspend fun fetchFromDatabase() {
        CoroutineScope(IO).launch {
            val spotsList = TouristSpotsDatabase(getApplication()).spotsListDao().getAllSpotsList()
            withContext(Main) {
                (touristSpotsList as MutableLiveData<List<Info>>).value = spotsList
                (listLoadError as MutableLiveData<Boolean>).value = false
                (listLoading as MutableLiveData<Boolean>).value = false

                Toast.makeText(
                    getApplication(),
                    "Information retrieved from Database",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    suspend fun fetchFromRemote() {
        CoroutineScope(IO).launch {
            val newList =
                RetrofitInstance.spotsApi.getSpots().body()?.XML_Head?.Infos?.Info ?: listOf()
            storeSpotsListLocally(newList)
            withContext(Main) {
                (touristSpotsList as MutableLiveData<List<Info>>).value = newList
                (listLoadError as MutableLiveData<Boolean>).value = false
                (listLoading as MutableLiveData<Boolean>).value = false

                Toast.makeText(getApplication(), "Information retrieve from API", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }


    suspend fun storeSpotsListLocally(list: List<Info>) {
        CoroutineScope(IO).launch {
            val spotsListDao = TouristSpotsDatabase(getApplication()).spotsListDao()
            spotsListDao.deleteAllSpots()
            val spotsResult = spotsListDao.insertAll(list)
            for (i in list.indices) {
                list[i].uuid = spotsResult[i].toInt()
            }
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }


    val touristSpotDetail: LiveData<Info> = MutableLiveData<Info>()

    fun refreshDetailData(info: Info) {
        (touristSpotDetail as MutableLiveData<Info>).value = info
    }


    val locationWeather: LiveData<Location> = MutableLiveData<Location>()
    val cityName: LiveData<String> = MutableLiveData<String>()

    suspend fun refreshWeatherData(location: String) {

        CoroutineScope(IO).launch {
            val weatherList = RetrofitInstance.getWeather().body()?.records?.location

            withContext(Main) {
                val certainLocation = weatherList?.find {
                    it.locationName == location
                }
                if (certainLocation != null) {
                    (locationWeather as MutableLiveData<Location>).value = certainLocation
                }
                (cityName as MutableLiveData<String>).value = location
            }

        }

    }

    val favoriteSpotsList: LiveData<List<FavoriteInfo>> =
        MutableLiveData<List<FavoriteInfo>>()

    suspend fun addFavoriteSpotToDB(FavoriteInfo: FavoriteInfo) {
        CoroutineScope(IO).launch {
            val favSpotsList = TouristSpotsDatabase(getApplication()).favoriteSpotsListDao()
                .getAllFavoriteSpotsList().toMutableList()
            favSpotsList.add(FavoriteInfo)
            TouristSpotsDatabase(getApplication()).favoriteSpotsListDao().insertAll(favSpotsList)
        }
    }

    suspend fun updateFavLiveData() {
        CoroutineScope(IO).launch {
            val favSpotsList = TouristSpotsDatabase(getApplication()).favoriteSpotsListDao()
                .getAllFavoriteSpotsList()

            withContext(Main) {
                (favoriteSpotsList as MutableLiveData<List<FavoriteInfo>>).value = favSpotsList
            }
        }
    }




}


