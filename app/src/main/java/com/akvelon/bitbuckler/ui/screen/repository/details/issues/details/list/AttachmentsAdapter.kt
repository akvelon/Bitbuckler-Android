package com.akvelon.bitbuckler.ui.screen.repository.details.issues.details.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akvelon.bitbuckler.databinding.ItemAttachmentBinding
import com.akvelon.bitbuckler.model.entity.issueTracker.Attachment

class AttachmentsAdapter : RecyclerView.Adapter<AttachmentsViewHolder>() {

    private var attachments: List<Attachment> = emptyList()
    private var onClickListener: (Attachment) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentsViewHolder {
        val binding = ItemAttachmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttachmentsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttachmentsViewHolder, position: Int) {
        holder.bind(attachments[position], onClickListener)
    }

    override fun getItemCount(): Int {
        return attachments.size
    }

    fun setOnItemClickListener(listener: (Attachment) -> Unit) {
        this.onClickListener = listener
    }

    fun updateData(data: List<Attachment>) {
        this.attachments = data
        this.notifyDataSetChanged()
    }
}

class AttachmentsViewHolder(private val binding: ItemAttachmentBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Attachment, onClickListener: (Attachment) -> Unit) {
        binding.filename.text = item.name
        binding.root.setOnClickListener {
            onClickListener.invoke(item)
        }
    }
}