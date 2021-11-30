package com.example.notetask

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.system.Os.close
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*

class HomeActivity : AppCompatActivity() {

    var mauth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        var user = mauth.currentUser

        val toggle = ActionBarDrawerToggle(this, drawer, toolBar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        toolBar.title = "Notes"


        val navigationView = nav_view
        val headerView: View = navigationView.getHeaderView(0)
        headerView.userNameDisplay.text = user?.displayName
        var url: Uri? = user?.photoUrl
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.ic_baseline_email_24)
            .error(R.drawable.ic_baseline_person_24)
            .into(headerView.displayDP)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.signOut -> {
                    mauth.signOut()
                    Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginPageActivity::class.java))
                    finish()
                }
            }
            true
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragmentsC, taskListFragment())
            .commit()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Do you want to Exit?")
            .setPositiveButton("Yes") { _, _ ->
                Toast.makeText(this, "Exiting Note App", Toast.LENGTH_SHORT).show()
//                Firebase.auth.signOut()
                finishAffinity()
            }
            .setNegativeButton("No") { _, _ ->

            }
            .create()
            .show()
    }
}