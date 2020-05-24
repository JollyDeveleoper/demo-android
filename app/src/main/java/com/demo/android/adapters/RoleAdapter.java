package com.demo.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.activity.RolesActivity;
import com.demo.android.models.Role;

import java.util.List;

public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.ViewHolder> {
    private RolesActivity rolesActivity;
    private List<Role> roles;

    public RoleAdapter(List<Role> roles, RolesActivity rolesActivity) {
        this.roles = roles;
        this.rolesActivity = rolesActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(Application.getInstance()).inflate(R.layout.roles_recycler_content, null);
        ViewHolder viewHolder = new ViewHolder(view);
//        view.setOnClickListener(v -> this.rolesActivity.);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return this.roles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
        }
    }
}
