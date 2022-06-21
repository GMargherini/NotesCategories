package it.uninsubria.gmargherini.notescategories

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import it.uninsubria.gmargherini.notescategories.databinding.ActivityMainBinding
import it.uninsubria.gmargherini.notescategories.databinding.AlertDialogBinding
import java.lang.Exception
import kotlin.math.abs
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), View.OnTouchListener{
    private lateinit var binding:ActivityMainBinding
    private lateinit var detector: android.view.GestureDetector
    private val dbh:DBHelper= DBHelper(this)
    private var notes:List<Note> = ArrayList()
    private var categories:List<String> = ArrayList()
    private var currentCategory:String=""
    private var categoryButtons:ArrayList<Button> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //dbh.destroy()
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        detector=GestureDetector(this, GestureListener())
        checkCategories()

        binding.floatingActionButton.setOnClickListener {
            val layout = AlertDialogBinding.inflate(layoutInflater)
            AlertDialog.Builder(this)
                .setTitle("Nuova nota")
                .setView(layout.root)
                .setPositiveButton("CREA"){ dialog, which ->
                    val note=Note(layout.etTitolo.text.toString(), layout.etCategoria.text.toString())
                    notes=dbh.readNotes(note.category)
                    if(notes.contains(note)){
                        Toast.makeText(this,"La nota esiste già",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        dbh.insertNote(note)
                        checkCategories()
                    }
                    showNotes(note.category)
                    dialog.cancel()
                }
                .setNegativeButton("ANNULLA",DialogInterface.OnClickListener{ dialog, _ ->
                    dialog.cancel() })
                .show()
        }
        binding.listView.setOnTouchListener(this@MainActivity)
    }

    override fun onResume() {
        showNotes(currentCategory)
        super.onResume()
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
                    button.setTextColor(resources.getColor(R.color.false_white,theme))
                    button.setBackgroundResource(R.drawable.category_button)
                    button.text = i
                    button.setOnClickListener {
                        showNotes(i)
                        currentCategory=i
                    }
                    categoryButtons.add(button)
                    binding.linearLayout.addView(button)
                }
            }
        }
    }
    private fun showNotes(category:String){
        notes=dbh.readNotes(category)
        binding.listView.adapter=NotesListAdapter(this,notes)
        if (categoryButtons.isNotEmpty()) {
            for (button in categoryButtons) {
                if (button.text.toString() == category) {
                    button.setBackgroundResource(R.drawable.category_button_pressed)
                } else {
                    button.setBackgroundResource(R.drawable.category_button)
                }
            }
        }
    }
    override fun onTouch(view:View, event: MotionEvent?): Boolean {
        detector.onTouchEvent(event)
        return true
    }

    override fun onStart() {
        super.onStart()
        if (dbh.readCategories().isNotEmpty())
            currentCategory=dbh.readCategories()[0]
        checkCategories()
        if (currentCategory!="")
            showNotes(currentCategory)
    }
    private inner class GestureListener:GestureDetector.SimpleOnGestureListener(){
        val DELETE_THRESHOLD=100
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            var note:Note=Note()
            try {
                note = binding.listView.adapter.getItem(
                    binding.listView.pointToPosition(
                        e!!.x.roundToInt(),
                        e.y.roundToInt()
                    )
                ) as Note
            }catch (e:Exception){
                return true
            }
            val intent=Intent(this@MainActivity,EditorActivity::class.java)
            intent.putExtra("title",note.title)
            intent.putExtra("category",note.category)
            startActivity(intent)
            return true
        }
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            var note:Note=Note()
                try {
                    note = binding.listView.adapter.getItem(
                        binding.listView.pointToPosition(
                            e1!!.x.roundToInt(),
                            e1.y.roundToInt()
                        )
                    ) as Note
                }catch (e:Exception){
                    return true
                }

                if(abs(distanceX) >DELETE_THRESHOLD){
                    currentCategory=(note).category
                    dbh.deleteNote(note)
                    finish()
                    startActivity(intent)
                }
            return true
        }

        override fun onLongPress(p0: MotionEvent?) {
            val layout=AlertDialogBinding.inflate(layoutInflater)
            var note:Note=Note()
            try {
                note = binding.listView.adapter.getItem(
                    binding.listView.pointToPosition(
                        p0!!.x.roundToInt(),
                        p0.y.roundToInt()
                    )
                ) as Note
            }catch (e:Exception){}
            if(note!=Note()){
                AlertDialog.Builder(this@MainActivity,)
                    .setTitle("Modifica nota")
                    .setView(layout.root)
                    .setPositiveButton("MODIFICA") { dialog, which ->
                        val oldNote = note
                        val newNote=Note(layout.etTitolo.text.toString(), layout.etCategoria.text.toString(), oldNote.text, oldNote.image)
                        notes=dbh.readNotes(newNote.category)
                        if(notes.contains(newNote)){
                            Toast.makeText(this@MainActivity,"La nota esiste già",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            dbh.insertNote(newNote)
                            dbh.deleteNote(oldNote)
                        }
                        checkCategories()
                        showNotes(newNote.category)
                        dialog.cancel()

                    }
                    .setNegativeButton("ANNULLA",DialogInterface.OnClickListener{ dialog, which ->
                        dialog.cancel() })
                    .show()
            }

        }
    }
}
