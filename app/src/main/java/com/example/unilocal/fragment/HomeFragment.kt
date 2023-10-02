package com.example.unilocal.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unilocal.R
import com.example.unilocal.adapter.PlaceAdapter
import com.example.unilocal.databinding.FragmentAcceptedPlacesBinding
import com.example.unilocal.databinding.FragmentAllAcceptedPlacesBinding
import com.example.unilocal.db.Places
import com.example.unilocal.model.Place
import com.example.unilocal.model.PlaceStatus

class HomeFragment : Fragment() {

    lateinit var  binding: FragmentAllAcceptedPlacesBinding
    private var listAcceptedPlaces:ArrayList<Place> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllAcceptedPlacesBinding.inflate(inflater, container, false)

        listAcceptedPlaces = Places.ListByState(PlaceStatus.ACCEPTED)

        val adapter = PlaceAdapter(listAcceptedPlaces)
        binding.listPlaces.adapter = adapter
        binding.listPlaces.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        return binding.root
        //return inflater.inflate(R.layout.fragment_home, container, false)
    }
}