package it.uninsubria.gmargherini.notescategories

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.widget.ImageView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import it.uninsubria.gmargherini.notescategories.databinding.ActivityEditorBinding
import java.io.FileNotFoundException
import java.lang.Exception

class EditorActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditorBinding
    private val dbh=DBHelper(this)
    lateinit var note:Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEditorBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        val notes=dbh.readNotes(intent.getStringExtra("category")!!)
        for(i in notes){
            if(i.title == intent.getStringExtra("title")!!){
                note=i
                break
            }
        }
        setView(note)

    }

    override fun onBackPressed() {
        saveNote()
        super.onBackPressed()

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putStringArrayList("note",saveNote())
        super.onSaveInstanceState(outState, outPersistentState)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val values:ArrayList<String>? = savedInstanceState.getStringArrayList("note")
        note=Note(values!![0],values!![1],values!![2],values!![3])
    }
    private fun setView(note:Note){
        binding.tvTitle.text=note.title
        binding.tvCategory.text=note.category
        binding.etText.setText(note.text)
        val imageView=ImageView(this)
        try {
            Glide.with(this).load(note.image).into(imageView)
        }catch (e:Exception){
            Glide.with(this).load("app/src/main/res/drawable-v24/kotlin_icon.png").into(imageView)
        }
        binding.clOuter.addView(imageView)
    }
    private fun saveNote():ArrayList<String>{
        binding.etText.clearFocus()
        note.text=binding.etText.text.toString()
        dbh.updateNotes(note)
        val values:ArrayList<String> = ArrayList()
        values.add(note.title)
        values.add(note.category)
        values.add(note.text)
        values.add(note.image)
        return values
    }
}