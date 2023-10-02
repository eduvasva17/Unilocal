package com.example.unilocal.db

import com.example.unilocal.model.Person

object People {

    fun login(email:String, password:String): Person?{
        var result:Person?

        result = Users.list().firstOrNull { u -> u.password == password && u.email == email }

        if(result == null){
            result = Moderators.list().firstOrNull { u -> u.password == password && u.email == email }
            if(result == null){
                result = Administrators.list().firstOrNull { u -> u.password == password && u.email == email }
            }
        }
        return result
    }
}