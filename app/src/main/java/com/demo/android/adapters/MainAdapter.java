package com.demo.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.activity.MainActivity;
import com.demo.android.models.Item;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Item> items;
    private MainActivity mainActivity;

    public MainAdapter(List<Item> items, MainActivity mActivity) {
        this.items = items;
        this.mainActivity = mActivity;
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(Application.getInstance()).inflate(R.layout.main_recycler_content, null);
        ViewHolder viewHolder = new ViewHolder(v);
        v.setOnClickListener(v1 -> this.mainActivity.onClick(viewHolder, viewHolder.getAdapterPosition(), this.items.get(viewHolder.getAdapterPosition()).getId()));
        v.setOnLongClickListener(v1 -> {
            this.mainActivity.onLongClick(viewHolder, viewHolder.getAdapterPosition(), this.items.get(viewHolder.getAdapterPosition()).getId());
            return true;
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        Item item = items.get(holder.getAdapterPosition());
        String text = item.isSales() ? item.getTitle().concat(" (Продано)") : item.getTitle();
        holder.title.setText(text);
        holder.description.setText(item.getDescription());
        Glide.with(Application.getInstance())
                .load(item.getPreviewLink())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.preview);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        ImageView preview;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            preview = itemView.findViewById(R.id.preview);
        }
    }
}
