package com.example.unilocal.model

class Schedule(var id:Int,
               var weekDay:ArrayList<WeekDay>,
               var startTime:Int,
               var closingTime:Int) {

    override fun toString(): String {
        return "Schedule(id=$id, weekDay=$weekDay, startTime=$startTime, closingTime=$closingTime)"
    }
}