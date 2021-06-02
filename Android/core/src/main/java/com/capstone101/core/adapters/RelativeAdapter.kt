package com.capstone101.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.capstone101.core.databinding.ModelRelativeBinding
import com.capstone101.core.domain.model.User
import com.capstone101.core.utils.Constant.TYPE_INVITATION
import com.capstone101.core.utils.Constant.TYPE_INVITED
import com.capstone101.core.utils.Constant.TYPE_PURE
import com.capstone101.core.utils.Function.glideGender

class RelativeAdapter(
    private val typeRelative: String,
    private val addCancelCallback: (User, Boolean) -> Unit,
    private val confirmDenyCallback: (User, Boolean) -> Unit
) :
    RecyclerView.Adapter<RelativeAdapter.RelativeAdapterViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username == newItem.username
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    inner class RelativeAdapterViewHolder(private val bind: ModelRelativeBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun bind(user: User) {
            bind.apply {
                with(user) {
                    tvName.text = name
                    tvUsername.text = username
                    itemView.glideGender(photoURL ?: "", svProfile, gender)

                    when (typeRelative) {
                        TYPE_INVITATION -> {
                            tbAdd.isVisible = true

                            tbAdd.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) {
                                    addCancelCallback(this, true)
                                } else {
                                    addCancelCallback(this, false)
                                }
                            }
                        }


                        TYPE_INVITED -> {
                            btnConfirm.isVisible = true
                            btnCancel.isVisible = true


                            btnConfirm.setOnClickListener {
                                confirmDenyCallback(this, true)
                            }

                            btnCancel.setOnClickListener {
                                confirmDenyCallback(this, false)
                            }

                        }
                        TYPE_PURE -> {
                            btnConfirm.isVisible = false
                            btnCancel.isVisible = false
                            tbAdd.isVisible = false
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RelativeAdapter.RelativeAdapterViewHolder =
        RelativeAdapterViewHolder(
            ModelRelativeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: RelativeAdapter.RelativeAdapterViewHolder,
        position: Int
    ) =
        holder.bind(differ.currentList[position])

    override fun getItemCount(): Int = differ.currentList.size
}