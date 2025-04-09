package com.example.moodlog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class JournalDetail : AppCompatActivity() {

    private lateinit var dateDisplay: TextView
    private lateinit var journalText: EditText
    private lateinit var returnButton: FloatingActionButton
    private lateinit var saveButton: FloatingActionButton
    private lateinit var deleteButton: FloatingActionButton

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_journal_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dateDisplay = findViewById(R.id.dateDisplay)
        journalText = findViewById(R.id.journalText)
        returnButton = findViewById(R.id.returnButton)
        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)

        val date = intent.getStringExtra("DATE")
        val text = intent.getStringExtra("TEXT")
        val journalId = intent.getStringExtra("JOURNAL_ID")

        dateDisplay.text = date
        journalText.setText(text)

        returnButton.setOnClickListener {
            val intent = Intent(applicationContext, Dashboard::class.java)
            startActivity(intent)
        }
        saveButton.setOnClickListener {
            val updatedText = journalText.text.toString()
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = user.uid

            val journalId = intent.getStringExtra("JOURNAL_ID")
            Log.d("JournalDetail", "Journal ID: $journalId")

            if (journalId != null) {
                db.collection("journalEntries")
                    .document(journalId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            db.collection("journalEntries")
                                .document(journalId)
                                .update("journalText", updatedText)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Journal entry updated", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error updating journal entry", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Journal entry not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error fetching journal entry", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Journal ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        deleteButton.setOnClickListener {
            val journalId = intent.getStringExtra("JOURNAL_ID")

            if (journalId == null) {
                Toast.makeText(this, "Journal ID is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Confirmation dialog
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this journal entry?")
                .setPositiveButton("Yes") { _, _ ->
                    db.collection("journalEntries")
                        .document(journalId)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Journal entry deleted", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, JournalEntriesView2::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error deleting entry", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


    }
}
