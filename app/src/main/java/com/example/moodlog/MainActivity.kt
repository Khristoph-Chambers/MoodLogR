package com.example.moodlog

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var mainTitle: TextView
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var signinButton: Button
    private lateinit var signUpButton: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        NotificationUtils.createNotificationChannel(this)
        ScheduleNotification.scheduleDailyReminder(this)

        Toast.makeText(this, "Notification scheduled", Toast.LENGTH_SHORT).show()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        mainTitle = findViewById(R.id.mainTitle)
        emailText = findViewById(R.id.emailText)
        passwordText = findViewById(R.id.passwordText)
        signinButton = findViewById(R.id.signinButton)
        signUpButton = findViewById(R.id.signUpButton)


        signUpButton.setOnClickListener {
            val intent = Intent(applicationContext, SignupPage::class.java)
            startActivity(intent)

        }

        signinButton.setOnClickListener {

            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and Password are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Dashboard::class.java)
                        startActivity(intent)
                        finish()


                    } else {
                        val errorMessage = task.exception?.message
                        Toast.makeText(this, "Login Failed: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
        }

    }
}