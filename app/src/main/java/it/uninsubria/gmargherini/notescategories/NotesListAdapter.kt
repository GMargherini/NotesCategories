package it.uninsubria.gmargherini.notescategories

import android.app.ActionBar
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.bumptech.glide.Glide
import java.lang.Exception

class NotesListAdapter(private val context: Context, private val notesList:List<Note>): BaseAdapter() {
    override fun getCount(): Int {
        return notesList.count()
    }

    override fun getItem(pos: Int): Any {
        return notesList[pos]
    }

    override fun getItemId(pos: Int): Long {
        val note=notesList[pos]
        return (note.title + note.category).hashCode().toLong()
    }

    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        var newView=convertView
        if(newView==null)
            newView=LayoutInflater.from(context).inflate(R.layout.layout_row_item,parent,false)
        val note = notesList[pos]
        try {
            val imageView=newView?.findViewById<ImageView>(R.id.image_view)!!
            imageView.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
            Glide.with(context).load(note.image).into(imageView)
        }catch (e:Exception){
            newView!!.findViewById<ImageView>(R.id.image_view)
        }

        newView!!.findViewById<TextView>(R.id.tv_title).text=note.title
        newView.findViewById<TextView>(R.id.tv_text).text=note.text
        return newView
    }
}