package com.example.notetask

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login_page.*

class LoginPageActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var mauth: FirebaseAuth
    val RC_SIGN_IN =9001
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        mauth = Firebase.auth

        signupNormal.setOnClickListener { login() }

        textSignUp.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }


        google_button.setOnClickListener{
            signIn()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

        googleSignInClient.signOut()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                }
            } else {
                Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mauth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mauth.currentUser
                    var c = 0
                    db.collection("Details").get()
                        .addOnSuccessListener { result ->
                            for (document in result) {if (user?.email == document.getString("Email")) c = +1}
                            if (c== 0) {
                                val name = user?.displayName
                                val uId = user?.uid
                                val email = user?.email
                                val users =hashMapOf("Username" to name, "Email" to email, "UId" to uId)
                                if (email != null) {
                                    db.collection("Details").document(email)
                                        .set(users)
                                        .addOnSuccessListener {
                                            Toast.makeText(this,"Added to firebase",Toast.LENGTH_SHORT).show()
                                            val i=Intent(this, HomeActivity::class.java)
                                            startActivity(i)
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this,"Failed to Add",Toast.LENGTH_SHORT).show()
                                        }
                                }
                            } else if(c==1) {
                                Toast.makeText(this,"logging to your existed account ",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, HomeActivity::class.java))
                            }
                        }
                        .addOnFailureListener {
                            Log.d("failed..",it.message.toString())}
                }
            }
            .addOnFailureListener {
                Log.d("failed..", it.message.toString())
            }
    }

    private fun login() {
        val user = UserIdInput.text.toString()
        val pwd = UserPasswordInput.text.toString()
        if (user.isEmpty()) {
            userId.isErrorEnabled = true
            userId.error = "Enter UserName"
        } else if (pwd.isEmpty()) {
            userId.isErrorEnabled = false
            password.isErrorEnabled = true
            password.error = "Enter password"
        } else {
            var c=0
            userId.isErrorEnabled = false
            password.isErrorEnabled = false
            db.collection("Details").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (user == document.getString("Username") && pwd == document.getString("Password")) {
                            Toast.makeText(this, "Login Sucessfull :)", Toast.LENGTH_SHORT).show()
                            val userName = document.getString("Username")
                            val i = Intent(this, HomeActivity::class.java)
                            i.putExtra("UserName", userName)
                            startActivity(i)
                            c = +1
                        } else if((user == document.getString("Username")) && (pwd != document.getString("Password"))) {
                            Toast.makeText(this, "Invalid details", Toast.LENGTH_SHORT).show()
                        }

                    }
                    }

            if (c==0){
                Toast.makeText(this, "Invalid Details", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onBackPressed() {
        val builder=AlertDialog.Builder(this)
            .setTitle("Do you want to Exit?")
            .setPositiveButton("Yes"){_,_->
                Toast.makeText(this, "Exiting Note App", Toast.LENGTH_SHORT).show()
//                Firebase.auth.signOut()
                finish()
            }
            .setNegativeButton("No"){_,_->

            }
            .create()
            .show()
    }


}