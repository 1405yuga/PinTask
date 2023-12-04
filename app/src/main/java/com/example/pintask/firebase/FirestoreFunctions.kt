package com.example.pintask.firebase

import android.content.Context
import com.example.pintask.constants.AppConstants
import com.example.pintask.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

object FirestoreFunctions {

    fun addTask(task: TaskModel, context: Context, navigateToFragment: () -> (Unit)) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val id = Random.nextInt(1, Int.MAX_VALUE)

        firebaseFirestore.collection(user!!.email.toString())
            .document(id.toString())
            .set(task)
            .addOnSuccessListener {
                AppConstants.notifyUser(context, "Task added successfully")
                navigateToFragment()
            }
            .addOnFailureListener {
                AppConstants.notifyUser(context, "Cannot add task")
            }
    }

    fun getTask(context: Context, updateList: (List<DocumentSnapshot>) -> (Unit)) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        firebaseFirestore.collection(user!!.email.toString()).get()
            .addOnSuccessListener {
                updateList(it.documents)
            }
            .addOnFailureListener {
                AppConstants.notifyUser(context, "Unable to get tasks")
            }
    }
}