package com.example.notetask

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_task.*
import java.util.*
import android.widget.DatePicker

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.TimePicker
import android.util.Log


class AddTaskActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    companion object getUri{
        var filePathUri : String? = ""
        fun uriFilePath(path:String?){
            filePathUri = path
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        filePathUri = ""
    }

    lateinit var listDetails: List<String>
    lateinit var add_task_intent: Intent
    var uri: Uri? = null
    var day = 0
    var month: Int? = 0
    var year: Int? = 0
    var hour: Int? = 0
    var minute: Int? = 0
    var myDay = 0
    var myMonth: Int? = 0
    var myYear: Int? = 0
    var myHour: Int? = 0
    var myMinute: Int? = 0

    var timee: Long? = 0

    val db = Firebase.firestore
    var user = FirebaseAuth.getInstance().currentUser
    var updateTitle: String? = ""
    var updateNote: String? = ""
    var updateTime: Any? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val b: Bundle? = intent.extras
        updateTitle = b?.getString("taskTitle")
        updateNote = b?.getString("taskData")
        updateTime = b?.getString("taskTime")

        createNotificationChannel()

        if (updateTitle != null) {
            submitButton.visibility = View.GONE
            UpdateButton.visibility = View.VISIBLE
            titleET.setText(updateTitle)
            messageET.setText(updateNote)
            titleET.isEnabled = false
            UpdateButton.setOnClickListener {
                var s = getTime()
                list.tlist.add(s)
                for (i in list.tlist) {
                    scheduleNotification(i)
                }
            }
        } else {
            submitButton.visibility = View.VISIBLE
            UpdateButton.visibility = View.GONE
        }

        scheduleTimeDate.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val aTime = calendar.timeInMillis
            val datePickerDialog =
                DatePickerDialog(this, this, year!!, month!!, day)
            datePickerDialog.show()
            if (timee!! > aTime) {
                displayTextDate.visibility = View.VISIBLE
            }
        }

        selectimage.setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(intent, 100)
        })

        submitButton.setOnClickListener {
            var s = getTime()
            list.tlist.add(s)
            for (i in list.tlist) {
                scheduleNotification(i)
            }
        }

        cancel.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 ) {
            uri = data?.data
            uriFilePath(uri.toString())
            imageDisplay.setImageURI(data?.data)
        }

    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        myDay = p3
        myYear = p1
        myMonth = p2
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this, this, hour!!, minute!!, false)
        timePickerDialog.show()
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {

        myHour = p1
        myMinute = p2
        displayTextDate.setText("${myDay}-${myMonth}-${myMonth} at ${myHour} : ${myMinute}")

        val calendar = Calendar.getInstance()
        calendar.set(myYear!!, myMonth!!, myDay, myHour!!, myMinute!!)
        timee = calendar.timeInMillis
    }

    private fun updateDetails(listDetails: List<String>) {
        db.collection("Details").document("${user?.email}")
            .collection("tasks").document("$updateTitle")
            .update("Task", listDetails[1], "date", listDetails[2], "time", listDetails[3])
            .addOnSuccessListener {
                Toast.makeText(this, "Task updated Sucessfully :)", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(this, "Not updated. Try again later :(", Toast.LENGTH_SHORT).show()
            }

    }

    private fun addDetails(listDetails: List<String>) {
        var note = hashMapOf(
            "title" to listDetails[0],
            "Task" to listDetails[1],
            "date" to listDetails[2],
            "time" to listDetails[3],
            "Format" to listDetails[4]
        )
        val tittle = listDetails[0]

        db.collection("Details").document("${user?.email}")
            .collection("tasks").document("$tittle").set(note)
            .addOnSuccessListener {
              //
                // Toast.makeText(this, "task added", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(this, "Task not added", Toast.LENGTH_SHORT).show()
            }


    }

    private fun scheduleNotification(i: Long) {
        val title = titleET.text.toString()
        val message = messageET.text.toString()
        if (title.isEmpty()) {
            Toast.makeText(this, "Set Task", Toast.LENGTH_SHORT).show()
        } else if (message.isEmpty()) {
            Toast.makeText(this, "Set task", Toast.LENGTH_SHORT).show()
        } else if (displayTextDate.text == "") {
            Toast.makeText(this, "Set Time", Toast.LENGTH_SHORT).show()
        } else {
            add_task_intent = Intent(applicationContext, Notification::class.java)
            add_task_intent.putExtra(titleExtra, title)
            add_task_intent.putExtra(messageExtra, message)
//            add_task_intent.putExtra("Uri",uri.toString())
//            Toast.makeText(this, "uriiiii<--->${uri.toString()}", Toast.LENGTH_SHORT).show()
     //       Toast.makeText(this, "uri - > ${filePathUri}", Toast.LENGTH_SHORT).show()
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Schedule Notification?")
            builder.setPositiveButton("Yes") { dialogInterface, j ->

                listDetails = showAlert(title, message, timee!!, i)
                if (updateTitle == null) {

                    addDetails(listDetails)
                } else {
                    updateDetails(listDetails)
                }

            }
                .setNegativeButton("No") { dialogInterface, j ->
                    dialogInterface.dismiss()
                }
            builder.create()
            builder.show()
            builder.setCancelable(false)

        }
    }

    private fun showAlert(title: String, message: String, timee: Long, i: Long): List<String> {
        val date = Date(timee)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        Toast.makeText(this,
            "${dateFormat.format(date)},${timeFormat.format(date)}",
            Toast.LENGTH_SHORT).show()

        val pendingIntent =
            PendingIntent.getBroadcast(applicationContext, i.toInt(), add_task_intent, 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timee, pendingIntent)


        return listOf(title,
            message,
            dateFormat.format(date),
            timeFormat.format(date),
            i.toString())
    }


    private fun getTime(): Long {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)
        val pickedDateTime = Calendar.getInstance()
        pickedDateTime.set(startYear, startMonth, startDay, startHour, startMinute)
        return pickedDateTime.timeInMillis
    }

    private fun createNotificationChannel() {
        val name = "Notification Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

