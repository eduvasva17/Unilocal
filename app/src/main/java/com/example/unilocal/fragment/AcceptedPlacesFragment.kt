package com.example.unilocal.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unilocal.R
import com.example.unilocal.adapter.PlaceAdapter
import com.example.unilocal.adapter.PlaceModeratorAdapter
import com.example.unilocal.databinding.FragmentAcceptedPlacesBinding
import com.example.unilocal.db.Places
import com.example.unilocal.model.Place
import com.example.unilocal.model.PlaceStatus


class AcceptedPlacesFragment : Fragment() {

    lateinit var  binding: FragmentAcceptedPlacesBinding
    private var listAcceptedPlaces:ArrayList<Place> = ArrayList()
    lateinit var adapter:PlaceModeratorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAcceptedPlacesBinding.inflate(inflater, container, false)

        listAcceptedPlaces = Places.ListByState(PlaceStatus.ACCEPTED)

        adapter = PlaceModeratorAdapter(listAcceptedPlaces)
        binding.listPlaces.adapter = adapter
        binding.listPlaces.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        return binding.root
    }
    override fun onResume() {
        super.onResume()
        listAcceptedPlaces = Places.ListByState(PlaceStatus.ACCEPTED)
        adapter = PlaceModeratorAdapter(listAcceptedPlaces)
        binding.listPlaces.adapter = adapter
        binding.listPlaces.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        Log.e("AcceptedPlacesFragment", "Se actualizo la lista")
    }




}