package com.demo.android.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.adapters.CategoryAdapter;
import com.demo.android.helpers.API.FetchHelper;
import com.demo.android.interfaces.OnCallback;
import com.demo.android.interfaces.OnItemClickListener;
import com.demo.android.models.Category;
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

public class CategoryActivity extends BaseRecyclerActivity implements OnItemClickListener {
    private List<Category> categories = new ArrayList<>();
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.primary_btn).setOnClickListener(v -> {
            createOrEditAlertDialog("Создание", false, 0);
        });
    }

    private void createOrEditAlertDialog(String titleCategory, boolean isEdit, int id) {
        View view = LayoutInflater.from(this).inflate(R.layout.category_edit_or_create_form, null);
        TextInputEditText title = view.findViewById(R.id.title);
        title.addTextChangedListener(new Validator(view.findViewById(R.id.titleBox), false));

        if (isEdit) {
            title.setText(titleCategory);
        }
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(titleCategory)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(dialog1 -> {
            Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(view1 -> {
                // TODO Do something
//                if (validate(title)) {
//                    fetchSave(title.getText().toString(), id);
//                    dialog1.cancel();
//                }
                fetchSave(title.getText().toString(), id);
                dialog1.cancel();
            });
        });
        dialog.show();
    }

    private void fetchSave(String title, int id) {
        FetchHelper.fetchCreateOrEditCategory(id, title, new OnCallback() {
            @Override
            public void OnSuccess(Response response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        recyclerView.getAdapter().notifyItemRemoved(position);
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            Category category = new Category();
                            category.setTitle(title);
                            category.setId(id == 0 ? jsonObject.getInt("id") : id);

                            if (id > 0) {
                                categories.set(position, category);
                                recyclerView.getAdapter().notifyItemChanged(position);
                            } else {
                                categories.add(category);
                                recyclerView.getAdapter().notifyItemInserted(categories.size() - 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void OnError(Call response) { }
        });
    }

    protected void fetchItems() {
        FetchHelper.fetchCategories(new OnCallback() {
            @Override
            public void OnSuccess(Response response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray array = jsonObject.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            setData(object);
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
            public void OnError(Call response) { }
        });
    }

    @Override
    protected void fetchDelete(int id) {
        FetchHelper.fetchDeleteCategory(id, new OnCallback() {
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
                            categories.remove(position);
                            recyclerView.getAdapter().notifyItemRemoved(position);
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void OnError(Call response) { }
        });
    }

    @Override
    protected void setAdapter() {
        super.setAdapter();
        recyclerView.setAdapter(new CategoryAdapter(categories, this));
    }

    @Override
    protected void setData(JSONObject object) throws JSONException {
        Category category = new Category();
        category.setTitle(object.getString("title"));
        category.setId(object.getInt("id"));
        categories.add(category);
    }

    @Override
    public void onClick(RecyclerView.ViewHolder viewHolder, int position, int id) {
        this.position = position;
        createOrEditAlertDialog(categories.get(position).getTitle(), true, id);
    }

    @Override
    public void onLongClick(RecyclerView.ViewHolder viewHolder, int position, int id) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление")
                .setMessage("При удалении все товары в этой категории будут так же удалены")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    fetchDelete(id);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
