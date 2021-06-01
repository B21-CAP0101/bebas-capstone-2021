package com.capstone101.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.capstone101.core.R
import com.capstone101.core.databinding.ModelRelativeBinding
import com.capstone101.core.domain.model.User
import com.capstone101.core.utils.Function.glide

class RelativeAdapter(private val typeRelative: String) :
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
                    tvName.text = name ?: username
                    tvUsername.text = username

                    if (gender == false) {
                        itemView.glide("", svProfile, R.drawable.ic_female_avatar)
                    } else {
                        itemView.glide("", svProfile, R.drawable.ic_male_avatar)
                    }

                    when (typeRelative) {
                        "inviting" -> {
                            tbAdd.isVisible = true

                            tbAdd.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) {
                                    // TODO : TAMBAHKAN MENJADI TEMAN/RELATIVE
                                } else {
                                    // TODO : MEMBATALAKAN PERMINTAAN PERTEMANAN
                                }
                            }
                        }


                        "invited" -> {
                            btnConfirm.isVisible = true
                            btnCancel.isVisible = true


                            btnConfirm.setOnClickListener {
                                // TODO: BUAT CONFIRMASI UNTUK MENERIMA USER YANG MELAKUKAN INVITING
                            }

                            btnCancel.setOnClickListener {
                                // TODO: HAPUS USER YANG MELAKUKAN IVITING
                            }

                        }
                        else -> {
                            btnConfirm.isVisible = false
                            btnCancel.isVisible = false
                            tbAdd.isVisible = false
                        }
                    }

                    tbAdd.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            // TODO:
                        } else {

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