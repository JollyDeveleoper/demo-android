package com.demo.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.activity.CategoryActivity;
import com.demo.android.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private CategoryActivity categoryActivity;

    public CategoryAdapter(List<Category> categories, CategoryActivity categoryActivity) {
        this.categories = categories;
        this.categoryActivity = categoryActivity;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(Application.getInstance()).inflate(R.layout.categories_recycler_content, null);
        ViewHolder viewHolder = new ViewHolder(v);
        v.setOnClickListener(v1 -> categoryActivity.onClick(viewHolder, viewHolder.getAdapterPosition(), categories.get(viewHolder.getAdapterPosition()).getId()));
        v.setOnLongClickListener(v2 -> {
            categoryActivity.onLongClick(viewHolder, viewHolder.getAdapterPosition(), categories.get(viewHolder.getAdapterPosition()).getId());
            return true;
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = categories.get(holder.getAdapterPosition());
        holder.title.setText(category.getTitle());
    }

    @Override
    public int getItemCount() {
        return this.categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }
}
