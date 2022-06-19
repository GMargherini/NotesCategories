package it.uninsubria.gmargherini.notescategories


data class Note(
    var id:Int,
    var title:String,
    var category:String,
    var text:String,
    var image: String
) {

}
