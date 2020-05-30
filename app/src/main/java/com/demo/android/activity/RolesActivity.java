package com.demo.android.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.adapters.RoleAdapter;
import com.demo.android.helpers.API.FetchHelper;
import com.demo.android.interfaces.OnCallback;
import com.demo.android.interfaces.OnItemClickListener;
import com.demo.android.models.Role;
import com.demo.android.utils.Validator;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class RolesActivity extends BaseRecyclerActivity implements OnItemClickListener {
    private List<Role> roles = new ArrayList<>();

    private int position;

    @Override
    protected void setAdapter() {
        super.setAdapter();
        recyclerView.setAdapter(new RoleAdapter(roles, this));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.primary_btn).setOnClickListener(v -> {
            createOrEditAlertDialogForm("Создание", "", false, 0);
        });
    }

    @Override
    public void onClick(RecyclerView.ViewHolder viewHolder, int position, int id) {
        String title = roles.get(position).getName();
        String description = roles.get(position).getDescription();
        this.position = position;
        createOrEditAlertDialogForm(title, description, true, id);
    }

    @Override
    public void onLongClick(RecyclerView.ViewHolder viewHolder, int position, int id) {
        this.position = position;
        new AlertDialog.Builder(this)
                .setTitle("Удаление")
                .setMessage("Удалить данную роль?")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> fetchDelete(id))
                .setNegativeButton(android.R.string.cancel, null).show();
    }

    @Override
    protected void fetchItems() {
        FetchHelper.fetchRoles(new OnCallback() {
            @Override
            public void OnSuccess(Response response) {
                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        for (int i = 0; i < array.length(); i++) {
                            setData(array.getJSONObject(i));
                        }
                        runOnUiThread(() -> {
                            loader.setVisibility(View.GONE);
                            setAdapter();
                        });
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void OnError(Call response) {
            }
        });
    }

    @Override
    protected void fetchDelete(int id) {
        FetchHelper.fetchDeleteRole(id, new OnCallback() {
            @Override
            public void OnSuccess(Response response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.has("error")) {
                                Toast.makeText(Application.getInstance(), object.getString("error"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            roles.remove(position);
                            recyclerView.getAdapter().notifyItemRemoved(position);
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void OnError(Call response) {
            }
        });
    }

    @Override
    protected void setData(JSONObject object) throws JSONException {
        Role role = new Role();
        role.setName(object.getString("name"));
        role.setDescription(object.getString("description"));
        role.setId(object.getInt("id"));
        roles.add(role);
    }

    private void fetchSave(String title, String description, int id) {
        FetchHelper.fetchCreateOrEditRole(id, title, description, new OnCallback() {
            @Override
            public void OnSuccess(Response response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            Role role = new Role();
                            role.setName(title);
                            role.setDescription(description);
                            role.setId(id == 0 ? jsonObject.getInt("id") : id);

                            if (id > 0) {
                                roles.set(position, role);
                                recyclerView.getAdapter().notifyItemChanged(position);
                            } else {
                                roles.add(role);
                                recyclerView.getAdapter().notifyItemInserted(roles.size() - 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void OnError(Call response) {
            }
        });
    }

    private void createOrEditAlertDialogForm(String titleRole, String descriptionRole, boolean isEdit, int id) {
        View view = LayoutInflater.from(this).inflate(R.layout.roles_edit_or_create_form, null);
        TextInputEditText title = view.findViewById(R.id.title);
        TextInputEditText description = view.findViewById(R.id.description);
        title.addTextChangedListener(new Validator(view.findViewById(R.id.titleBox), false));
        description.addTextChangedListener(new Validator(view.findViewById(R.id.descriptionBox), false));

        if (isEdit) {
            title.setText(titleRole);
            description.setText(descriptionRole);
        }
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(titleRole)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(dialog1 -> {
            Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(view1 -> {
                TextInputEditText[] fields = {
                        title, description
                };
                if (Validator.validate(fields)) {
                    fetchSave(title.getText().toString(), description.getText().toString(), id);
                    dialog1.cancel();
                }
            });
        });
        dialog.show();
    }
}
