package com.example.batru.gamecaro.fragments

import android.app.AlertDialog
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
import com.example.batru.gamecaro.ui.LoginActivity.Companion.mSocket

class UserFragment : BaseFragment(), IUserHandler {
    private lateinit var mRecyclerUser: RecyclerView
    private val mUsers: ArrayList<User> = arrayListOf()

    private lateinit var mAdapter: UserAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mDivider: DividerItemDecoration

    private val labelSendRequest = "USER_A_SEND_REQUEST"

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

    fun enableRecyclerView(isEnable: Boolean) {
        mRecyclerUser.isEnabled = isEnable
    }

    override fun clickListener(view: View, position: Int) {
        val user = mUsers[position]
        if (user.Email == GameActivity.mEmail) {
            toast(activity, resources.getString(R.string.cannot_send_a_request))
            return
        }
        val dialog = AlertDialog.Builder(activity)
                .setTitle(resources.getString(R.string.send_a_request))
                .setNegativeButton(resources.getString(R.string.no)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    mSocket.emit(labelSendRequest, user.SocketId)
                    enableRecyclerView(false)
                }
        dialog.show()
    }
}