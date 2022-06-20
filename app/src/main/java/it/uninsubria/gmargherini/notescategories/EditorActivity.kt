package it.uninsubria.gmargherini.notescategories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import it.uninsubria.gmargherini.notescategories.databinding.ActivityEditorBinding
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
        val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            note.image = result.toString()
            dbh.updateNotes(note)
            note=dbh.readNote(note.title,note.category)
            setView(note)
        }
        binding.btnAddImage.setOnClickListener {
            resultLauncher.launch("*/*")
        }
    }

    override fun onBackPressed() {
        saveNote()
        super.onBackPressed()
    }

    override fun onDestroy() {
        saveNote()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putStringArrayList("note",saveNote())
        super.onSaveInstanceState(outState, outPersistentState)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val values:ArrayList<String>? = savedInstanceState.getStringArrayList("note")
        note=Note(values!![0],values[1],values[2],values[3])
    }
    private fun setView(note:Note){
        binding.tvTitle.text=note.title
        binding.tvCategory.text=note.category
        binding.etText.setText(note.text)
        try {
            if(note.image!="") {
                Glide.with(binding.imageView.context).load(note.image).into(binding.imageView)
            }
        }catch (e:Exception){}

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