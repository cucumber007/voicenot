package com.cucumber007.voicenot.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.cucumber007.voicenot.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppsCheckableRecyclerAdapter extends SelectableRecyclerAdapter<AppItem, AppsCheckableRecyclerAdapter.VhApp> {

    public AppsCheckableRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public VhApp createViewHolder(View view) {
        return new VhApp(view);
    }

    @Override
    public void bindViewHolder(VhApp holder, AppItem item, int position) {
        holder.checkBox.setText(item.getTitle());
        holder.checkBox.setChecked(isItemSelected(position));
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            notifyItemSelectedChange(holder, item, position);
        });
        holder.imageView.setImageDrawable(item.getImage());
    }

    static class VhApp extends RecyclerView.ViewHolder {
        @BindView(R.id.checkBox) CheckBox checkBox;
        @BindView(R.id.imageView) ImageView imageView;

        public VhApp(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
