package com.example.notetask

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import android.content.Intent.getIntent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast


const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class Notification : BroadcastReceiver(){


    override fun onReceive(context: Context, intent: Intent)
    {

        val imgUri = AddTaskActivity.filePathUri
//        Log.d("123456789","234567------>>>>${imgUri}")

        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),Uri.parse(imgUri))
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(bitmap)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))


        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID,notification.build())

    }

}