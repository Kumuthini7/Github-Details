package com.example.repository.ui.repository

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.RecyclerView
import com.example.repository.R
import com.example.repository.common.constant.Constants
import com.example.repository.common.utils.ImageLoader
import com.example.repository.models.Repository
import kotlinx.android.synthetic.main.item_repo_list.view.*


class RepoAdapter(
    private var itemList: List<Repository>
) :
    RecyclerView.Adapter<RepoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_repo_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    override fun getItemCount(): Int = itemList.size

    private fun getItem(position: Int): Repository {
        return itemList[position]
    }

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        fun bindData(item: Repository) {
            itemView.tv_name.text = item.name?.capitalize()
            itemView.tv_id.text = item.nodeId
            itemView.type.text = item.owner?.type
            val imgLoader = ImageLoader()
            item.owner?.avatarUrl?.let {
                imgLoader.DisplayImage(
                    it,
                    R.drawable.ic_loading,
                    itemView.iv_avatar
                )
            }

            //  picasso.load(item.owner?.avatarUrl)?.fit()?.into(itemView.iv_avatar)

            itemView.setOnClickListener {
                val comments = itemView.et_comments.text.toString()
                val intent = Intent(itemView.context, RepoDetailActivity::class.java)
                intent.putExtra(Constants.COMMENT, comments)
                intent.putExtra(Constants.ID, item.id.toString())
                intent.putExtra(Constants.NAME, item.fullName)
                itemView.context.startActivity(intent)
            }

            itemView.et_comments.setOnEditorActionListener { v, actionId, event ->

                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        itemView.et_comments.clearFocus()
                    }
                    else -> {

                    }
                }

                false
            }
        }
    }


}