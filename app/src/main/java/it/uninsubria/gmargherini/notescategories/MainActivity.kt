package it.uninsubria.gmargherini.notescategories

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.marginEnd
import androidx.core.view.setMargins
import it.uninsubria.gmargherini.notescategories.databinding.ActivityMainBinding
import it.uninsubria.gmargherini.notescategories.databinding.ListRowBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private val dbh:DBHelper= DBHelper(this)
    private var notes:List<Note> = ArrayList()
    private var categories:List<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //dbh.destroy()
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        checkCategories()
        binding.floatingActionButton.setOnClickListener {
            val editTexts= ListRowBinding.inflate(layoutInflater)
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
                    }
                    binding.linearLayout.addView(button)
                }
            }
        }
    }
    private fun showNotes(category:String){
        val data=ArrayList<HashMap<String,Any>>()

        notes=dbh.readNotes(category)
        if(notes.isNotEmpty()) {
            for(i in notes.indices){
                val item=HashMap<String,Any>()
                item["title"]=notes[i].title
                data.add(item)
            }
            binding.listView.adapter = SimpleAdapter(
                this, data, android.R.layout.simple_list_item_2,
                arrayOf("title"), intArrayOf(android.R.id.text1)
            )
            println(notes)
        }
    }
}