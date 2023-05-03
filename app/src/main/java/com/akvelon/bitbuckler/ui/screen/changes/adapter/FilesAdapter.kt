/**
 * All rights reserved by Akvelon Inc.
 *
 * Created by Ignat Krasnov (ignat.krasnov@akvelon.com)
 * on 12 January 2021
 */

package com.akvelon.bitbuckler.ui.screen.changes.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.databinding.ItemFileBinding
import com.akvelon.bitbuckler.extension.getFormattingPath
import com.akvelon.bitbuckler.extension.getStatusIcon
import com.akvelon.bitbuckler.extension.getString
import com.akvelon.bitbuckler.extension.hide
import com.akvelon.bitbuckler.extension.inflate
import com.akvelon.bitbuckler.extension.show
import com.akvelon.bitbuckler.model.entity.pullrequest.diff.file.FileStat
import com.akvelon.bitbuckler.ui.base.BaseViewHolder

class FilesAdapter(
    private val files: List<FileStat>,
    private val onFileClickListener: (Int) -> Unit
) : RecyclerView.Adapter<FilesAdapter.FileHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FileHolder(parent.inflate(R.layout.item_file))

    override fun onBindViewHolder(holder: FileHolder, position: Int) =
        holder.bind(files[position])

    override fun getItemCount() = files.size

    inner class FileHolder(itemView: View) : BaseViewHolder(itemView) {
        private val binding by viewBinding(ItemFileBinding::bind)

        init {
            itemView.setOnClickListener {
                onFileClickListener(bindingAdapterPosition * 2)
            }
        }

        fun bind(fileStat: FileStat) =
            with(binding) {
                fileStatus.setImageResource(fileStat.status.getStatusIcon())
                filePath.text = fileStat.getFormattingPath()

                if (fileStat.isMergeConflict) {
                    isMergeConflict.show()
                    linesAdded.hide()
                    linesRemoved.hide()
                } else {
                    isMergeConflict.hide()

                    when (fileStat.linesAdded) {
                        0 -> linesAdded.hide()
                        else -> {
                            linesAdded.text =
                                itemView.getString(R.string.lines_added, fileStat.linesAdded)
                            linesAdded.show()
                        }
                    }

                    when (fileStat.linesRemoved) {
                        0 -> linesRemoved.hide()
                        else -> {
                            linesRemoved.text =
                                itemView.getString(R.string.lines_removed, fileStat.linesRemoved)
                            linesRemoved.show()
                        }
                    }
                }
            }
    }
}
