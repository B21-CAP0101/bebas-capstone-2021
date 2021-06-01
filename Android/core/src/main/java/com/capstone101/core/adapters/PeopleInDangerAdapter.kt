package com.capstone101.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.capstone101.core.R
import com.capstone101.core.databinding.ModelPeopleInDangerBinding
import com.capstone101.core.domain.model.User
import com.capstone101.core.utils.Function.glideWithLoading

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

    private var onItemClickListener: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }


    inner class PeopleInDangerViewHolder(private val bind: ModelPeopleInDangerBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun bind(user: User) {
            bind.apply {
                with(user) {
                    tvUsername.isVisible = false
                    svProfile.isVisible = false
                    progressbar.isVisible = true

                    tvUsername.text = username
                    itemView.glideWithLoading(
                        "",
                        svProfile,
                        if (gender == false) R.drawable.ic_female_avatar else R.drawable.ic_male_avatar,
                        progressbar, listOf(tvUsername, svProfile)
                    )
                    itemView.setOnClickListener {
                        onItemClickListener?.let { click ->
                            click(this)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PeopleInDangerAdapter.PeopleInDangerViewHolder =
        PeopleInDangerViewHolder(
            ModelPeopleInDangerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PeopleInDangerViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    override fun getItemCount(): Int = differ.currentList.size
}