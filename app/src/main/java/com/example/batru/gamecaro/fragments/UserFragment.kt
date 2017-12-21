package com.example.batru.gamecaro.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.adapter.UserAdapter
import com.example.batru.gamecaro.models.User

class UserFragment : Fragment() {
    private lateinit var mRecyclerUser: RecyclerView
    private val mUsers: ArrayList<User> = arrayListOf(
            User("Bucky", "bucky@gmail.com", 200),
            User("Bacon", "bacon@gmail.com", 100),
            User("Bucky", "bucky@gmail.com", 200),
            User("Bacon", "bacon@gmail.com", 100),
            User("Bucky", "bucky@gmail.com", 200),
            User("Bacon", "bacon@gmail.com", 100),
            User("Bucky", "bucky@gmail.com", 200),
            User("Bacon", "bacon@gmail.com", 100))
    private lateinit var mAdapter: UserAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mDivider: DividerItemDecoration

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_users, container, false)

        mRecyclerUser = rootView.findViewById(R.id.revUsers)
        mAdapter = UserAdapter(context, mUsers)
        mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRecyclerUser.layoutManager = mLayoutManager
        mDivider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        mRecyclerUser.addItemDecoration(mDivider)
        mRecyclerUser.adapter = mAdapter

        return rootView
    }
}