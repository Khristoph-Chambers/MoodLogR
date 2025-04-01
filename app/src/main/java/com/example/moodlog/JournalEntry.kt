package com.example.moodlog

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class JournalEntry : AppCompatActivity() {

    private lateinit var dateDisplay: TextView
    private lateinit var journalEntry: EditText
    private lateinit var savefaButton: FloatingActionButton
    private lateinit var returnButton: FloatingActionButton
    private lateinit var deleteButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_journal_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dateDisplay = findViewById(R.id.dateDisplay)
        journalEntry = findViewById(R.id.journalEntry)
        savefaButton = findViewById(R.id.savefaButton)
        returnButton = findViewById(R.id.returnButton)
        deleteButton = findViewById(R.id.deleteButton)

        val currentDate = Calendar.getInstance().time

        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)


        dateDisplay.text = formattedDate

        val journalText =journalEntry.text.toString().trim()

        savefaButton.setOnClickListener {
            val journalText =journalEntry.text.toString().trim()

            if (journalText.isEmpty()) {
                Toast.makeText(this, "Journal Empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = user.uid
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(currentDate)

            val journalEntryData = hashMapOf(
                "userId" to userId,
                "journalText" to journalText,
                "createdAt" to formattedDate
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("journalEntries").add(journalEntryData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Entry saved successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, Dashboard::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error saving entry", Toast.LENGTH_SHORT).show()
                }



        }

        returnButton.setOnClickListener {
            val intent = Intent(applicationContext, Dashboard::class.java)
            startActivity(intent)
        }



    }
}