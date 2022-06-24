package it.uninsubria.gmargherini.notescategories

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.core.view.children
import it.uninsubria.gmargherini.notescategories.databinding.ActivityMainBinding
import it.uninsubria.gmargherini.notescategories.databinding.AlertDialogBinding
import java.lang.Exception
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), View.OnTouchListener{
    private lateinit var binding:ActivityMainBinding
    private lateinit var detector: GestureDetector
    private val dbh:DBHelper= DBHelper(this)
    private var notes:List<Note> = ArrayList()
    private var categories:List<String> = ArrayList()
    private var currentCategory:String=""
    private var categoryButtons:ArrayList<Button> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        detector=GestureDetector(this, GestureListener())
        binding.floatingActionButton.setOnClickListener {
            val layout = AlertDialogBinding.inflate(layoutInflater)
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.new_note))
                .setView(layout.root)
                .setPositiveButton(getString(R.string.create)){ dialog, _ ->
                    val note=Note(layout.etTitolo.text.toString(), layout.etCategoria.text.toString())
                    notes=dbh.readNotes(note.category)
                    if(notes.contains(note)){
                        Toast.makeText(this,getString(R.string.note_already_exists),Toast.LENGTH_SHORT).show()
                    }
                    else if (note.title==""){
                        Toast.makeText(this,getString(R.string.insert_title),Toast.LENGTH_SHORT).show()
                    }
                    else if (note.category==""){
                        Toast.makeText(this,getString(R.string.insert_category),Toast.LENGTH_SHORT).show()
                    }
                    else{
                        dbh.insertNote(note)
                        onStart()
                    }
                    showNotes(note.category)
                    dialog.cancel()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }
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
            for (category in categories) {
                if (category != "" && !prev.contains(category)) {
                    val button = Button(this)
                    button.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    (button.layoutParams as LinearLayout.LayoutParams).setMargins(0,0,24,0)
                    button.setTextColor(resources.getColor(R.color.false_white,theme))
                    button.setBackgroundResource(R.drawable.category_button)
                    button.text = category
                    button.setOnClickListener {
                        showNotes(category)
                        currentCategory=category
                    }
                    categoryButtons.add(button)
                    binding.linearLayout.addView(button)
                }
            }
        }
        for(category in prev) {
            if (!categories.contains(category)){
                for (button in binding.linearLayout.children) {
                    if ((button as Button).text.toString().lowercase()==category ) {
                        binding.linearLayout.removeView(button)
                        break
                    }
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
        val categories = dbh.readCategories()
        if (categories.isNotEmpty() ){
            if (!categories.contains(currentCategory))
                currentCategory = categories[0]
        }
        checkCategories()
        if (currentCategory!="")
            showNotes(currentCategory)
    }
    private inner class GestureListener:GestureDetector.SimpleOnGestureListener(){
        val DELETE_THRESHOLD=100
        inner class DeleteAnimationListener(val note:Note):Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                currentCategory=(note).category
                dbh.deleteNote(note)
                onStart()
            }
            override fun onAnimationRepeat(p0: Animation?) {}
        }
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            val note:Note
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
            val note:Note
            val view:Int
            try {
                view=binding.listView.pointToPosition(e1!!.x.roundToInt(), e1.y.roundToInt())
                note = binding.listView.adapter.getItem(view) as Note
            }catch (e:Exception){
                return true
            }
            val animationListener=DeleteAnimationListener(note)
                if(distanceX < -DELETE_THRESHOLD){
                    val item=binding.listView.getChildAt(view)
                    item.animation=TranslateAnimation(0f,400f,0f,0f)
                    item.animation.duration=300
                    item.animation.setAnimationListener(animationListener)
                    item.requestLayout()
                    item.animation.start()

                }
            return true
        }

        override fun onLongPress(p0: MotionEvent?) {
            val layout=AlertDialogBinding.inflate(layoutInflater)
            var oldNote=Note()
            try {
                oldNote = binding.listView.adapter.getItem(
                    binding.listView.pointToPosition(
                        p0!!.x.roundToInt(),
                        p0.y.roundToInt()
                    )
                ) as Note
            }catch (e:Exception){}
            if(oldNote!=Note()){
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.edit_note))
                    .setView(layout.root)
                    .setPositiveButton(getString(R.string.edit)) { dialog, _ ->
                        val newNote=Note(layout.etTitolo.text.toString(), layout.etCategoria.text.toString(), oldNote.text, oldNote.image)
                        notes=dbh.readNotes(newNote.category)
                        if(notes.contains(newNote)){
                            Toast.makeText(this@MainActivity,getString(R.string.note_already_exists),Toast.LENGTH_SHORT).show()
                        }
                        else if (newNote.title==""){
                            Toast.makeText(this@MainActivity,getString(R.string.insert_title),Toast.LENGTH_SHORT).show()
                        }
                        else if (newNote.category==""){
                            Toast.makeText(this@MainActivity,getString(R.string.insert_category),Toast.LENGTH_SHORT).show()
                        }
                        else{
                            dbh.insertNote(newNote)
                            dbh.deleteNote(oldNote)
                        }
                        dialog.cancel()
                        onStart()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
                layout.etTitolo.setText(oldNote.title)
                layout.etCategoria.setText(oldNote.category)
            }

        }
    }
}
