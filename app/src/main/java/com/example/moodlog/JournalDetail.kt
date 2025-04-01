package com.example.moodlog

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class JournalDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_journal_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

            val dateDisplay: TextView = findViewById(R.id.dateDisplay)
            val journalText: TextView = findViewById(R.id.journalText)
            val returnButton: FloatingActionButton = findViewById(R.id.returnButton)

            val date = intent.getStringExtra("DATE")
            val text = intent.getStringExtra("TEXT")

            dateDisplay.text = date
            journalText.text = text

            returnButton.setOnClickListener {
                val intent = Intent(applicationContext, Dashboard::class.java)
                startActivity(intent)
            }



    }
}