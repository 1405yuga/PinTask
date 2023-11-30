package com.example.pintask.mainfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.pintask.adapter.TaskListAdapter
import com.example.pintask.databinding.FragmentDisplayTaskBinding
import com.example.pintask.firebase.FirestoreFunctions
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

        viewModel.taskList.observe(viewLifecycleOwner, Observer {
            taskListAdapter.submitList(it)
        })

        return binding.root
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
                        openConfirmationDialog()
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

    private fun openConfirmationDialog() {
        val view = layoutInflater.inflate(R.layout.card_signout_confirmation_dialog, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
            .create()
        builder.setView(view)
        view.findViewById<TextView>(R.id.noButton).setOnClickListener {
            builder.dismiss()
            Toast.makeText(requireContext(), "Cancelled signOut!", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<TextView>(R.id.yesButton).setOnClickListener {
            mGoogleSignInClient.signOut().addOnSuccessListener {
                navigateToFragment(R.id.onBoardingFragment)
            }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Unable to signOut!", Toast.LENGTH_SHORT)
                        .show()
                    Log.d(TAG, "signOut exception : ${it.message}")
                }
            builder.dismiss()
        }

        builder.show()
    }

    private fun navigateToFragment(fragmentId: Int) {
        findNavController().apply {
            // if navigating to onboarding then remove from stack if navigating to addtask dont remove from stack
            popBackStack(R.id.displayTaskFragment, fragmentId == R.id.onBoardingFragment)
            navigate(fragmentId)
        }
    }

}