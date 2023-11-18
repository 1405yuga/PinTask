package com.example.pintask.MainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.pintask.R
import com.example.pintask.databinding.FragmentDisplayTaskBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth


class DisplayTaskFragment : Fragment() {

    private lateinit var binding: FragmentDisplayTaskBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDisplayTaskBinding.inflate(inflater,container,false)
        binding.apply {
            navigationMenu.setOnClickListener {
                binding.drawerLayout.open()
            }
            navigationView.setNavigationItemSelectedListener {menuItem ->
                when(menuItem.itemId){
                    R.id.signOut -> {
                        openConfirmationDialog()
                        true
                    }

                    else -> false
                }
            }
        }

        return binding.root
    }

    private fun openConfirmationDialog() {
        val view = layoutInflater.inflate(R.layout.card_signout_confirmation_dialog,null)
        val builder = MaterialAlertDialogBuilder(requireContext())
            .create()
        builder.setView(view)
        view.findViewById<TextView>(R.id.noButton).setOnClickListener {
            builder.dismiss()
            Toast.makeText(requireContext(),"Cancelled signOut!",Toast.LENGTH_SHORT).show()
        }
        view.findViewById<TextView>(R.id.yesButton).setOnClickListener {
            // TODO: signout the current user and navigate to onboarding page 
            Toast.makeText(requireContext(),"yes signOut!",Toast.LENGTH_SHORT).show()
            builder.dismiss()
        }
        builder.show()
    }

    private fun navigateToFragment(fragmentId: Int) {
        findNavController().apply {
            navigate(fragmentId)
            popBackStack(R.id.displayTaskFragment,true)
        }
    }

}