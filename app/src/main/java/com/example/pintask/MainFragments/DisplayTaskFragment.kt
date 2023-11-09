package com.example.pintask.MainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pintask.R
import com.example.pintask.databinding.FragmentDisplayTaskBinding


class DisplayTaskFragment : Fragment() {

    private lateinit var binding: FragmentDisplayTaskBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDisplayTaskBinding.inflate(inflater,container,false)
        binding.apply {
            navigationMenu.setOnClickListener {
                binding.drawerLayout.open()
            }
        }

        return binding.root
    }

}