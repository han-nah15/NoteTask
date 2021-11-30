package com.example.notetask

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivities
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.animation.content.Content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.list_view_details.view.*

class taskListAdapter(var context: Context, val details:ArrayList<taskDetails>) : RecyclerView.Adapter<taskListAdapter.listViewHolder>() {


    var db=Firebase.firestore
    val user=FirebaseAuth.getInstance().currentUser

   inner class listViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindValue(data:taskDetails) {
            itemView.findViewById<TextView>(R.id.titlee).text=data.title
            itemView.findViewById<TextView>(R.id.dataa).text=data.note
            itemView.findViewById<TextView>(R.id.datee).text=data.date
            itemView.findViewById<ImageView>(R.id.delete).setOnClickListener {

                db.collection("Details").document("${user?.email}")
                    .collection("tasks").document("${data.title}")
                    .delete()
                    .addOnSuccessListener {
                        details.removeAt(position)
                        notifyDataSetChanged()
                        Toast.makeText(itemView.context, "task deleted", Toast.LENGTH_SHORT).show()
                    }
            }

            itemView.findViewById<ImageView>(R.id.edit).setOnClickListener {

                    var s=itemView.context as AppCompatActivity

                var i=Intent(s,AddTaskActivity::class.java)
                i.putExtra("taskTitle",data.title)
                i.putExtra("taskData",data.note)
                i.putExtra("taskTime",data.format)
                s.startActivity(i)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): listViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_view_details, parent, false)
        return listViewHolder(layout)
    }
    override fun onBindViewHolder(holder: listViewHolder, position: Int) {
        var pos=details[position]

        holder.bindValue(pos)
        val anim = AnimationUtils.loadAnimation(context,R.anim.slide_in)
        holder.itemView.cardView.animation=anim
    }

    override fun getItemCount(): Int {
        return details.size
    }
}