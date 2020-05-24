package com.demo.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.activity.UsersActivity;
import com.demo.android.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> users;
    private UsersActivity usersActivity;

    public UserAdapter(List<User> users, UsersActivity usersActivity) {
        this.users = users;
        this.usersActivity = usersActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(Application.getInstance()).inflate(R.layout.users_recycler_content, null);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(v -> this.usersActivity.onClick(
                viewHolder,
                viewHolder.getAdapterPosition(),
                this.users.get(viewHolder.getAdapterPosition()).getId()
        ));
        view.setOnLongClickListener(v -> {
            this.usersActivity.onLongClick(
                    viewHolder,
                    viewHolder.getAdapterPosition(),
                    this.users.get(viewHolder.getAdapterPosition()).getId()
            );
            return true;
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = this.users.get(holder.getAdapterPosition());
        holder.name.setText(user.getLogin());
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
        }
    }
}
