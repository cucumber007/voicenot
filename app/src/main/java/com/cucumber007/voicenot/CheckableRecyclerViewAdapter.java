package com.cucumber007.voicenot;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class CheckableRecyclerViewAdapter<T extends CheckableRecyclerViewAdapter.CheckableAdapterItem & Comparable> extends RecyclerView.Adapter<CheckableRecyclerViewAdapter.AppViewHolder> {

    private Context context;
    private final OnItemCheckListener listener;
    private List<CheckableItem> items;
    private HashMap<Integer, CheckableItem> checkedItems = new HashMap<>();


    public CheckableRecyclerViewAdapter(Context context, List<T> data, OnItemCheckListener listener) {
        this.context = context;
        this.listener = listener;
        Observable.from(data).map(CheckableItem::new).toList().subscribe(list->items = list);
    }

    @Override
    public CheckableRecyclerViewAdapter.AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppViewHolder(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.item_app, parent, false));
    }

    @Override
    public void onBindViewHolder(CheckableRecyclerViewAdapter.AppViewHolder holder, int position) {
        holder.checkBox.setText(getItem(position).getObject().getTitle());
        holder.imageView.setImageDrawable(getItem(position).getObject().getImage());
        holder.checkBox.setOnClickListener(view-> {
            boolean checked = holder.checkBox.isChecked();
            getItem(position).setChecked(checked);
            if (checked) {
                if (!checkedItems.containsKey(position))
                    checkedItems.put(position, getItem(position));
            } else {
                if (checkedItems.containsKey(position))
                    checkedItems.remove(position);
            }
            listener.check(position, checked);
        });
    }

    public int getCheckedCount() {
        return checkedItems.keySet().size();
    }

    public List<T> getCheckedItems() {
        List<T> ret = new ArrayList<T>();
        Observable.from(checkedItems.values()).map(CheckableItem::getObject).toList().subscribe(ret::addAll);
        return ret;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private CheckableItem getItem(int position) {
        return items.get(position);
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.checkBox) CheckBox checkBox;
        @BindView(R.id.imageView) ImageView imageView;

        public AppViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class CheckableItem {
        private boolean checked;
        private T object;

        public CheckableItem(T object) {
            this.object = object;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public T getObject() {
            return object;
        }
    }

    public interface CheckableAdapterItem {
        String getTitle();
        Drawable getImage();
    }

    public interface OnItemCheckListener {
        void check(int position, boolean state);
    }
}
