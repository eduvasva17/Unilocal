package com.example.unilocal.model

import java.time.LocalDateTime
import java.util.Date

class Comment(var id:Int,
              var text:String,
              var idUser:Int,
              var rating:Int,
              var idPlace: Int) {

    var date:Date = Date()
    override fun toString(): String {
        return "Comment(id=$id, text='$text', idUser=$idUser, rating=$rating, idPlace=$idPlace, date=$date)"
    }


}