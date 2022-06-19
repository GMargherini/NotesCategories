package it.uninsubria.gmargherini.notescategories

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import it.uninsubria.gmargherini.notescategories.databinding.ActivityMainBinding
import it.uninsubria.gmargherini.notescategories.databinding.AlertDialogBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private val dbh:DBHelper= DBHelper(this)
    private var notes:List<Note> = ArrayList()
    private var categories:List<String> = ArrayList()
    private var currentCategory:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //dbh.destroy()
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        checkCategories()
        binding.floatingActionButton.setOnClickListener {
            val editTexts= AlertDialogBinding.inflate(layoutInflater)
            var ad=AlertDialog.Builder(this)
                .setTitle("Nuova nota")
                .setView(editTexts.root)
                .setPositiveButton("CREA",DialogInterface.OnClickListener{ dialog,which->
                    dbh.insertNote(Note(editTexts.etTitolo.text.toString(),editTexts.etCategoria.text.toString()))
                    checkCategories()
                    showNotes(editTexts.etCategoria.toString())
                    dialog.cancel()
                })
                .setNegativeButton("ANNULLA",DialogInterface.OnClickListener{ dialog, which ->
                    dialog.cancel()
                })
                .show()
        }
        binding.listView.setOnItemClickListener { parent, view, pos, id ->
            val intent=Intent(this,EditorActivity::class.java)
            intent.putExtra("title",view.findViewById<TextView>(R.id.tv_title).text.toString())
            intent.putExtra("category",currentCategory)
            startActivity(intent)
        }
    }
    private fun checkCategories(){
        val prev=categories
        categories=dbh.readCategories().distinct().toList()
        if(categories.isNotEmpty()) {
            for (i in categories) {
                if (i != "" && !prev.contains(i)) {
                    val button = Button(this)
                    button.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    (button.layoutParams as LinearLayout.LayoutParams).setMargins(0,0,24,0)
                    button.setBackgroundResource(R.color.purple_light)
                    button.setTextColor(Color.WHITE)
                    button.text = i
                    button.setOnClickListener {
                        showNotes(i)
                        currentCategory=i
                    }
                    binding.linearLayout.addView(button)
                }
            }
        }
    }
    private fun showNotes(category:String){
        notes=dbh.readNotes(category)
        binding.listView.adapter=NotesListAdapter(this,notes)
    }
}