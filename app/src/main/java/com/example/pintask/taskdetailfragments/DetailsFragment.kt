package com.example.pintask.taskdetailfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.pintask.R
import com.example.pintask.constants.AppConstants
import com.example.pintask.databinding.FragmentDetailsBinding
import com.example.pintask.firebase.FirestoreFunctions
import com.example.pintask.model.DetailsTaskViewModel
import com.example.pintask.model.TaskModel

private const val TAG = "DetailsFragment tag"

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private val viewModel: DetailsTaskViewModel by activityViewModels()
    private var docID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        docID = requireActivity().intent.getStringExtra(AppConstants.KEY_TASK_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCurrentTask()
        applyBinding()
        applyObservers()

    }

    private fun applyObservers() {
        viewModel.apply {
            isPinned.observe(viewLifecycleOwner, Observer {
                binding.pinButton.setImageResource(
                    if (it) R.drawable.pushpin_selected
                    else R.drawable.pushpin_unselected
                )
            })
        }
    }

    private fun applyBinding() {
        binding.apply {
            pinButton.setOnClickListener {
                viewModel.setPinnedStatus(!viewModel.isPinned.value!!)
            }

            backButton.setOnClickListener {
                requireActivity().finish()
            }

            deleteTaskButton.setOnClickListener {
                FirestoreFunctions.deleteTask(requireContext(), docID!!) {
                    requireActivity().finish()
                }
            }

            saveTask.setOnClickListener {

                FirestoreFunctions.updateTask(
                    requireContext(),
                    docID!!,
                    TaskModel(
                        if(titleEditText.text.toString().trim().isNullOrEmpty()) AppConstants.DEFAULT_TASK_TITLE
                        else titleEditText.text.toString().trim(),
                        taskEditText.text.toString(),
                        viewModel.isPinned.value), closeCurrentActivity = {
                        requireActivity().finish()
                    }
                )
            }
        }
    }

    private fun getCurrentTask() {
        if (docID != null) {
            FirestoreFunctions.getTask(requireContext(), docID!!, returnTask = {
                val currentTask = it.toObject(TaskModel::class.java)
                viewModel.apply {
                    setPinnedStatus(currentTask!!.pinned ?: AppConstants.DEFAULT_PINNED_VALUE)
                }
                binding.apply {
                    titleEditText.setText(currentTask!!.taskTitle)
                    taskEditText.setText(currentTask.task)
                }
            })
        } else {
            AppConstants.notifyUser(requireContext(), "Unable to load Task")
        }
    }
}