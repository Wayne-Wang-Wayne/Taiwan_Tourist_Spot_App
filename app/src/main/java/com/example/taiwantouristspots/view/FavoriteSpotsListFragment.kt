package com.example.taiwantouristspots.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taiwantouristspots.R
import com.example.taiwantouristspots.spotsmodel.FavoriteInfo
import com.example.taiwantouristspots.spotsmodel.Info
import com.example.taiwantouristspots.viewmodel.TouristSpotsAppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class FavoriteSpotsListFragment : Fragment() {


    private val viewModel: TouristSpotsAppViewModel by activityViewModels()

    private val touristSpotsListAdapter = FavoriteSpotsListAdapter(arrayListOf())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_spots_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.favoriteSpotsListRecyclerView)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = touristSpotsListAdapter
        }


        CoroutineScope(IO).launch {
            viewModel.updateFavLiveData()
        }

        observeViewModel(view)
    }

    private fun observeViewModel(view: View) {
        viewModel.favoriteSpotsList.observe(viewLifecycleOwner, Observer {
            touristSpotsListAdapter.updateTouristSpotsList(it)
        })
    }


}