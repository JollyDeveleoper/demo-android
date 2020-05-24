package com.demo.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.android.R;
import com.demo.android.helpers.API.FetchHelper;
import com.demo.android.interfaces.OnCallback;
import com.demo.android.interfaces.OnItemClickListener;
import com.demo.android.models.Role;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class RolesActivity extends Activity implements OnItemClickListener {
    private List<Role> roles;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.base_recycler);
    }

    @Override
    public void onClick(RecyclerView.ViewHolder viewHolder, int position, int id) {
        Intent intent = new Intent(this, CreateOrEditRolesActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onLongClick(RecyclerView.ViewHolder viewHolder, int position, int id) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление")
                .setMessage(String.format("Удалить роль \"%s\"?", this.roles))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    deleteItem(id, position);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void deleteItem(int id, int position) {
        FetchHelper.fetchDeleteRole(id, new OnCallback() {
            @Override
            public void OnSuccess(Response response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        recyclerView.getAdapter().notifyItemRemoved(position);
                        roles.remove(position);
                    });
                }
            }

            @Override
            public void OnError(Call response) {
            }
        });
    }
}
