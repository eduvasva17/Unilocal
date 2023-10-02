package com.example.unilocal.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unilocal.R
import com.example.unilocal.adapter.PlaceAdapter
import com.example.unilocal.databinding.FragmentProfileBinding
import com.example.unilocal.db.Places
import com.example.unilocal.model.Place

class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private var listPlacesByOwner:ArrayList<Place> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val sp = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE)
        val idUser = sp.getInt("id_user", -1)
        if( idUser != -1 ) {
            listPlacesByOwner = Places.ListByOwner(idUser)

            val adapter = PlaceAdapter(listPlacesByOwner)
            binding.listPlaces.adapter = adapter
            binding.listPlaces.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }

        return binding.root
    }

}