package com.cucumber007.voicenot.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.cucumber007.reusables.recycler.adapters.BaseRecyclerAdapter;
import com.cucumber007.voicenot.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SelectableRecyclerAdapter<T, V extends RecyclerView.ViewHolder> extends BaseRecyclerAdapter<T, V> {

    private Set<Integer> selectedPositions = new HashSet<>();
    private RecyclerView recyclerView;
    private OnItemSelectedListener<T> onItemSelectedListener;

    public SelectableRecyclerAdapter(Context context) {
        super(context, R.layout.item_app);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void bindViewHolder(V holder, T item, int position) {
        holder.itemView.setOnClickListener(view -> {
            notifyItemSelectedChange(holder, item, position);
        });
    }

    protected boolean isItemSelected(int position) {
        return selectedPositions.contains(position);
    }

    protected void notifyItemSelectedChange(V holder, T item, int position) {
        boolean isSelected = !isItemSelected(position);
        holder.itemView.setSelected(isSelected);
        if(isSelected) selectedPositions.add(position);
        else selectedPositions.remove(position);
        if(getOnItemSelectedListener() != null) {
            getOnItemSelectedListener().onOptionClick(item, isSelected, selectedPositions, getItems());
        }
    }

    public void setSelectedPositions(Set<Integer> selectedPositions) {
        this.selectedPositions = selectedPositions;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public OnItemSelectedListener<T> getOnItemSelectedListener() {
        return onItemSelectedListener;
    }

    public interface OnItemSelectedListener<B> {
        void onOptionClick(B item, boolean isSelected, Set<Integer> selectedPositions, List<B> items);
    }
}
