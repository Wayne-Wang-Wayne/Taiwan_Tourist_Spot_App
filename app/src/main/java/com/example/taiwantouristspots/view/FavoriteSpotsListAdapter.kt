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

class FavoriteSpotsListAdapter(val favList: ArrayList<FavoriteInfo>) :
    RecyclerView.Adapter<FavoriteSpotsListAdapter.FavoriteSpotsListViewHolder>() {


    class FavoriteSpotsListViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    fun updateTouristSpotsList(newList: List<FavoriteInfo>) {
        favList.clear()
        favList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteSpotsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.favorite_item_tourist_spot, parent, false)
        return FavoriteSpotsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteSpotsListViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.tVItemSpotName).text = favList[position].spotName
        holder.view.findViewById<TextView>(R.id.tVItemPhoneNumber).text = favList[position].spotTel
        holder.view.findViewById<TextView>(R.id.tVItemOpenTime).text =
            favList[position].spotOpenTime
        holder.view.findViewById<TextView>(R.id.tVItemAddress).text = favList[position].spotAddress
        holder.view.findViewById<ImageView>(R.id.iVItemSpotPic).loadImage(
            favList[position].spotPictureUrl,
            getProgressDrawable(holder.view.context)
        )
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

        //筆記editText
        holder.view.findViewById<EditText>(R.id.etNote).setText(favList[position].spotNote)
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