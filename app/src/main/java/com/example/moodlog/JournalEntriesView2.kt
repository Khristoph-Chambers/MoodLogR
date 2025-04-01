package com.example.moodlog

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class JournalEntriesView2 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var journalAdapter: JournalAdapter
    private val journalList = mutableListOf<JournalEntryModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_entries_view2)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadJournalEntries()
    }



    private fun loadJournalEntries() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = user.uid
        val db = FirebaseFirestore.getInstance()
        db.collection("journalEntries")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                journalList.clear()
                for (document in documents) {
                    val entryText = document.getString("journalText") ?: ""
                    val createdAt = document.getString("createdAt") ?: ""
                    journalList.add(JournalEntryModel(entryText, createdAt))
                }
                journalAdapter = JournalAdapter(journalList) { journalEntry ->
                    val intent = Intent(this, JournalDetail::class.java)
                    intent.putExtra("DATE", journalEntry.createdAt)
                    intent.putExtra("TEXT", journalEntry.journalText)
                    startActivity(intent)
                }
                recyclerView.adapter = journalAdapter

            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading entries", Toast.LENGTH_SHORT).show()
            }


    }


}
