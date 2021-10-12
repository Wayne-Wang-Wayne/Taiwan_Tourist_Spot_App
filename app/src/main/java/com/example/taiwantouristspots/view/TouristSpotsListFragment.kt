package com.example.taiwantouristspots.view

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView;
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.taiwantouristspots.R
import com.example.taiwantouristspots.roomModel.TouristSpotsDatabase
import com.example.taiwantouristspots.spotsmodel.Info
import com.example.taiwantouristspots.viewmodel.TouristSpotsAppViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TouristSpotsListFragment : Fragment() {

    //下面會用到的view model 和 Adapter
    private val viewModel: TouristSpotsAppViewModel by activityViewModels()
    private val touristSpotsListAdapter = TouristSpotsListAdapter(arrayListOf())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tourist_spots_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val errorSign = view.findViewById<TextView>(R.id.listErrorSign)
        val loadingSign = view.findViewById<ProgressBar>(R.id.listLoadingSign)

        //set RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.touristSpotsListRecyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = touristSpotsListAdapter
        }

        //observe Live Data
        observeViewModel(view)

        //下拉可以從Api重新抓取最新資料的邏輯
        val listSwiper = view.findViewById<SwipeRefreshLayout>(R.id.listSwiper)
        listSwiper.setOnRefreshListener {
            recyclerView.visibility = View.GONE
            errorSign.visibility = View.GONE
            loadingSign.visibility = View.VISIBLE
            CoroutineScope(IO).launch {
                viewModel.refreshBypassCache()
            }
            listSwiper.isRefreshing = false
        }

        //到達我的最愛Fragment按鈕
        val favoriteListButton = view.findViewById<FloatingActionButton>(R.id.favoriteListButton)
        favoriteListButton.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(TouristSpotsListFragmentDirections.actionListToFavoriteFragment())
        }

        //隨機挑一個按鈕的邏輯
        val randomButton = view.findViewById<Button>(R.id.randomButton)
        randomButton.setOnClickListener {

            //從DB抓List資料出來
            CoroutineScope(IO).launch {
                val getDBList =
                    TouristSpotsDatabase(requireActivity().getApplicationContext()).spotsListDao()
                        .getAllSpotsList() ?: listOf()

                withContext(Main) {
                    //隨機抓個數字
                    val randomNumber = (getDBList.indices).random()
                    //帶著隨機資料到Detail Fragment去做顯示
                    Navigation.findNavController(it).navigate(
                        TouristSpotsListFragmentDirections.actionListToDetailFragment(
                            getDBList[randomNumber].spotAddress ?: "",
                            getDBList[randomNumber].spotDescription ?: "",
                            getDBList[randomNumber].spotId ?: "",
                            getDBList[randomNumber].spotKeyword ?: "",
                            getDBList[randomNumber].spotName ?: "",
                            getDBList[randomNumber].spotOpenTime ?: "",
                            getDBList[randomNumber].spotRegion ?: "",
                            getDBList[randomNumber].spotTel ?: "",
                            getDBList[randomNumber].spotTravellingInfo ?: "",
                            getDBList[randomNumber].spotPictureUrl ?: ""
                        )
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //刷新view model Live Data資料的方法
        CoroutineScope(IO).launch {
            viewModel.refreshListData()
        }
    }

    //此為主頁搜尋功能的邏輯
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val menuItem = menu!!.findItem(R.id.search)

        CoroutineScope(IO).launch {
            val getDBList =
                TouristSpotsDatabase(requireActivity().getApplicationContext()).spotsListDao()
                    .getAllSpotsList() ?: listOf()
            withContext(Main) {


                val originalList: List<Info> = getDBList
                val displayList: MutableList<Info> = getDBList.toMutableList()

                if (menuItem != null) {

                    val searchView = menuItem.actionView as SearchView

                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(p0: String?): Boolean {
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {

                            if (newText == null)
                                return true

                            if (newText!!.isNotEmpty()) {


                                displayList.clear()
                                val search = newText

                                //只要搜尋的字有被包含到就把他加到display List裡
                                originalList.forEach {
                                    if (search in it.spotRegion ?: "" ||
                                        it.spotAddress?.contains(search) ?: false ||
                                        it.spotDescription?.contains(search) ?: false ||
                                        it.spotKeyword?.contains(search) ?: false ||
                                        it.spotName?.contains(search) ?: false
                                    ) {
                                        displayList.add(it)
                                    }
                                }
                                touristSpotsListAdapter.updateTouristSpotsList(displayList as List<Info>)
                            } else {
                                displayList.clear()
                                displayList.addAll(originalList)
                                touristSpotsListAdapter.updateTouristSpotsList(displayList as List<Info>)
                            }
                            return true
                        }
                    })
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }


    // observe view model Live Data的方法，並set UI 介面
    private fun observeViewModel(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.touristSpotsListRecyclerView)
        val errorSign = view.findViewById<TextView>(R.id.listErrorSign)
        val loadingSign = view.findViewById<ProgressBar>(R.id.listLoadingSign)

        viewModel.touristSpotsList.observe(viewLifecycleOwner, Observer { touristSpots ->
            touristSpots?.let {
                recyclerView.visibility = View.VISIBLE
                touristSpotsListAdapter.updateTouristSpotsList(touristSpots)

            }
        })

        viewModel.listLoadError.observe(viewLifecycleOwner, Observer { isError ->
            isError?.let {
                errorSign.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.listLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                loadingSign.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    errorSign.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                }
            }
        })
    }


}