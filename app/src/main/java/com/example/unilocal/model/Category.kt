package com.example.unilocal.model

class Category(var id:Int, var name:String, var icon:String) {

    override fun toString(): String {
        return "Category(id=$id, name='$name', icon='$icon')"
    }
}