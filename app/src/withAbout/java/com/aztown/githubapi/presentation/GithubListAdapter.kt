package com.aztown.githubapi.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aztown.githubapi.databinding.RvItemRepositoryBinding
import com.aztown.githubapi.domain.entity.GitRepoEntity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import javax.inject.Inject

class GithubListAdapter @Inject constructor() :
    PagingDataAdapter<GitRepoEntity, GithubListAdapter.GithubListViewHolder>(GithubDiffCallback()) {

    var onRepoClickListener: ((username: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GithubListViewHolder {
        val binding = RvItemRepositoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GithubListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: GithubListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        with(holder.binding) {
            tvName.text = item.name ?: DEFAULT_STRING
            tvDescriotion.text = item.description ?: DEFAULT_STRING
            tvLastChangesDate.text = item.lastChange ?: DEFAULT_STRING
            tvStarsCount.text = item.starsCount.toString()
            Glide.with(ivAvatar)
                .load(item.avatarUrl)
                .apply(RequestOptions().override(PICTURE_SIZE_SQUARE, PICTURE_SIZE_SQUARE))
                .into(ivAvatar)
            root.setOnClickListener {
                item.ownerUsername?.let {
                    onRepoClickListener?.invoke(it)
                }
            }
        }
    }

    inner class GithubListViewHolder(
        val binding: RvItemRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val DEFAULT_STRING = "Unknown"
        private const val PICTURE_SIZE_SQUARE = 216
    }
}

class GithubDiffCallback : DiffUtil.ItemCallback<GitRepoEntity>() {
    override fun areItemsTheSame(oldItem: GitRepoEntity, newItem: GitRepoEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GitRepoEntity, newItem: GitRepoEntity): Boolean {
        return oldItem == newItem
    }
}