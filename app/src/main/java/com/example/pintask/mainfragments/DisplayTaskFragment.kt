package com.example.pintask.mainfragments

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.example.pintask.R
import com.example.pintask.TaskDetailActivity
import com.example.pintask.adapter.TaskListAdapter
import com.example.pintask.constants.AppConstants
import com.example.pintask.databinding.FragmentDisplayTaskBinding
import com.example.pintask.firebase.FirestoreFunctions
import com.example.pintask.model.TaskModel
import com.example.pintask.model.TaskViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

private val TAG = "DisplayTaskFragment tag"

class DisplayTaskFragment : Fragment() {

    private lateinit var binding: FragmentDisplayTaskBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var taskListAdapter: TaskListAdapter
    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: Notification.Builder
    private val channel_ID = "i.apps.notifications"
    private val description = "Test notification"

    override fun onStart() {
        super.onStart()
        // Configure Google Sign In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        // getting the value of gso inside the GoogleSigninClient
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        refreshList()
    }

    private fun refreshList() {
        FirestoreFunctions.getTask(requireContext(), updateList = {
            viewModel.setTaskList(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDisplayTaskBinding.inflate(inflater, container, false)
        taskListAdapter = TaskListAdapter()

        notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        viewModel.taskList.observe(viewLifecycleOwner, Observer {
            taskListAdapter.submitList(it)

            for (i in it) {
                //send notification
                val currentTask = i.toObject(TaskModel::class.java)
                val notificationID = i.id.toInt()

                // TODO: check if notification already sent
                if (currentTask!!.pinned == true) {
                    buildNotification(
                        currentTask.taskTitle ?: AppConstants.DEFAULT_TASK_TITLE,
                        currentTask.task ?: AppConstants.DEFAULT_TASK_DESC
                    )
                    notificationManager.notify(notificationID, notificationBuilder.build())
                }
            }


        })

        return binding.root
    }

    @SuppressLint("RemoteViewLayout")
    private fun buildNotification(taskTitle: String, task: String) {
        val intent = Intent(requireActivity(), TaskDetailActivity::class.java)

        //immutable since no changes after clicking notification
        val pendingIntent =
            PendingIntent.getActivity(requireActivity(), 0, intent, PendingIntent.FLAG_MUTABLE)

        //api>=26 requires notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channel_ID, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationManager.createNotificationChannel(notificationChannel)

            notificationBuilder = Notification.Builder(requireActivity(), channel_ID)
                .setSmallIcon(R.drawable.pushpin_selected)
                .setContentTitle(taskTitle)
                .setContentText(task)
                .setContentIntent(pendingIntent)
                .setOngoing(true) // to keep notification in notification bar
                .setAutoCancel(true)

        } else {

            val contentView =
                RemoteViews(requireContext().packageName, R.layout.activity_task_detail)

            notificationBuilder = Notification.Builder(requireActivity())
                .setContent(contentView)
                .setSmallIcon(R.drawable.pushpin_selected)
                .setContentIntent(pendingIntent)
                .setOngoing(true) // to keep notification in notification bar
                .setAutoCancel(true)

        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            accountImage.load(firebaseAuth.currentUser?.photoUrl) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.baseline_account_box_24)
                error(R.drawable.baseline_account_box_24)
            }
            navigationMenu.setOnClickListener {
                binding.drawerLayout.open()
            }
            navigationView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.signOut -> {
                        createDialog(
                            R.layout.card_signout_confirmation_dialog,
                            "Cancelled SignOut !",
                            ::signOutConfirmed
                        )
                        true
                    }

                    R.id.deleteAccount -> {
                        createDialog(R.layout.card_delete_account_confirmation_dialog,"Account Delete cancelled",::deleteAccountConfirmed)
                        true
                    }

                    else -> false
                }
            }

            recyclerView.apply {
                adapter = taskListAdapter
                layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            }

            addTaskButton.setOnClickListener {
                navigateToFragment(R.id.addTaskFragment)
            }
        }

    }
    

    private fun createDialog(
        layout: Int,
        noButtonMessage: String,
        yesButtonAction: () -> Unit
    ) {
        val view = layoutInflater.inflate(layout, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
            .create()
        builder.setView(view)
        view.findViewById<TextView>(R.id.noButton).setOnClickListener {
            builder.dismiss()
            AppConstants.notifyUser(requireContext(), noButtonMessage)
        }
        view.findViewById<TextView>(R.id.yesButton).setOnClickListener {
            builder.dismiss()
            yesButtonAction()
        }

        builder.show()
    }

    private fun deleteAccountConfirmed() {
        // TODO: delete account
        AppConstants.notifyUser(requireContext(),"delete account todo")
    }


    private fun signOutConfirmed() {
        mGoogleSignInClient.signOut().addOnSuccessListener {
            navigateToFragment(R.id.onBoardingFragment)
        }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Unable to signOut!", Toast.LENGTH_SHORT)
                    .show()
                Log.d(TAG, "signOut exception : ${it.message}")
            }
    }

    private fun navigateToFragment(fragmentId: Int) {
        findNavController().apply {
            // if navigating to onboarding then remove from stack if navigating to addtask dont remove from stack
            popBackStack(R.id.displayTaskFragment, fragmentId == R.id.onBoardingFragment)
            navigate(fragmentId)
        }
    }

}