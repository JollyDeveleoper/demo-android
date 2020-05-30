package com.demo.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.android.R;
import com.demo.android.adapters.MainAdapter;
import com.demo.android.helpers.API.FetchHelper;
import com.demo.android.helpers.PrefsHelper;
import com.demo.android.interfaces.OnCallback;
import com.demo.android.interfaces.OnItemClickListener;
import com.demo.android.models.Category;
import com.demo.android.models.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Главный экран
 */
public class MainActivity extends BaseRecyclerActivity implements OnItemClickListener {

    private List<Item> items = new ArrayList<>();

    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Если не авторизованы - перебрасываем на авторизацию
        if (PrefsHelper.getAccessToken().isEmpty()) {
            finishAffinity();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        super.onCreate(savedInstanceState);
        Button addBtn = this.findViewById(R.id.primary_btn);
        addBtn.setOnClickListener(v -> startActivity(new Intent(this, CreateOrEditItemActivity.class)));
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
        startActivity(intent);
    }

    @Override
    public void onLongClick(RecyclerView.ViewHolder viewHolder, int position, int id) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление")
                .setMessage("Удалить данный товар?")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    this.position = position;
                    fetchDelete(id);
                })
                .setNegativeButton(android.R.string.cancel, null).show();
    }

    /**
     * Проверяем есть ли товары на складе
     */
    public void checkCountItems() {
        findViewById(R.id.not_found).setVisibility(this.items.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
