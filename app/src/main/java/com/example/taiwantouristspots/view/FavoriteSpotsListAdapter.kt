package com.example.taiwantouristspots.view

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.taiwantouristspots.R
import com.example.taiwantouristspots.roomModel.TouristSpotsDatabase
import com.example.taiwantouristspots.spotsmodel.FavoriteInfo
import com.example.taiwantouristspots.util.getProgressDrawable
import com.example.taiwantouristspots.util.loadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


//此為我的最愛Fragment的 recyclerView adapter
class FavoriteSpotsListAdapter(val favList: ArrayList<FavoriteInfo>) :
    RecyclerView.Adapter<FavoriteSpotsListAdapter.FavoriteSpotsListViewHolder>() {


    class FavoriteSpotsListViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    //刷新list資料的function
    fun updateTouristSpotsList(newList: List<FavoriteInfo>) {
        favList.clear()
        favList.addAll(newList)
        notifyDataSetChanged()
    }

    //以favorite_item_tourist_spot為layout set到子recyclerView上
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteSpotsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.favorite_item_tourist_spot, parent, false)
        return FavoriteSpotsListViewHolder(view)
    }

    //以此邏輯set到UI介面上
    override fun onBindViewHolder(holder: FavoriteSpotsListViewHolder, position: Int) {

        //set Text
        holder.view.findViewById<TextView>(R.id.tVItemSpotName).text = favList[position].spotName
        holder.view.findViewById<TextView>(R.id.tVItemPhoneNumber).text = favList[position].spotTel
        holder.view.findViewById<TextView>(R.id.tVItemOpenTime).text =
            favList[position].spotOpenTime
        holder.view.findViewById<TextView>(R.id.tVItemAddress).text = favList[position].spotAddress

        //set圖片
        holder.view.findViewById<ImageView>(R.id.iVItemSpotPic).loadImage(
            favList[position].spotPictureUrl,
            getProgressDrawable(holder.view.context)
        )
        //set OnClickListener 傳遞資料到下一個Fragment
        holder.view.findViewById<LinearLayout>(R.id.linearLayout1).setOnClickListener {
            Navigation.findNavController(it).navigate(
                FavoriteSpotsListFragmentDirections.actionFavoriteToDetailFragment(
                    favList[position].spotAddress ?: "",
                    favList[position].spotDescription ?: "",
                    favList[position].spotId ?: "",
                    favList[position].spotKeyword ?: "",
                    favList[position].spotName ?: "",
                    favList[position].spotOpenTime ?: "",
                    favList[position].spotRegion ?: "",
                    favList[position].spotTel ?: "",
                    favList[position].spotTravellingInfo ?: "",
                    favList[position].spotPictureUrl ?: ""
                )
            )
        }
        //set OnLongClickListener實現刪除功能
        holder.view.findViewById<LinearLayout>(R.id.linearLayout1).setOnLongClickListener {
            val addAlertDialog = AlertDialog.Builder(holder.view.context)
                .setTitle("確定要刪除？")
                .setMessage("您確定要刪除此景點？")
                .setIcon(R.drawable.ic_action_delete)
                .setPositiveButton("是！") { dialogInterface, i ->

                    favList.removeAt(position)
                    notifyDataSetChanged()

                    CoroutineScope(IO).launch {
                        TouristSpotsDatabase(holder.view.context).favoriteSpotsListDao()
                            .deleteAllFavoriteSpots()
                        TouristSpotsDatabase(holder.view.context).favoriteSpotsListDao()
                            .insertAll(favList)
                    }
                }
                .setNegativeButton("我不要") { _, _ ->
                }.create()
            addAlertDialog.show()
            true
        }

        //筆記功能 editText 邏輯
        holder.view.findViewById<EditText>(R.id.etNote).setText(favList[position].spotNote)

        //儲存自訂筆記到Room DB 按鈕
        holder.view.findViewById<Button>(R.id.etButton).setOnClickListener {
            val newNote = holder.view.findViewById<EditText>(R.id.etNote).text.toString()
            favList[position].spotNote = newNote
            notifyDataSetChanged()
            CoroutineScope(IO).launch {
                TouristSpotsDatabase(holder.view.context).favoriteSpotsListDao().insertAll(favList)
            }
        }
    }

    override fun getItemCount() = favList.size
}