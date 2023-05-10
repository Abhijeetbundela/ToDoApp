package com.bundela.todoapp.adapter

import android.view.LayoutInflater
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bundela.todoapp.R
import com.bundela.todoapp.database.model.TodoData
import com.bundela.todoapp.databinding.ItemTodoBinding
import com.bundela.todoapp.utils.appLogs


class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoVH>() {

    private val differCallback = object : DiffUtil.ItemCallback<TodoData>() {
        override fun areItemsTheSame(oldItem: TodoData, newItem: TodoData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoData, newItem: TodoData): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoVH {
        val binding =
            ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoVH(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var selectedTodo = ArrayList<TodoData>()

    override fun onBindViewHolder(holder: TodoVH, position: Int) {

        val item = differ.currentList[position]
        holder.binding.apply {

            tvTitle.text = item.title ?: ""
            tvDescription.text = item.description ?: ""

            if (selectedTodo.contains(item)) {
                clMain.setBackgroundResource(R.drawable.bg_selected_item)
            } else {
                clMain.setBackgroundResource(R.color.card_color)
            }

            // on item click
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(item, holder.adapterPosition) }
            }

            holder.itemView.setOnLongClickListener {
                onLongClickListener?.let { it(item, holder.adapterPosition) }
                true
            }

        }
    }

    inner class TodoVH(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    // on item click listener
    private var onItemClickListener: ((TodoData, Int) -> Unit)? = null
    private var onLongClickListener: ((TodoData, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (TodoData, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setLongClickListener(listener: (TodoData, Int) -> Unit) {
        onLongClickListener = listener
    }
}
