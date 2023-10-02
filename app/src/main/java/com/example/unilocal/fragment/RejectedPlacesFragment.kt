package com.example.unilocal.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unilocal.R
import com.example.unilocal.adapter.PlaceAdapter
import com.example.unilocal.adapter.PlaceModeratorAdapter
import com.example.unilocal.databinding.FragmentRejectedPlacesBinding
import com.example.unilocal.db.Places
import com.example.unilocal.model.Place
import com.example.unilocal.model.PlaceStatus

class RejectedPlacesFragment : Fragment() {

    lateinit var binding: FragmentRejectedPlacesBinding
    private var listRejectedPlaces:ArrayList<Place> = ArrayList()
    lateinit var adapter:PlaceModeratorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRejectedPlacesBinding.inflate(inflater, container, false)

        listRejectedPlaces = Places.ListByState(PlaceStatus.REJECTED)

        adapter = PlaceModeratorAdapter(listRejectedPlaces)
        binding.listPlaces.adapter = adapter
        binding.listPlaces.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        listRejectedPlaces = Places.ListByState(PlaceStatus.REJECTED)
        adapter = PlaceModeratorAdapter(listRejectedPlaces)
        binding.listPlaces.adapter = adapter
        binding.listPlaces.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        Log.e("RejectedPlacesFragment", "Se actualizo la lista")
    }
}