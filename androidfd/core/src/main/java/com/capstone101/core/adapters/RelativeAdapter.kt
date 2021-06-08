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


    private var addCancelCallback: ((User, Boolean) -> Unit)? = null

    private var confirmDenyCallback: ((User, Boolean) -> Unit)? = null

    fun setAddCancelCallBack(listener: (User, Boolean) -> Unit) {
        addCancelCallback = listener
    }

    fun setConfirmDenyCallBack(listener: (User, Boolean) -> Unit) {
        confirmDenyCallback = listener
    }

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
                            btnCancel.isVisible = true

                            btnCancel.setOnClickListener {
                                addCancelCallback?.let { it1 -> it1(this,false) }
                            }
                        }


                        TYPE_INVITED -> {
                            btnConfirm.isVisible = true
                            btnCancel.isVisible = true


                            btnConfirm.setOnClickListener {
                                confirmDenyCallback?.let { it1 -> it1(this, true) }
                            }

                            btnCancel.setOnClickListener {
                                confirmDenyCallback?.let { it1 -> it1(this, false) }
                            }

                        }
                        TYPE_PURE -> {
                            btnConfirm.isVisible = false
                            btnCancel.isVisible = false
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