package com.hsdroid.openinappassignment.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hsdroid.openinappassignment.data.models.RecentLink
import com.hsdroid.openinappassignment.databinding.ItemListBinding
import com.hsdroid.openinappassignment.utils.Helper.Companion.convertTimeStampToReadableTime
import com.hsdroid.openinappassignment.utils.Helper.Companion.showToast

class RecentLinksAdapter : RecyclerView.Adapter<RecentLinksAdapter.ViewHolder>() {

    private lateinit var binding: ItemListBinding
    private var recentLink: List<RecentLink>? = null

    inner class ViewHolder(val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentLinksAdapter.ViewHolder {
        binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentLinksAdapter.ViewHolder, position: Int) {
        val currentItem = recentLink?.get(position)

        with(holder.binding) {
            tvName.text = currentItem?.title
            tvClicks.text = currentItem?.total_clicks.toString()
            tvDate.text = convertTimeStampToReadableTime(currentItem?.created_at)
            tvLink.text = currentItem?.web_link

            tvLink.setOnClickListener {
                showToast(it.context, "Clicked")
            }
        }
    }

    override fun getItemCount(): Int {
        if (recentLink == null) return 0
        return recentLink!!.size
    }

    fun setList(topLinks: List<RecentLink>) {
        recentLink = topLinks
        notifyDataSetChanged()
    }
}