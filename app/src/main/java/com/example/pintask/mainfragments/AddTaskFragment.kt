package com.example.pintask.mainfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import coil.Coil
import coil.load
import com.example.pintask.R
import com.example.pintask.constants.AppConstants
import com.example.pintask.databinding.FragmentAddTaskBinding
import com.example.pintask.model.TaskViewModel

class AddTaskFragment : Fragment() {

    private lateinit var binding: FragmentAddTaskBinding
    private val viewModel : TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTaskBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isPinned.observe(viewLifecycleOwner, Observer {
            val imageValue = if(it){
                R.drawable.pushpin_selected
            }
            else{
                R.drawable.pushpin_unselected
            }
            binding.pinButton.setImageDrawable(ContextCompat.getDrawable(requireContext(),imageValue))
        })


        binding.apply {
            pinButton.setOnClickListener {
                viewModel.setPinnedStatus(!viewModel.isPinned.value!!)
            }
            saveTask.setOnClickListener {
                // TODO: add task
            }
        }
    }

}