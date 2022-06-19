package it.uninsubria.gmargherini.notescategories

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.collections.ArrayList


class DBHelper(var context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        const val DATABASE_NAME: String="notesApp.db"
        const val DATABASE_VERSION: Int=1
        val TABLE_NOTES="Notes"
        val COL_TITLE="title"
        val COL_TEXT="text"
        val COL_IMAGE="image"
        val COL_CATEGORY="category"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableNotes=
            "CREATE TABLE " + TABLE_NOTES + "("+
                COL_TITLE + " VARCHAR(128)," +
                COL_CATEGORY + " VARCHAR(128),"+
                COL_TEXT + " TEXT,"+
                COL_IMAGE + " VARCHAR(256),"+
                "PRIMARY KEY($COL_TITLE,$COL_CATEGORY)"+
            ")"
        db?.execSQL(createTableNotes)
    }

    override fun onUpgrade(db: SQLiteDatabase?, newVersion: Int, oldVersion: Int) {
        //TODO("Not yet implemented")
        db!!.execSQL("DROP TABLE IF EXISTS "+TABLE_NOTES)
        onCreate(db)
    }

    fun readNotes(category: String):ArrayList<Note>{
        val list : ArrayList<Note> = ArrayList()
        val db=this.readableDatabase
        val query =  COL_CATEGORY+" = '"+category.lowercase()+"'"
        val cursor=db.query(TABLE_NOTES,null,query,null,null,null,null)
        val titleIndex= cursor.getColumnIndex(COL_TITLE)
        val categoryIndex= cursor.getColumnIndex(COL_CATEGORY)
        val textIndex= cursor.getColumnIndex(COL_TEXT)
        val imageIndex= cursor.getColumnIndex(COL_IMAGE)
        if(cursor.moveToFirst()){
            do {
                val note=Note(
                    cursor.getString(titleIndex),
                    cursor.getString(categoryIndex),
                    cursor.getString(textIndex),
                    cursor.getString(imageIndex)
                )
                list.add(note)
            }while(cursor.moveToNext())
        }
        return list
    }
    fun readCategories():ArrayList<String>{
        val list:ArrayList<String> = ArrayList()
        val db=this.readableDatabase
        val query = "SELECT $COL_CATEGORY FROM $TABLE_NOTES"
        val cursor=db.rawQuery(query,null)
        val categoryIndex= cursor.getColumnIndex(COL_CATEGORY)
        if(cursor.moveToFirst()){
            do {
                val category = cursor.getString(categoryIndex)
                list.add(category)
            }while(cursor.moveToNext())
        }
        return list
    }
    fun insertNote(note:Note){
        val db=this.writableDatabase
        val contentValues=ContentValues()
        contentValues.put(COL_TITLE,note.title)
        contentValues.put(COL_CATEGORY,note.category.lowercase())
        contentValues.put(COL_TEXT,note.text)
        contentValues.put(COL_IMAGE,note.image)
        db.insert(TABLE_NOTES,null,contentValues)
    }
    fun updateNotes(note:Note){
        val db=this.writableDatabase
        val contentValues=ContentValues()
        contentValues.put(COL_TITLE,note.title)
        contentValues.put(COL_CATEGORY,note.category.lowercase())
        contentValues.put(COL_TEXT,note.text)
        contentValues.put(COL_IMAGE,note.image)
        db.update(TABLE_NOTES,contentValues,"$COL_TITLE='${note.title}' AND $COL_CATEGORY ='${note.category}' ",null)
    }

    fun deleteNote(note:Note){
        val db=this.writableDatabase
        val contentValues=ContentValues()
        contentValues.put(COL_TITLE,note.title)
        contentValues.put(COL_CATEGORY,note.category)
        db.delete(TABLE_NOTES,"$COL_TITLE='${note.title}' AND $COL_CATEGORY ='${note.category}' ",null)
    }
    fun destroy(){
        val db=this.writableDatabase
        db!!.execSQL("DROP TABLE IF EXISTS "+TABLE_NOTES)
        onCreate(db)
    }
}