package com.example.notetask

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_task_list.view.*
import kotlinx.android.synthetic.main.list_view_details.*

class taskListFragment : Fragment() {

    var auth= FirebaseAuth.getInstance()
    lateinit var recyc: RecyclerView
    var list=ArrayList<taskDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
       val view= inflater.inflate(R.layout.fragment_task_list, container, false)

        view?.add_notes?.setOnClickListener {
            startActivity(Intent(view?.context,AddTaskActivity::class.java))
            activity?.finish()
        }

        recyc=view.recyc_view
        recyc.layoutManager= LinearLayoutManager(view.context)
        val anim = AnimationUtils.loadAnimation(view.context,R.anim.slide_in)
        recyc.animation=anim
        var user=auth.currentUser
        val db= Firebase.firestore
        db.collection("Details").document("${user?.email}")
            .collection("tasks")
            .get()
            .addOnSuccessListener { resObj->
                for (doc in resObj){
                    list.add(taskDetails(doc.getString("title").toString(),
                        doc.getString("Task").toString(),
                        doc.getString("date").toString(),
                    doc.getString("time").toString(),
                    doc.getString("Format").toString()))
                    val adapt=(taskListAdapter(view.context,list))
                    recyc.adapter=adapt
                }

            }
            .addOnFailureListener {
                Toast.makeText(view.context, it.message.toString(), Toast.LENGTH_SHORT).show()
            }







        return view
    }


}