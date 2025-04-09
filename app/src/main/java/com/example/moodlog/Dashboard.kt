package com.example.moodlog


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Dashboard : AppCompatActivity() {

    private lateinit var journalDisplay: TextView
    private lateinit var createfaButton: FloatingActionButton
    private lateinit var viewEntriesButton: Button
    private lateinit var logoutButton: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        NotificationUtils.createNotificationChannel(this)
        ScheduleNotification.scheduleDailyReminder(this)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }



        //Toast.makeText(this, "Notification scheduled", Toast.LENGTH_SHORT).show()


        journalDisplay = findViewById(R.id.journalDisplay)
        createfaButton = findViewById(R.id.createfaButton)
        viewEntriesButton = findViewById(R.id.viewEntriesButton)
        logoutButton = findViewById(R.id.logoutButton)


        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
        }

        val viewEntriesButton: Button = findViewById(R.id.viewEntriesButton)

        viewEntriesButton.setOnClickListener {
            val intent = Intent(this, JournalEntriesView2::class.java)
            startActivity(intent)
        }



        createfaButton.setOnClickListener {
            val intent = Intent(applicationContext, JournalEntry::class.java)
            startActivity(intent)
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = user.uid

        db.collection("journalEntries")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val entries = StringBuilder()

                for (document in documents) {
                    val entryText = document.getString("journalText")
                    val createdAt = document.getString("createdAt")
                    entries.append("$createdAt\n$entryText\n\n")
                }

                journalDisplay.text = if (entries.isNotEmpty()) entries.toString() else "No Journal Entries Found."
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading entries", Toast.LENGTH_SHORT).show()
            }

    }
}