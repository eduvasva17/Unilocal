package com.example.unilocal.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unilocal.R
import com.example.unilocal.adapter.CommentsAdapter
import com.example.unilocal.databinding.FragmentCommentsPlaceBinding
import com.example.unilocal.db.Comments
import com.example.unilocal.model.Comment
import com.google.android.material.snackbar.Snackbar


class CommentsPlaceFragment : Fragment() {

    lateinit var binding: FragmentCommentsPlaceBinding
    var list:ArrayList<Comment> = ArrayList()
    var codePlace:Int = 0
    private lateinit var adapter: CommentsAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments != null){
            codePlace = requireArguments().getInt("id_place")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentsPlaceBinding.inflate(inflater, container, false)

        list = Comments.listById(codePlace)

        adapter = CommentsAdapter(list)
        binding.listComments.adapter = adapter
        binding.listComments.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        binding.sendButton.setOnClickListener { createComment() }


        return binding.root
    }

    fun createComment(){
        val text = binding.messageInput.text.toString()

        if(text.isNotEmpty()){

            val sp = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE)
            val idUser = sp.getInt("id_user", -1)

            var id: Int = Comments.getSize()
            id++
            val comment:Comment = Comment(id, text, idUser, 4, codePlace)
            Comments.new(comment)
            Snackbar.make(binding.root, "Mensaje Enviado", Snackbar.LENGTH_LONG).show()

            list.add(comment)
            adapter.notifyItemInserted(list.size-1)
            clearInputs()

        } else {
            Snackbar.make(binding.root, "Hay q escribir el mensaje", Snackbar.LENGTH_LONG).show()
        }
    }

    fun clearInputs(){
        binding.messageInput.setText("")
    }

    companion object{
        fun newInstance(codePlace: Int):CommentsPlaceFragment{
            val args = Bundle()
            args.putInt("id_place", codePlace)
            val fragment = CommentsPlaceFragment()
            fragment.arguments = args
            return fragment
        }
    }
}