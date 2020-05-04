package com.cucumber007.voicenot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_base.view.*
import java.util.*

abstract class BaseAdapter<T : Any, VH : BaseAdapter.BaseVh<T>> : RecyclerView.Adapter<VH>() {

    var onItemClickListener: ((T) -> Unit)? = null

    val items: MutableList<T> = mutableListOf<T>()

    fun updateItems(_items: List<T>) {
        items.clear()
        addItemsAndUpdate(_items)
    }

    fun addItemsAndUpdate(_items: List<T>) {
        items.addAll(_items)
        notifyDataSetChanged()
    }

    open val itemLayoutResource: Int = R.layout.item_app

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return createViewHolder(
            LayoutInflater.from(parent.context).inflate(
                itemLayoutResource,
                parent,
                false
            )
        ) { position ->
            onItemClickListener?.invoke(items[position])
        }
    }

    open fun createViewHolder(view: View, baseClickListener: ((Int) -> Unit)): VH {
        return BaseVh<T>(view, baseClickListener) as VH
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    open class BaseVh<R : Any>(val view: View, clickListener: ((Int) -> Unit)) :
        RecyclerView.ViewHolder(view), LayoutContainer {

        private lateinit var item: R

        init {
            view.setOnClickListener {
                clickListener.invoke(adapterPosition)
            }
        }

        open fun bind(item: R) {
            view.text?.let {
                it.text = item.toString()
            }
        }

        override val containerView = view
    }

}

