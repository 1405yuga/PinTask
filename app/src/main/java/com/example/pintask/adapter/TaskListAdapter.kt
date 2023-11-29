package com.example.pintask.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pintask.R
import com.example.pintask.databinding.CardTaskItemBinding
import com.example.pintask.model.TaskModel
import com.google.firebase.firestore.DocumentSnapshot

class TaskListAdapter :
    ListAdapter<DocumentSnapshot, TaskListAdapter.TaskViewHolder>(DiffCallBack) {
    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<DocumentSnapshot>() {
            override fun areItemsTheSame(
                oldItem: DocumentSnapshot,
                newItem: DocumentSnapshot
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: DocumentSnapshot,
                newItem: DocumentSnapshot
            ): Boolean {
                return oldItem.toObject(TaskModel::class.java) == newItem.toObject(TaskModel::class.java)
            }

        }
    }


    class TaskViewHolder(private val binding: CardTaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(documentSnapshot: DocumentSnapshot) {
            //  bind data
            binding.apply {
                val currentTask = documentSnapshot.toObject(TaskModel::class.java)
                titleText.text = currentTask!!.taskTitle
                taskText.text = currentTask!!.task
                val image = if (currentTask.isPinned!!) {
                    R.drawable.pushpin_selected

                } else {
                    R.drawable.pushpin_unselected
                }
                pin.setImageDrawable(ContextCompat.getDrawable(binding.root.context, image))

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            CardTaskItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}