package com.example.notetask

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login_page.*
import kotlinx.android.synthetic.main.activity_register.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RegisterActivity : AppCompatActivity() {
    val cal = Calendar.getInstance()
    lateinit var emailPattern: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        regButton.setOnClickListener {
            addUser()
        }
        back.setOnClickListener {
            startActivity(Intent(this, LoginPageActivity::class.java))
            finish()
        }

        regDOB.isEnabled = false
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int,
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@RegisterActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })


    }

    private fun addUser() {
        var rEmail = regEmail.text.toString()
        var rUname = regUsername.text.toString()
        var rFname = regFname.text.toString()
        var rLname = regLname.text.toString()
        var rPwd = regPwd.text.toString()
        var rDob = regDOB.text.toString()

        if (rEmail.isEmpty()) {
            emaiL.isErrorEnabled = true
            emaiL.error = "Enter the Email"
        } else if (rUname.isEmpty()) {
            emaiL.isErrorEnabled = false
            userName.isErrorEnabled = true
            userName.error = "Enter first name"
        } else if (rFname.isEmpty()) {
            emaiL.isErrorEnabled = false
            userName.isErrorEnabled = false
            firstName.isErrorEnabled = true
            firstName.error = "Enter last name"
        } else if (rLname.isEmpty()) {
            emaiL.isErrorEnabled = false
            userName.isErrorEnabled = false
            firstName.isErrorEnabled = false
            LastName.isErrorEnabled = true
            LastName.error = "Enter Last name"
        } else if (rPwd.isEmpty()) {
            emaiL.isErrorEnabled = false
            userName.isErrorEnabled = false
            firstName.isErrorEnabled = false
            LastName.isErrorEnabled = false
            pwd.isErrorEnabled = true
            pwd.error = "Enter Valid Password"
        } else if (rDob.isEmpty()) {
            emaiL.isErrorEnabled = false
            userName.isErrorEnabled = false
            firstName.isErrorEnabled = false
            LastName.isErrorEnabled = false
            pwd.isErrorEnabled = false
            Toast.makeText(this, "Select Dob", Toast.LENGTH_SHORT).show()
        } else if (!alpha(rPwd)) {
            emaiL.isErrorEnabled = false
            userName.isErrorEnabled = false
            firstName.isErrorEnabled = false
            LastName.isErrorEnabled = false
            pwd.isErrorEnabled = false
            pwd.error = "Password must be Alpha Numeric with special characters"
        } else if (!(rEmail.contains("@gmail.com"))) {
            emaiL.isErrorEnabled = false
            userName.isErrorEnabled = false
            firstName.isErrorEnabled = false
            LastName.isErrorEnabled = false
            pwd.isErrorEnabled = false
            emaiL.isErrorEnabled = true
            emaiL.error = "Enter valid email"
        } else {
            emaiL.isErrorEnabled = false
            val db = Firebase.firestore

            db.collection("Details").get()
                .addOnSuccessListener { result ->
                    var c = 0
                    for (document in result) {
                        if (rEmail == document.getString("Email")) c = +1
                    }
                    if (c == 0) {

                        var entryDetails = hashMapOf(
                            "Email" to rEmail, "Username" to rUname, "FirstName" to rFname,
                            "LastName" to rLname, "Password" to rPwd, "Dob" to rDob
                        )

                        db.collection("Details").document(rEmail)
                            .set(entryDetails)
                            .addOnSuccessListener {
                                Toast.makeText(this, "sucessfull signUp", Toast.LENGTH_SHORT)
                                    .show()
                                val i = Intent(this, HomeActivity::class.java)
                                i.putExtra("UserName", rUname)
                                startActivity(i)
                                finish()
                            }

                    } else if (c == 1) {
                        userName.isErrorEnabled = false
                        firstName.isErrorEnabled = false
                        LastName.isErrorEnabled = false
                        pwd.isErrorEnabled = false
                        emaiL.isErrorEnabled = true
                        emaiL.error = "Email already exists"
                    }
                }
        }
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        regDOB!!.setText(sdf.format(cal.getTime()))
    }


    fun alpha(Pwd: String): Boolean {
        var upper = false
        var lower = false
        var num = false
        var spl_char = false
        for (c in Pwd) {
            if (c in 'A'..'Z') {
                upper = true
            } else if (c in 'a'..'z')
                lower = true
            else if (c in '0'..'9')
                num = true
            else if (c !in 'a'..'z' && c !in 'A'..'Z' && c !in '0'..'9') {
                spl_char = true
            }
        }
        if (upper && lower && num && spl_char) return true
        else return false
    }
}



