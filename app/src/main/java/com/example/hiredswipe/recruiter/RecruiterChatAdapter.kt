package com.example.hiredswipe.recruiter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.R

class RecruiterChatAdapter(val context: Context, var candidateChatList: ArrayList<CandidateChatObject>) : RecyclerView.Adapter<RecruiterChatAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.username_chat_entry, parent, false)
        return UserViewHolder(view)
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = candidateChatList[position]
        holder.textName.text = currentUser.firstName

        holder.itemView.setOnClickListener {
//            val intent = Intent(context, ChatActivity::class.java)
//            intent.putExtra("firstName", currentUser.firstName)
//            intent.putExtra("uid", currentUser.uid)
//            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return candidateChatList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.tvUserName)

    }
}