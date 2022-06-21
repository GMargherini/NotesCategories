package it.uninsubria.gmargherini.notescategories

data class Note(
    var title:String,
    var category:String,
    var text:String,
    var image: String
) {
    constructor():this("title")
    constructor(title:String) : this(title,"category","","")
    constructor(title: String,category: String):this(title,category,"", "")

    override fun equals(other: Any?): Boolean {
        return this.title==(other as Note).title && this.category==other.category
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + category.hashCode()
        return result
    }
}
