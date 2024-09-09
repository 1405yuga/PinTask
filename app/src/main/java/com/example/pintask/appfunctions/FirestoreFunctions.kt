package com.example.pintask.appfunctions

import android.content.Context
import android.util.Log
import com.example.pintask.constants.AppConstants
import com.example.pintask.model.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

private val TAG = "FirestoreFunctions tag"

object FirestoreFunctions {

    fun addTask(
        task: TaskModel,
        context: Context,
        navigateToFragment: () -> (Unit)
    ) {
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

    fun updateTask(
        context: Context,
        documentPath: String,
        task: TaskModel,
        manageNotification: () -> Unit,
        closeCurrentActivity: () -> (Unit)
    ) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        firebaseFirestore.collection(user!!.email.toString()).document(documentPath).set(task)
            .addOnSuccessListener {
                AppConstants.notifyUser(context, "Task updated successfully")
                manageNotification()
                closeCurrentActivity()
            }
            .addOnFailureListener { AppConstants.notifyUser(context, "Cannot update task") }
    }

    fun getTask(context: Context, documentPath: String, returnTask: (DocumentSnapshot) -> (Unit)) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        firebaseFirestore.collection(user!!.email.toString()).document(documentPath).get()
            .addOnSuccessListener { returnTask(it) }
            .addOnFailureListener { AppConstants.notifyUser(context, "Unable to load Task") }
    }

    fun getTaskList(context: Context, updateList: (List<DocumentSnapshot>) -> (Unit)) {
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

    fun updatePinStatus(
        context: Context,
        documentPath: String,
        pinStatus: Boolean,
        refreshList: () -> Unit,
        manageNotification: () -> Unit
    ) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email

        firebaseFirestore.collection(email.toString()).document(documentPath)
            .update("pinned", pinStatus).addOnSuccessListener {
                manageNotification()
                refreshList()
            }
            .addOnFailureListener {
                AppConstants.notifyUser(context, "Unable to Pin")
            }
    }

    fun clearAllTasks(context: Context, refreshList: () -> Unit) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email

        if (email != null) {
            firebaseFirestore.collection(email).get()
                .addOnSuccessListener { taskList ->
                    for (i in taskList) {
                        i.reference.delete().addOnSuccessListener { refreshList() }
                    }

                }
                .addOnFailureListener {
                    AppConstants.notifyUser(context, "${it.message}")
                }
        }
    }

    fun deleteUser(context: Context, skipToOnBoarding: () -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user!!.email
        user.delete()
            .addOnSuccessListener {
                if (email != null) {
                    FirebaseFirestore.getInstance().collection(email).get()
                        .addOnSuccessListener { taskList ->
                            for (i in taskList) {
                                i.reference.delete()
                            }
                            skipToOnBoarding()
                        }
                }
                AppConstants.notifyUser(context, "Account Deleted successfully")
            }
            .addOnFailureListener {
                AppConstants.notifyUser(context, "Log in again to delete account")
                Log.d(TAG, "Delete account error : ${it.message}")
            }

    }


    fun deleteTask(
        context: Context,
        id: String,
        onSuccessFunction: () -> Unit
    ) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val email = FirebaseAuth.getInstance().currentUser!!.email

        if (email != null) {
            firebaseFirestore.collection(email).document(id).delete()
                .addOnSuccessListener {
                    onSuccessFunction()
                    NotificationFunctions.removeFromNotification(context, id.toInt())
                }
                .addOnFailureListener { AppConstants.notifyUser(context, "${it.message}") }
        }
    }

    fun unpinAllTasks(refreshList: () -> Unit, manageNotification: () -> Unit) {
        val email = FirebaseAuth.getInstance().currentUser!!.email

        if (email != null) {
            val collectionReference = FirebaseFirestore.getInstance().collection(email)

            collectionReference.whereEqualTo("pinned", true).get()
                .addOnSuccessListener {
                    for (i in it.documents) {
                        collectionReference.document(i.id).update("pinned", false)
                            .addOnSuccessListener { refreshList() }
                    }
                    manageNotification()
                }
                .addOnFailureListener {
                    Log.d(TAG, "unpinAllTasks ${it.message}")
                }
        }
    }
}