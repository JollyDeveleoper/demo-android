package com.demo.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.adapters.MainAdapter;
import com.demo.android.helpers.API.FetchHelper;
import com.demo.android.helpers.APIHelper;
import com.demo.android.helpers.OkHttpHelper;
import com.demo.android.helpers.PrefsHelper;
import com.demo.android.interfaces.OnCallback;
import com.demo.android.interfaces.OnItemClickListener;
import com.demo.android.models.Category;
import com.demo.android.models.Item;

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
import okhttp3.Response;

public class MainActivity extends Activity implements OnItemClickListener {

    private RecyclerView recyclerView;
    private List<Item> items = new ArrayList<>();
    private ProgressBar loader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PrefsHelper.getAccessToken().isEmpty()) {
            finishAffinity();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        setContentView(R.layout.activity_main);
        APIHelper.load();
        fetchItems();
        recyclerView = this.findViewById(R.id.recycler);
        loader = this.findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);
        Button addBtn = this.findViewById(R.id.primary_btn);
        addBtn.setOnClickListener(v -> startActivity(new Intent(this, CreateOrEditItemActivity.class)));
        findViewById(R.id.settings).setVisibility(View.VISIBLE);
        findViewById(R.id.settings).setOnClickListener(v1 -> startActivity(new Intent(this, MenuActivity.class)));
    }

    private void setAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(Application.getInstance()));
        recyclerView.setAdapter(new MainAdapter(this.items, this));
    }

    private void fetchItems() {
        Request request = OkHttpHelper.getRequest("items");
        OkHttpHelper.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TAG", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                try {
                    JSONArray array = new JSONArray(data);
                    setData(array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setData(JSONArray response) throws JSONException {
        for (int i = 0; i < response.length(); i++) {
            JSONObject obj = response.getJSONObject(i);
            Item item = new Item();
            item.setTitle(obj.getString("title"));
            item.setDescription(obj.getString("description"));
            item.setPreviewLink(obj.getString("image_url"));
            item.setId(obj.getInt("id"));
            item.setPrice(obj.getInt("price"));
            item.setCount(obj.getInt("count"));
            item.setSales(obj.getBoolean("is_sales"));
            Object object1 = obj.get("category");
            if (object1 instanceof JSONObject) {
                JSONObject object = obj.getJSONObject("category");
                Category category = new Category();
                category.setTitle(object.getString("title"));
                category.setId(object.getInt("id"));
                item.setCategory(category);
            }
            this.items.add(item);
        }
        this.runOnUiThread(() -> {
            this.setAdapter();
            loader.setVisibility(View.GONE);
            checkCountItems();
        });
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
                .setPositiveButton(android.R.string.ok, (dialog, which) -> deleteItem(id, position))
                .setNegativeButton(android.R.string.cancel, null).show();
    }

    private void deleteItem(int id, int index) {
        FetchHelper.fetchDeleteItem(id, new OnCallback() {
            @Override
            public void OnSuccess(Response response) {
                if (!response.isSuccessful()) {
                    return;
                }
                MainActivity.this.runOnUiThread(() -> {
                    recyclerView.getAdapter().notifyItemRemoved(index);
                    items.remove(index);
                    checkCountItems();
                });

            }

            @Override
            public void OnError(Call response) {
            }
        });
    }

    public void checkCountItems() {
        this.findViewById(R.id.not_found).setVisibility(this.items.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
