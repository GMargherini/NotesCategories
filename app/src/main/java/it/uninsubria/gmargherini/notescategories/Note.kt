package it.uninsubria.gmargherini.notescategories


data class Note(
    var id:Int,
    var title:String,
    var category:String,
    var text:String,
    var image: String
) {
    constructor(title:String) : this(0,title,"category","","")
    constructor(title: String,category: String):this(0,title,category,"","")
}
