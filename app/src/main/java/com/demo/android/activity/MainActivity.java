package com.demo.android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.adapters.MainAdapter;
import com.demo.android.helpers.API.FetchHelper;
import com.demo.android.helpers.APIHelper;
import com.demo.android.helpers.OkHttpHelper;
import com.demo.android.helpers.PrefsHelper;
import com.demo.android.helpers.RoleHelper;
import com.demo.android.interfaces.OnCallback;
import com.demo.android.interfaces.OnItemClickListener;
import com.demo.android.models.Category;
import com.demo.android.models.Item;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.demo.android.helpers.OkHttpHelper.JSON;

/**
 * Главный экран
 */
public class MainActivity extends BaseRecyclerActivity implements OnItemClickListener {

    private List<Item> items = new ArrayList<>();

    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Если не авторизованы - перебрасываем на авторизацию
        if (PrefsHelper.getAccessToken().isEmpty()) {
            finishAffinity();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        Button addBtn = this.findViewById(R.id.primary_btn);
        if (RoleHelper.isManager()) {
            addBtn.setVisibility(View.GONE);
        } else {
            addBtn.setOnClickListener(v -> startActivity(new Intent(this, CreateOrEditItemActivity.class)));
        }
        findViewById(R.id.settings).setVisibility(View.VISIBLE);
        findViewById(R.id.settings).setOnClickListener(v1 -> startActivity(new Intent(this, MenuActivity.class)));
    }

    @Override
    protected void OnRefresh() {
        items.clear();
        loader.setVisibility(View.VISIBLE);
        fetchItems();
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void fetchItems() {
        FetchHelper.fetchItems(new OnCallback() {
            @Override
            public void OnSuccess(Response response) {
                try {
                    String data = response.body().string();
                    JSONArray array = new JSONArray(data);
                    for (int i = 0; i < array.length(); i++) {
                        setData(array.getJSONObject(i));
                    }
                    runOnUiThread(() -> {
                        setAdapter();
                        loader.setVisibility(View.GONE);
                        checkCountItems();
                    });
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnError(Call response) {
            }
        });
    }

    @Override
    protected void setAdapter() {
        super.setAdapter();
        recyclerView.setAdapter(new MainAdapter(this.items, this));
    }

    @Override
    protected void fetchDelete(int id) {
        FetchHelper.fetchDeleteItem(id, new OnCallback() {
            @Override
            public void OnSuccess(Response response) {
                if (response.isSuccessful()) {
                    MainActivity.this.runOnUiThread(() -> {
                        items.remove(position);
                        recyclerView.getAdapter().notifyItemRemoved(position);
                        checkCountItems();
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
        Item item = new Item();
        item.setTitle(object.getString("title"));
        item.setDescription(object.getString("description"));
        item.setPreviewLink(object.getString("image_url"));
        item.setId(object.getInt("id"));
        item.setPrice(object.getInt("price"));
        item.setCount(object.getInt("count"));
        item.setSales(object.getBoolean("is_sales"));
        Object object1 = object.get("category");
        if (object1 instanceof JSONObject) {
            JSONObject object2 = object.getJSONObject("category");
            Category category = new Category();
            category.setTitle(object2.getString("title"));
            category.setId(object2.getInt("id"));
            item.setCategory(category);
        }
        this.items.add(item);
    }

    @Override
    public void onClick(RecyclerView.ViewHolder viewHolder, int position, int id) {
        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("index", position + 1);
        startActivity(intent);
    }

    @Override
    public void onLongClick(RecyclerView.ViewHolder viewHolder, int position, int id) {
        // Закрываем доступ на удаление товара всем, кроме админа
        boolean isSales = this.items.get(position).isSales();
        String[] items = {
          isSales ? "Возобновить продажу" : "Продать",
          "Удалить"
        };
        new AlertDialog.Builder(this)
                .setTitle("Действия")
                .setItems(items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            fetchUpdate(id, isSales);
                            break;
                        case 1:
                            if (!RoleHelper.isAdmin()) {
                                Toast.makeText(Application.getInstance(), "У вас нет прав на удаление записи", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            this.position = position;
                            fetchDelete(id);
                            break;
                    }
                })
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void fetchUpdate(int id, boolean isSales) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("is_sales", !isSales);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + APIHelper.TOKEN)
                .url(APIHelper.BASE_DOMAIN + "/items/create")
                .post(body)
                .build();
        OkHttpHelper.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(() -> {
                    OnRefresh();
                });
            }
        });
    }

    /**
     * Проверяем есть ли товары на складе
     */
    public void checkCountItems() {
        findViewById(R.id.not_found).setVisibility(this.items.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
