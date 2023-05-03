package com.akvelon.bitbuckler.ui.screen.account.switchflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.akvelon.bitbuckler.R
import com.akvelon.bitbuckler.extension.loadCircle
import com.akvelon.bitbuckler.model.entity.User

class UserAccountsAdapter(
    private val users: List<User>,
    private val onItemClickListener: (User) -> Unit,
    private val onRemoveClickListener: (User) -> Unit
): RecyclerView.Adapter<UserAccountsAdapter.ViewHolder>() {

    private lateinit var inflater: LayoutInflater

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        inflater = LayoutInflater.from(recyclerView.context)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val container = itemView.findViewById<ConstraintLayout>(R.id.container)
        val userImage = itemView.findViewById<ImageView>(R.id.userImage)
        val userName = itemView.findViewById<TextView>(R.id.userName)
        val nickname = itemView.findViewById<TextView>(R.id.userNickName)
        val remove = itemView.findViewById<AppCompatImageView>(R.id.removeAccount)

        fun bind(user: User) {
            userName.text = user.username
            nickname.text = user.nickname
            userImage.loadCircle(user.links.avatar.href, R.drawable.ic_avatar_placeholder)

            container.setOnClickListener {
                onItemClickListener.invoke(user)
            }

            remove.isVisible = true
            remove.setOnClickListener {
                onRemoveClickListener.invoke(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_account, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(user = users[position])
    }

    override fun getItemCount() = users.size
}