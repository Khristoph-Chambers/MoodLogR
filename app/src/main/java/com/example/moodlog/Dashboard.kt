package com.example.moodlog


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Dashboard : AppCompatActivity() {



    private lateinit var userInfo: TextView
    private lateinit var journalDisplay: TextView
    private lateinit var createfaButton: FloatingActionButton
    private lateinit var viewEntriesButton: Button
    private lateinit var logoutButton: Button
    private lateinit var dashboardDisplay: TextView
    private lateinit var journalAdapter: JournalAdapter
    private val db = FirebaseFirestore.getInstance()
    private val journalEntries = mutableListOf<JournalEntryModel>()

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
        userInfo = findViewById(R.id.userInfo)



        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = user.uid


        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val firstName = document.getString("firstName") ?: ""
                    userInfo.text = "Hi, $firstName"
                } else {
                    userInfo.text = "Hi!"
                }
            }
            .addOnFailureListener { exception ->
                userInfo.text = "Hi!"
                Toast.makeText(this, "Could not load user info", Toast.LENGTH_SHORT).show()
                Log.e("Dashboard", "Error fetching user info", exception)
            }

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



        val calendarView = findViewById<CalendarView>(R.id.calendarView)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)

            val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
            val selectedDate = dateFormat.format(calendar.time)

            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnDateChangeListener
            }

            val userId = user.uid
            val db = FirebaseFirestore.getInstance()

            loadJournalEntriesForDate(selectedDate)
            setupRecyclerView()

        }


        createfaButton.setOnClickListener {
            val intent = Intent(applicationContext, JournalEntry::class.java)
            startActivity(intent)
        }


    }
    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvDashboardJournalEntries)
        recyclerView.layoutManager = LinearLayoutManager(this)
        journalAdapter = JournalAdapter(this, journalEntries) { entry ->
        }
        recyclerView.adapter = journalAdapter
    }


    private fun loadJournalEntriesForDate(date: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("journalEntries")
            .whereEqualTo("userId", userId)
            .whereEqualTo("createdAt", date)
            .get()
            .addOnSuccessListener { snapshot ->
                journalEntries.clear()
                for (doc in snapshot) {
                    val entry = JournalEntryModel(
                        journalText = doc.getString("journalText") ?: "",
                        createdAt = doc.getString("createdAt") ?: "",
                        id = doc.id
                    )
                    journalEntries.add(entry)
                }
                journalAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load entries", Toast.LENGTH_SHORT).show()

            }
    }


}