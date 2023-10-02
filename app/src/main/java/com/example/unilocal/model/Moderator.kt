package com.example.unilocal.model

class Moderator(idUser:Int,
                name:String,
                last_name:String,
                email:String,
                var nickname:String,
                password:String,
                idCountry:Int,
                idDepartment:Int,
                idCity:Int,
                age:Int,
                phone: String): Person(idUser, name, last_name, email, password, idCountry, idDepartment, idCity, age, phone) {

    override fun toString(): String {
        return super.toString()
    }
}