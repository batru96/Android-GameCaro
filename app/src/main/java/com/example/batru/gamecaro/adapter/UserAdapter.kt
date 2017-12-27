package com.example.batru.gamecaro.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.`interface`.IUserHandler
import com.example.batru.gamecaro.models.User

class UserAdapter(private var context: Context, private var users: ArrayList<User>) :
        RecyclerView.Adapter<UserAdapter.ItemHolder>() {

    private var listener: IUserHandler? = null

    fun setListener(listener: IUserHandler) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: ItemHolder?, position: Int) {
        val user = users[position]
        holder!!.tvName.text = user.Name
        holder.tvEmail.text = user.Email
        val pointsStr = context.resources.getString(R.string.points, user.Points)
        holder.tvPoints.text = pointsStr
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemHolder {
        val view: View = LayoutInflater.from(context)
                .inflate(R.layout.item_user, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int = users.size

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        override fun onClick(v: View?) {
            Toast.makeText(context, "Hello moi nguoi", Toast.LENGTH_SHORT).show()
            listener!!.clickListener(v!!, layoutPosition)
        }

        val tvName: TextView = view.findViewById(R.id.tvUsername)
        val tvEmail: TextView = view.findViewById(R.id.tvUserEmail)
        val tvPoints: TextView = view.findViewById(R.id.tvPoints)

        init {
            view.setOnClickListener(this)
        }
    }
}