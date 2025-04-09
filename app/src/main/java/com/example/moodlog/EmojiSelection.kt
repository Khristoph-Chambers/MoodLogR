package com.example.moodlog

import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
// import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.firestore.FirebaseFirestore

class EmojiSelection : AppCompatActivity() {

    private lateinit var emojiHappy: TextView
    private lateinit var emojiContent: TextView
    private lateinit var emojiNeutral: TextView
    private lateinit var emojiSad: TextView
    private lateinit var emojiAngry: TextView
    private lateinit var emojiAnxious: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emoji_selection)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emojiHappy = findViewById(R.id.emojiHappy)
        emojiContent= findViewById(R.id.emojiContent)
        emojiNeutral = findViewById(R.id.emojiNeutral)
        emojiSad = findViewById(R.id.emojiSad)
        emojiAngry = findViewById(R.id.emojiAngry)
        emojiAnxious = findViewById(R.id.emojiAnxious)



        val emojiMap = mapOf(
            R.id.emojiHappy to "Happy",
            R.id.emojiContent to "Content",
            R.id.emojiNeutral to "Neutral",
            R.id.emojiSad to "Sad",
            R.id.emojiAngry to "Angry",
            R.id.emojiAnxious to "Anxious",

        )

        for ((id, mood) in emojiMap) {
            val emojiView = findViewById<TextView>(id)
            emojiView.setOnClickListener {
                animateEmoji(it)
                Toast.makeText(this, "You selected: $mood", Toast.LENGTH_SHORT).show()

                // Uncomment below to save to Firestore later
                /*
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val db = FirebaseFirestore.getInstance()
                    val moodEntry = hashMapOf(
                        "mood" to mood,
                        "timestamp" to System.currentTimeMillis(),
                        "userId" to user.uid
                    )
                    db.collection("moodSelections")
                        .add(moodEntry)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Mood saved!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save mood", Toast.LENGTH_SHORT).show()
                        }
                }
                */
            }
        }
    }

    private fun animateEmoji(view: View) {
        view.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setInterpolator(OvershootInterpolator())
            .setDuration(200)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }
}
