package it.uninsubria.gmargherini.notescategories

import android.net.Uri


data class Note(
    var title:String,
    var category:String,
    var text:String,
    var image: String
) {
    constructor():this("title")
    constructor(title:String) : this(title,"category","","")
    constructor(title: String,category: String):this(title,category,"", "app/src/main/res/drawable-v24/kotlin_icon.png")

     fun exists(notes:List<Note>): Boolean {
         return notes.contains(this)
    }
}
