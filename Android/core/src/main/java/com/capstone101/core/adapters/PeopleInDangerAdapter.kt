package com.capstone101.core.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.capstone101.core.databinding.ModelPeopleInDangerBinding
import com.capstone101.core.domain.model.User


class PeopleInDangerAdapter :
    RecyclerView.Adapter<PeopleInDangerAdapter.PeopleInDangerViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username == newItem.username
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    inner class PeopleInDangerViewHolder(private val bind: ModelPeopleInDangerBinding) :
        RecyclerView.ViewHolder(bind.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleInDangerViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: PeopleInDangerViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}