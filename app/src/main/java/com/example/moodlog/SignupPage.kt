package com.example.moodlog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

class SignupPage : AppCompatActivity() {

    private lateinit var mainTitle: TextView
    private lateinit var firstnameText: EditText
    private lateinit var lasnameText: EditText
    private lateinit var emailText: EditText
    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var conpasswordText: EditText
    private lateinit var signinButton: Button
    private lateinit var signinReturnButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        mainTitle = findViewById(R.id.mainTitle)
        firstnameText = findViewById(R.id.firstnameText)
        lasnameText = findViewById(R.id.lasnameText)
        emailText = findViewById(R.id.emailText)
        usernameText = findViewById(R.id.usernameText)
        passwordText = findViewById(R.id.passwordText)
        conpasswordText = findViewById(R.id.conpasswordText)
        signinButton = findViewById(R.id.signinButton)
        signinReturnButton = findViewById(R.id.signinReturnButton)

        signinReturnButton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        signinButton.setOnClickListener {

            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)




            val firstName = firstnameText.text.toString().trim()
            val lastName = lasnameText.text.toString().trim()
            val email = emailText.text.toString().trim()
            val username = usernameText.text.toString().trim()
            val password = passwordText.text.toString().trim()
            val confirmPassword = conpasswordText.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

                    // Username is available, proceed with signup
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            val user = hashMapOf(
                                "firstName" to firstName,
                                "lastName" to lastName,
                                "username" to username,
                                "email" to email,
                                "created_at" to System.currentTimeMillis()
                            )

                            userId?.let {
                                db.collection("users").document(it).set(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Error saving user data", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            val errorMessage = task.exception?.message
                            Toast.makeText(this, "Signup Failed: $errorMessage", Toast.LENGTH_LONG).show()
                            Log.e("SignupError", "Signup Failed: $errorMessage")
                        }
                    }
            }

        }


    }

