package com.example.listtodoapp.Model

import com.google.firebase.database.Exclude

data class Task(
    var uid: String?=null,
    var title:String?=null,
    var description:String?=null,
    var date:Long= 0L,
    var time:Long=0L,
    var isFinished : Int = 0
)
{

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "title" to title,
            "description" to description,
            "date" to date,
            "time" to time,
            "isFinished" to isFinished
        )
    }
}