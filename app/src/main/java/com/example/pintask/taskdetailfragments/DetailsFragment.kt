package com.example.pintask.taskdetailfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.pintask.R
import com.example.pintask.constants.AppConstants
import com.example.pintask.databinding.FragmentDetailsBinding
import com.example.pintask.firebase.FirestoreFunctions
import com.example.pintask.model.DetailsTaskViewModel
import com.example.pintask.model.TaskModel
import com.google.android.material.tabs.TabLayout.TabGravity

private const val TAG = "DetailsFragment tag"

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private val viewModel : DetailsTaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater,container,false)
        val docID : String? = requireActivity().intent.getStringExtra("TASK_ID")
        if (docID != null) {
            FirestoreFunctions.getTask(requireContext(),docID, returnTask = {
                val currentTask = it.toObject(TaskModel::class.java)
                viewModel.apply {
                    setTaskTitle(currentTask!!.taskTitle ?: AppConstants.DEFAULT_TASK_TITLE)
                    setTask(currentTask.task ?: AppConstants.DEFAULT_TASK_DESC)
                    setPinnedStatus(currentTask.pinned ?: AppConstants.DEFAULT_PINNED_VALUE)
                }
            })
        }
        else{
            AppConstants.notifyUser(requireContext(),"Unable to load Task")
        }

        viewModel.apply {
            title.observe(viewLifecycleOwner, Observer {
                binding.titleEditText.setText(it)
            })

            task.observe(viewLifecycleOwner, Observer {
                binding.taskEditText.setText(it)
            })

            isPinned.observe(viewLifecycleOwner, Observer {
                val imageValue = if (it) {
                    R.drawable.pushpin_selected
                } else {
                    R.drawable.pushpin_unselected
                }
                binding.pinButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        imageValue
                    )
                )
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}