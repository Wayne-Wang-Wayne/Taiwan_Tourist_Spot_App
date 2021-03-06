package com.example.taiwantouristspots.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.taiwantouristspots.R
import com.example.taiwantouristspots.spotsmodel.FavoriteInfo
import com.example.taiwantouristspots.spotsmodel.Info
import com.example.taiwantouristspots.util.getProgressDrawable
import com.example.taiwantouristspots.util.loadImage

//主頁面Fragment的RecyclerView Adapter
class TouristSpotsListAdapter(val infoList: ArrayList<Info>) :
    RecyclerView.Adapter<TouristSpotsListAdapter.TouristSpotsViewHolder>() {

    class TouristSpotsViewHolder(var view: View) : RecyclerView.ViewHolder(view)

    //更新List information的方法
    fun updateTouristSpotsList(newTouristSpotsList: List<Info>) {
        infoList.clear()
        infoList.addAll(newTouristSpotsList)
        notifyDataSetChanged()
    }


    //以item_tourist_spot為layout set到子recyclerView上
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TouristSpotsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_tourist_spot, parent, false)
        return TouristSpotsViewHolder(view)
    }

    //以此邏輯set到UI介面上
    override fun onBindViewHolder(holder: TouristSpotsViewHolder, position: Int) {
        //set Text
        holder.view.findViewById<TextView>(R.id.tVItemSpotName).text = infoList[position].spotName
        holder.view.findViewById<TextView>(R.id.tVItemPhoneNumber).text = infoList[position].spotTel
        holder.view.findViewById<TextView>(R.id.tVItemOpenTime).text =
            infoList[position].spotOpenTime
        holder.view.findViewById<TextView>(R.id.tVItemAddress).text = infoList[position].spotAddress

        //set 圖片
        holder.view.findViewById<ImageView>(R.id.iVItemSpotPic).loadImage(
            infoList[position].spotPictureUrl,
            getProgressDrawable(holder.view.context)
        )
        //set OnclickListener到達Detail Fragment並傳遞資料過去
        holder.view.setOnClickListener {
            Navigation.findNavController(it).navigate(
                TouristSpotsListFragmentDirections.actionListToDetailFragment(
                    infoList[position].spotAddress ?: "",
                    infoList[position].spotDescription ?: "",
                    infoList[position].spotId ?: "",
                    infoList[position].spotKeyword ?: "",
                    infoList[position].spotName ?: "",
                    infoList[position].spotOpenTime ?: "",
                    infoList[position].spotRegion ?: "",
                    infoList[position].spotTel ?: "",
                    infoList[position].spotTravellingInfo ?: "",
                    infoList[position].spotPictureUrl ?: ""
                )
            )
        }

    }

    override fun getItemCount() = infoList.size


}