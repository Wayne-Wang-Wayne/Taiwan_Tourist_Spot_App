package com.example.taiwantouristspots.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taiwantouristspots.R
import com.example.taiwantouristspots.spotsmodel.Info
import com.example.taiwantouristspots.spotsmodel.FavoriteInfo
import com.example.taiwantouristspots.util.getProgressDrawable
import com.example.taiwantouristspots.util.loadImage
import com.example.taiwantouristspots.viewmodel.TouristSpotsAppViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


//景點詳細資訊Fragment
class TouristSpotsDetailFragment : Fragment() {

    //此為下面會用到的viewModel及傳遞進此Fragment的資料
    private val viewModel: TouristSpotsAppViewModel by activityViewModels()
    val args: TouristSpotsDetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tourist_spots_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //連接到天氣Fragment去的Button，並傳遞資料過去
        val weatherFloatingButton =
            view.findViewById<FloatingActionButton>(R.id.weatherFloatingButton)
        weatherFloatingButton.setOnClickListener {
            it.findNavController()
                .navigate(TouristSpotsDetailFragmentDirections.actionDetailToWeatherFragment(args.spotRegion))
        }

        val favInfo = FavoriteInfo(
            args.spotAddress,
            args.spotDescription,
            args.spotId,
            args.spotKeyword,
            args.spotName,
            args.spotOpenTime,
            args.spotRegion,
            args.spotTel,
            args.spotTravellingInfo,
            args.spotPictureUrl,
            ""
        )
        //到達我的最愛Fragment按鈕
        val favoriteListButton = view.findViewById<FloatingActionButton>(R.id.favoriteListButton)

        favoriteListButton.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(TouristSpotsDetailFragmentDirections.actionDetailToFavoriteFragment())
        }

        //加到我的最愛按鈕
        val addToMyFavButton = view.findViewById<Button>(R.id.addToMyFavButton)
        var clickTimes = 0
        addToMyFavButton.setOnClickListener {
            if (clickTimes < 1) {
                CoroutineScope(IO).launch {
                    viewModel.addFavoriteSpotToDB(favInfo)
                }
                addToMyFavButton.text = "已加到最愛！"
                clickTimes++
            }
        }

        val info = Info(
            args.spotAddress,
            args.spotDescription,
            args.spotId,
            args.spotKeyword,
            args.spotName,
            args.spotOpenTime,
            args.spotRegion,
            args.spotTel,
            args.spotTravellingInfo,
            args.spotPictureUrl
        )

        //創建此Fragment時更新ViewModel資料
        viewModel.refreshDetailData(info)
        observeViewModel(view)

        //按鈕設置，點擊地址可以透過Intent launch Google Map
        val tVDetailAddress = view.findViewById<TextView>(R.id.tVDetailAddress)

        tVDetailAddress.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${args.spotName}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)

        }
        //按鈕設置，點擊可以 make phone call
        val tVDetailPhoneNumber = view.findViewById<TextView>(R.id.tVDetailPhoneNumber)
        tVDetailPhoneNumber.setOnClickListener {
            val number = Uri.parse("tel:${args.spotTel}")
            val callIntent = Intent(Intent.ACTION_CALL, number)
            startActivity(callIntent)
        }
    }

    //observe View Model Live Data以及set UI介面的邏輯
    private fun observeViewModel(view: View) {
        val iVDetailSpotPic = view.findViewById<ImageView>(R.id.iVDetailSpotPic)
        val tVDetailSpotName = view.findViewById<TextView>(R.id.tVDetailSpotName)
        val tVDetailOpenTime = view.findViewById<TextView>(R.id.tVDetailOpenTime)
        val tVDetailPhoneNumber = view.findViewById<TextView>(R.id.tVDetailPhoneNumber)
        val tVDetailAddress = view.findViewById<TextView>(R.id.tVDetailAddress)
        val tVDetailTravelInfo = view.findViewById<TextView>(R.id.tVDetailTravelInfo)
        val tVDetailDescription = view.findViewById<TextView>(R.id.tVDetailDescription)


        viewModel.touristSpotDetail.observe(viewLifecycleOwner, Observer { Info ->
            Info?.let {
                tVDetailSpotName.text = Info.spotName
                tVDetailOpenTime.text = "開放時間：${Info.spotOpenTime}"
                tVDetailPhoneNumber.text = "電話：${Info.spotTel}"
                tVDetailAddress.text = "地址：${Info.spotAddress}"
                tVDetailTravelInfo.text = "交通資訊：${Info.spotTravellingInfo}"
                tVDetailDescription.text = "景點介紹：${Info.spotDescription}"
                iVDetailSpotPic.loadImage(Info.spotPictureUrl, getProgressDrawable(view.context))
            }
        })
    }
}