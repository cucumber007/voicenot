package com.cucumber007.voicenot.view

import android.view.View
import com.cucumber007.voicenot.BaseAdapter
import com.cucumber007.voicenot.R
import kotlinx.android.extensions.LayoutContainer
import java.util.*

class AppsAdapter : BaseAdapter<AppItem, AppsAdapter.VhApp>() {

    override val itemLayoutResource = R.layout.item_app

    private var selectedPositions: MutableSet<Int> = HashSet()
    private var onItemSelectedListener: OnItemSelectedListener<AppItem>? = null

    override fun createViewHolder(view: View, baseClickListener: (Int) -> Unit): VhApp {
        return VhApp(view) { position ->
            notifyItemSelectedChange(view, items[position], position);
        }
    }

    protected fun isItemSelected(position: Int): Boolean {
        return selectedPositions.contains(position)
    }

    protected fun notifyItemSelectedChange(view: View, item: AppItem, position: Int) {
        val isSelected = !isItemSelected(position)
        view.setSelected(isSelected)
        if (isSelected) selectedPositions.add(position) else selectedPositions.remove(position)
        if (getOnItemSelectedListener() != null) {
            getOnItemSelectedListener()?.onOptionClick(item, isSelected, selectedPositions, items)
        }
    }

    class VhApp(override val containerView: View, val clickListener: (Int) -> Unit)
        : BaseAdapter.BaseVh<AppItem>(containerView, {}), LayoutContainer {

        override fun bind(item: AppItem) {
            containerView.setOnClickListener {
                clickListener.invoke(adapterPosition)
            }
        }
    }

    fun setSelectedPositions(selectedPositions: MutableSet<Int>) {
        this.selectedPositions = selectedPositions
    }

    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener<AppItem>?) {
        this.onItemSelectedListener = onItemSelectedListener
    }

    fun getOnItemSelectedListener(): OnItemSelectedListener<AppItem>? {
        return onItemSelectedListener
    }

    interface OnItemSelectedListener<B> {
        fun onOptionClick(item: B, isSelected: Boolean, selectedPositions: Set<Int?>?, items: List<B>?)
    }
}