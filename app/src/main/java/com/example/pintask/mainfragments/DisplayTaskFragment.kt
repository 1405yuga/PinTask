package com.example.pintask.mainfragments

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.example.pintask.R
import com.example.pintask.adapter.TaskListAdapter
import com.example.pintask.constants.AppConstants
import com.example.pintask.databinding.FragmentDisplayTaskBinding
import com.example.pintask.datastore.PreferenceStore
import com.example.pintask.appfunctions.FirestoreFunctions
import com.example.pintask.model.TaskModel
import com.example.pintask.model.TaskViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

private val TAG = "DisplayTaskFragment tag"

class DisplayTaskFragment : Fragment() {

    private lateinit var binding: FragmentDisplayTaskBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var taskListAdapter: TaskListAdapter
    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: Notification.Builder
    private lateinit var preferenceStore: PreferenceStore
    private var isDarkModeOn = false

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDisplayTaskBinding.inflate(inflater, container, false)
        taskListAdapter = TaskListAdapter(requireContext(), ::refreshList)
        notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        preferenceStore = PreferenceStore(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyBinding()
        viewObservers()
    }

    private fun viewObservers() {
        viewModel.taskList.observe(viewLifecycleOwner, Observer {
            taskListAdapter.submitList(it)

            if (it.isNotEmpty()) {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyListText.visibility = View.GONE
                for (i in it) {
                    //send notification
                    val currentTask = i.toObject(TaskModel::class.java)
                    val notificationID = i.id.toInt()

                    if (currentTask!!.pinned == true) {
                        AppConstants.buildNotification(
                            requireContext(),
                            i.id,
                            currentTask.taskTitle ?: AppConstants.DEFAULT_TASK_TITLE,
                            currentTask.task ?: AppConstants.DEFAULT_TASK_DESC
                        )
                    }
                }
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.emptyListText.visibility = View.VISIBLE
            }

        })

        preferenceStore.userUIPreference.asLiveData().observe(viewLifecycleOwner, Observer {
            isDarkModeOn = it
            if (it) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        })
    }

    private fun applyBinding() {
        binding.apply {
            accountImage.load(firebaseAuth.currentUser?.photoUrl) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.baseline_account_box_24)
                error(R.drawable.baseline_account_box_24)
            }
            navigationMenu.setOnClickListener {
                drawerLayout.open()
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
                        createDialog(
                            R.layout.card_delete_account_confirmation_dialog,
                            "Account Delete cancelled",
                            ::deleteAccountConfirmed
                        )
                        true
                    }

                    R.id.clearTasks -> {
                        createDialog(
                            R.layout.card_clear_all_tasks_confirmation_dialog,
                            "Cancelled",
                            ::clearAllTasksConfirmed
                        )
                        drawerLayout.close()
                        true
                    }

                    R.id.unpinAllTasks -> {
                        FirestoreFunctions.unpinAllTasks(::refreshList, manageNotification = {
                            notificationManager.cancelAll()
                        })
                        drawerLayout.close()
                        true
                    }

                    R.id.uiMode -> {
                        isDarkModeOn = !isDarkModeOn
                        lifecycleScope.launch {
                            preferenceStore.saveUIMode(requireContext(), isDarkModeOn)
                        }
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
    /*
        private fun buildNotification(taskID: String, taskTitle: String, task: String) {
            val intent = Intent(requireActivity(), TaskDetailActivity::class.java)
            intent.putExtra(AppConstants.KEY_TASK_ID, taskID)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // existing PendingIntent is canceled(CANCEL_CURRENT)
            val pendingIntent =
                PendingIntent.getActivity(
                    requireActivity(),
                    taskID.toInt(),
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
                )

            //api>=26 requires notification channel
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
                .setOnlyAlertOnce(true)
                .setStyle(Notification.BigTextStyle().bigText(task)) // expandable notification

        }

     */

    private fun refreshList() {
        FirestoreFunctions.getTaskList(requireContext(), updateList = {
            viewModel.setTaskList(it)
        })
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
            binding.drawerLayout.close()
            yesButtonAction()
        }

        builder.show()
    }

    private fun deleteAccountConfirmed() {
        FirestoreFunctions.deleteUser(requireContext(), ::signOutConfirmed)
    }

    private fun clearAllTasksConfirmed() {
        FirestoreFunctions.clearAllTasks(requireContext(), ::refreshList)
    }

    private fun signOutConfirmed() {
        mGoogleSignInClient.signOut().addOnSuccessListener {
            navigateToFragment(R.id.onBoardingFragment)
        }
            .addOnFailureListener {
                AppConstants.notifyUser(requireContext(), "Unable to signOut!")
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