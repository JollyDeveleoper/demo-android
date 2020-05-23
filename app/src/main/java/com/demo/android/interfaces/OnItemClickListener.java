package com.demo.android.interfaces;

import androidx.recyclerview.widget.RecyclerView;

public interface OnItemClickListener {
    void onClick(RecyclerView.ViewHolder viewHolder, int position, int id);
    void onLongClick(RecyclerView.ViewHolder viewHolder, int position, int id);
}
