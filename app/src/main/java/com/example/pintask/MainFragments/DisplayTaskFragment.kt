package com.example.pintask.MainFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.pintask.R
import com.example.pintask.databinding.FragmentDisplayTaskBinding
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

    override fun onStart() {
        super.onStart()
        // Configure Google Sign In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        // getting the value of gso inside the GoogleSigninClient
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDisplayTaskBinding.inflate(inflater, container, false)
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

            addTaskButton.setOnClickListener {
                navigateToFragment(R.id.addTaskFragment)
            }
        }

        return binding.root
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
            popBackStack(R.id.displayTaskFragment, fragmentId == R.id.onBoardingFragment)
            navigate(fragmentId)
        }
    }

}