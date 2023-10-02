package com.example.unilocal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unilocal.R
import com.example.unilocal.db.Users
import com.example.unilocal.model.Comment

class CommentsAdapter(var list:ArrayList<Comment>): RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.comment_place, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(var commentsPlace: View):RecyclerView.ViewHolder(commentsPlace), OnClickListener{

        val owner:TextView = commentsPlace.findViewById(R.id.owner_name)
        val date:TextView = commentsPlace.findViewById(R.id.date)
        val commentX:TextView = commentsPlace.findViewById(R.id.comment)

        init {
            commentsPlace.setOnClickListener(this)
        }

        fun bind(comment: Comment) {
            owner.text = Users.findNameByID(comment.idUser)
            date.text = "00/00/00"
            commentX.text = comment.text
        }

        override fun onClick(p0: View?) {

        }



    }
}