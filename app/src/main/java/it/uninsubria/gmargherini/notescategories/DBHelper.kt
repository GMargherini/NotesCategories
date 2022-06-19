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
    }
    val TABLENAME="Notes"
    val COL_ID="id"
    val COL_TITLE="title"
    val COL_TEXT="text"
    val COL_IMAGE="image"
    val COL_CATEGORY="category"

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable="CREATE TABLE" + TABLENAME + "("+
                        COL_ID + "INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        COL_TITLE + "VARCHAR(128)," +
                        COL_CATEGORY + "VARCHAR(128)"+
                        COL_TEXT + "TEXT,"+
                        COL_IMAGE + "BLOB"+
                        ")"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, newVersion: Int, oldVersion: Int) {
        TODO("Not yet implemented")
    }

    fun readData(category: String):ArrayList<Note>{
        val list : ArrayList<Note> = ArrayList()
        val db=this.readableDatabase
        val query = "SELECT * FROM $TABLENAME" +
                "WHERE category=${category.lowercase()}"
        val cursor=db.rawQuery(query,null)
        val idIndex=cursor.getColumnIndex(COL_ID)
        val titleIndex= cursor.getColumnIndex(COL_TITLE)
        val categoryIndex= cursor.getColumnIndex(COL_CATEGORY)
        val textIndex= cursor.getColumnIndex(COL_TEXT)
        val imageIndex= cursor.getColumnIndex(COL_IMAGE)
        if(cursor.moveToFirst()){
            do {
                val note=Note(
                    cursor.getInt(idIndex),
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
    fun updateData(note:Note){
        val db=this.writableDatabase
        val contentValues=ContentValues()
        contentValues.put(COL_ID,note.id)
        contentValues.put(COL_TITLE,note.title)
        contentValues.put(COL_CATEGORY,note.category.lowercase())
        contentValues.put(COL_TEXT,note.text)
        contentValues.put(COL_IMAGE,note.image)
        db.update(TABLENAME,contentValues,"id=${note.id}",null)
    }

    fun deleteNote(note:Note){
        val db=this.writableDatabase
        val contentValues=ContentValues()
        contentValues.put(COL_ID,note.id)
        db.delete(TABLENAME,"id=${note.id}",null)
    }
}