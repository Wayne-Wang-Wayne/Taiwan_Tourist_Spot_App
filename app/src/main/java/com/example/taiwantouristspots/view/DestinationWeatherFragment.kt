package com.example.taiwantouristspots.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.taiwantouristspots.R
import com.example.taiwantouristspots.retrofitmodel.RetrofitInstance
import com.example.taiwantouristspots.viewmodel.TouristSpotsAppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.w3c.dom.Text

//此為即時查詢當地縣市近兩天天氣的Fragment
class DestinationWeatherFragment : Fragment() {

    //需要用到的傳遞到這個Fragment的資料以及View Model
    val args: DestinationWeatherFragmentArgs by navArgs()
    private val viewModel: TouristSpotsAppViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_destination_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //在創建此Fragment時呼叫的update View Model資料的方法，並observe Live Data 的資料
        val location = args.spotLocation
        CoroutineScope(IO).launch {
            viewModel.refreshWeatherData(location)
        }
        observeViewModel(view)
    }

    //此為自寫的observe View Model方法，Set UI介面
    private fun observeViewModel(view: View) {

        viewModel.cityName.observe(viewLifecycleOwner, Observer { cityName ->
            val tVCityName = view.findViewById<TextView>(R.id.tVCityName)
            tVCityName.text = cityName

            if (cityName == "連江縣") {
                tVCityName.text = "無此地區天氣資料！"
            }
        })

        viewModel.locationWeather.observe(viewLifecycleOwner, Observer { locationWeather ->

            val weather1StartTime = locationWeather.weatherElement[0].time[0].startTime
            val weather1EndTime = locationWeather.weatherElement[0].time[0].endTime
            val weather2StartTime = locationWeather.weatherElement[0].time[1].startTime
            val weather2EndTime = locationWeather.weatherElement[0].time[1].endTime
            val weather3StartTime = locationWeather.weatherElement[0].time[2].startTime
            val weather3EndTime = locationWeather.weatherElement[0].time[2].endTime

            val tVWeatherTime1 = view.findViewById<TextView>(R.id.tVWeatherTime1)
            val tVWeatherTime2 = view.findViewById<TextView>(R.id.tVWeatherTime2)
            val tVWeatherTime3 = view.findViewById<TextView>(R.id.tVWeatherTime3)

            tVWeatherTime1.text = "$weather1StartTime 到 $weather1EndTime 的天氣："
            tVWeatherTime2.text = "$weather2StartTime 到 $weather2EndTime 的天氣："
            tVWeatherTime3.text = "$weather3StartTime 到 $weather3EndTime 的天氣："

            val weatherWx1 =
                locationWeather.weatherElement[0].time[0].parameter.parameterName
            val weatherWx2 =
                locationWeather.weatherElement[0].time[1].parameter.parameterName
            val weatherWx3 =
                locationWeather.weatherElement[0].time[2].parameter.parameterName

            val tVWeatherWx1 = view.findViewById<TextView>(R.id.tVWeatherWx1)
            val tVWeatherWx2 = view.findViewById<TextView>(R.id.tVWeatherWx2)
            val tVWeatherWx3 = view.findViewById<TextView>(R.id.tVWeatherWx3)

            tVWeatherWx1.text = "天氣：$weatherWx1"
            tVWeatherWx2.text = "天氣：$weatherWx2"
            tVWeatherWx3.text = "天氣：$weatherWx3"

            val weatherPoP1 = locationWeather.weatherElement[1].time[0].parameter.parameterName
            val weatherPoP2 = locationWeather.weatherElement[1].time[1].parameter.parameterName
            val weatherPoP3 = locationWeather.weatherElement[1].time[2].parameter.parameterName

            val tVWeatherPoP1 = view.findViewById<TextView>(R.id.tVWeatherPoP1)
            val tVWeatherPoP2 = view.findViewById<TextView>(R.id.tVWeatherPoP2)
            val tVWeatherPoP3 = view.findViewById<TextView>(R.id.tVWeatherPoP3)

            tVWeatherPoP1.text = "降雨機率：$weatherPoP1 % "
            tVWeatherPoP2.text = "降雨機率：$weatherPoP2 % "
            tVWeatherPoP3.text = "降雨機率：$weatherPoP3 % "

            val weatherMinT1 = locationWeather.weatherElement[2].time[0].parameter.parameterName
            val weatherMaxT1 = locationWeather.weatherElement[4].time[0].parameter.parameterName
            val weatherMinT2 = locationWeather.weatherElement[2].time[1].parameter.parameterName
            val weatherMaxT2 = locationWeather.weatherElement[4].time[1].parameter.parameterName
            val weatherMinT3 = locationWeather.weatherElement[2].time[2].parameter.parameterName
            val weatherMaxT3 = locationWeather.weatherElement[4].time[2].parameter.parameterName

            val tVWeatherMinTMaxT1 = view.findViewById<TextView>(R.id.tVWeatherMinTMaxT1)
            val tVWeatherMinTMaxT2 = view.findViewById<TextView>(R.id.tVWeatherMinTMaxT2)
            val tVWeatherMinTMaxT3 = view.findViewById<TextView>(R.id.tVWeatherMinTMaxT3)

            tVWeatherMinTMaxT1.text = "最低溫為：$weatherMinT1 度 \n最低溫為：$weatherMaxT1 度"
            tVWeatherMinTMaxT2.text = "最低溫為：$weatherMinT2 度 \n最低溫為：$weatherMaxT2 度"
            tVWeatherMinTMaxT3.text = "最低溫為：$weatherMinT3 度 \n最低溫為：$weatherMaxT3 度"

            val weatherCI1 = locationWeather.weatherElement[3].time[0].parameter.parameterName
            val weatherCI2 = locationWeather.weatherElement[3].time[1].parameter.parameterName
            val weatherCI3 = locationWeather.weatherElement[3].time[2].parameter.parameterName

            val tVWeatherCI1 = view.findViewById<TextView>(R.id.tVWeatherCI1)
            val tVWeatherCI2 = view.findViewById<TextView>(R.id.tVWeatherCI2)
            val tVWeatherCI3 = view.findViewById<TextView>(R.id.tVWeatherCI3)

            tVWeatherCI1.text = "溫度體感：$weatherCI1"
            tVWeatherCI2.text = "溫度體感：$weatherCI2"
            tVWeatherCI3.text = "溫度體感：$weatherCI3"
        }
        )
    }
}


