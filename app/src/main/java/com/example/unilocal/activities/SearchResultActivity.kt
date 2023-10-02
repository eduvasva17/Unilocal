package com.example.unilocal.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unilocal.R
import com.example.unilocal.adapter.PlaceAdapter
import com.example.unilocal.databinding.ActivitySearchResultBinding
import com.example.unilocal.db.Places
import com.example.unilocal.model.Place

class SearchResultActivity : AppCompatActivity() {

    lateinit var binding:ActivitySearchResultBinding
    var searchText:String = ""
    lateinit var listPlaces:ArrayList<Place>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchText = intent.extras!!.getString("text", "")
        listPlaces = ArrayList()

        if(searchText.isNotEmpty()){
            listPlaces = Places.findByName(searchText)
        }

        val adapter = PlaceAdapter(listPlaces)
        binding.listPlaces.adapter = adapter
        binding.listPlaces.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

    }
}