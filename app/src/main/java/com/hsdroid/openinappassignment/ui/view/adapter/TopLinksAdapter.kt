package com.hsdroid.openinappassignment.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hsdroid.openinappassignment.data.models.TopLink
import com.hsdroid.openinappassignment.databinding.ItemListBinding
import com.hsdroid.openinappassignment.utils.Helper.Companion.convertTimeStampToReadableTime
import com.hsdroid.openinappassignment.utils.Helper.Companion.showToast

class TopLinksAdapter : RecyclerView.Adapter<TopLinksAdapter.ViewHolder>() {

    private lateinit var binding: ItemListBinding
    private var topLinksList: List<TopLink>? = null

    inner class ViewHolder(val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopLinksAdapter.ViewHolder {
        binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopLinksAdapter.ViewHolder, position: Int) {
        val currentItem = topLinksList?.get(position)

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
        if (topLinksList == null) return 0
        return topLinksList!!.size
    }

    fun setList(topLinks: List<TopLink>) {
        topLinksList = topLinks
        notifyDataSetChanged()
    }
}