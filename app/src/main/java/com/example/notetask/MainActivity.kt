package com.example.notetask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var mauth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lottie.animate().alpha(0f).alpha(1f).setDuration(3000L).withEndAction {
            if (mauth.currentUser != null) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "login to continue", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginPageActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}