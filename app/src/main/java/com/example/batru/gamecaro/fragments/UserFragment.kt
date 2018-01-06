package com.example.batru.gamecaro.fragments

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.batru.gamecaro.R
import com.example.batru.gamecaro.`interface`.IUserFragment
import com.example.batru.gamecaro.`interface`.IUserHandler
import com.example.batru.gamecaro.adapter.UserAdapter
import com.example.batru.gamecaro.models.User

class UserFragment : BaseFragment(), IUserHandler {
    private val mUserFragmentTag = "UserFragment"

    private lateinit var mRecyclerUser: RecyclerView
    private val mUsers: ArrayList<User> = arrayListOf()
    private lateinit var mListener: IUserFragment

    private lateinit var mAdapter: UserAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mDivider: DividerItemDecoration

    lateinit var mCancelButton: Button

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mListener = context as IUserFragment
        } catch (e: ClassCastException) {
            Log.d(mUserFragmentTag, "The activity does not implement listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_users, container, false)

        mRecyclerUser = rootView.findViewById(R.id.revUsers)
        mAdapter = UserAdapter(activity, mUsers)
        mAdapter.setListener(this as IUserHandler)
        mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mRecyclerUser.layoutManager = mLayoutManager
        mDivider = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)
        mRecyclerUser.addItemDecoration(mDivider)
        mRecyclerUser.adapter = mAdapter

        mCancelButton = rootView.findViewById(R.id.btnExit)
        mCancelButton.setOnClickListener {
            mListener.cancelGame()
        }

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

    override fun clickListener(view: View, position: Int) {
        val user = mUsers[position]
        mListener.itemClick(user)
    }
}