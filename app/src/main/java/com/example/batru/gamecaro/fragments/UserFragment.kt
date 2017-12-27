package com.example.batru.gamecaro.fragments

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.`interface`.IUserHandler
import com.example.batru.gamecaro.adapter.UserAdapter
import com.example.batru.gamecaro.models.User
import com.example.batru.gamecaro.ui.GameActivity

class UserFragment : BaseFragment(), IUserHandler {
    private lateinit var mRecyclerUser: RecyclerView
    private val mUsers: ArrayList<User> = arrayListOf()

    private lateinit var mAdapter: UserAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mDivider: DividerItemDecoration

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_users, container, false)

        val mUserHandler = this as IUserHandler

        mRecyclerUser = rootView.findViewById(R.id.revUsers)
        mAdapter = UserAdapter(activity, mUsers)
        mAdapter.setListener(mUserHandler)
        mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mRecyclerUser.layoutManager = mLayoutManager
        mDivider = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)
        mRecyclerUser.addItemDecoration(mDivider)
        mRecyclerUser.adapter = mAdapter

        val button = rootView.findViewById<Button>(R.id.btnExit)
        button.setOnClickListener { exit() }
        return rootView
    }

    fun addNewUser(user: User) {
        mUsers.add(user)
        mAdapter.notifyItemInserted(mUsers.size - 1)
    }

    fun removeUser(email: String) {
        val user = mUsers.find { user -> user.Email == email }
        val index = mUsers.indexOf(user)
        mUsers.removeAt(index)
        mAdapter.notifyItemRemoved(index)
    }

    private fun exit() {
        (activity as GameActivity).onBackPressed()
    }

    override fun clickListener(view: View, position: Int) {
        toast(activity, position.toString())
    }
}