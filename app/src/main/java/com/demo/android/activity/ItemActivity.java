package com.demo.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.helpers.OkHttpHelper;
import com.demo.android.helpers.RoleHelper;
import com.demo.android.models.Category;
import com.demo.android.models.Item;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ItemActivity extends Activity {

    private int id;
    private ProgressBar loader;
    private Item item;
    private int index;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        this.loader = this.findViewById(R.id.loader);
        this.loader.setVisibility(View.VISIBLE);
        this.id = getIntent().getExtras().getInt("id");
        this.index = getIntent().getExtras().getInt("index");
        fetchItem(this.id);

        if (RoleHelper.isAdmin() || RoleHelper.isModerator()) {
            findViewById(R.id.editBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.editBtn).setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateOrEditItemActivity.class);
                intent.putExtra("id", this.item.getId());
                intent.putExtra("title", this.item.getTitle());
                intent.putExtra("count", this.item.getCount());
                intent.putExtra("description", this.item.getDescription());
                intent.putExtra("price", this.item.getPrice());
                intent.putExtra("url", this.item.getPreviewLink());
                intent.putExtra("is_sales", this.item.isSales());
                intent.putExtra("category", this.item.getCategory() != null ? this.item.getCategory().getTitle() : "");
                startActivity(intent);
            });
        }
    }

    private void fetchItem(int id) {
        Request request = OkHttpHelper.getRequest("items/" + id);
        OkHttpHelper.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                try {
                    setData(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setData(JSONObject object) throws JSONException {
        this.item = new Item();
        item.setTitle(object.getString("title"));
        item.setDescription(object.getString("description"));
        item.setPreviewLink(object.getString("image_url"));
        item.setId(object.getInt("id"));
        item.setPrice(object.getInt("price"));
        item.setCount(object.getInt("count"));
        item.setId(object.getInt("id"));
        item.setSales(object.getBoolean("is_sales"));
        Object o = object.get("category");
        if (o instanceof JSONObject) {
            Category category = new Category();
            JSONObject object1 = object.getJSONObject("category");
            category.setId(object1.getInt("id"));
            category.setTitle(object1.getString("title"));
            item.setCategory(category);
        }

        this.runOnUiThread(() -> {
            ((TextView) this.findViewById(R.id.title)).setText(String.format("Товар №%s", this.index));
            ((TextView) this.findViewById(R.id.titleProduct)).setText(item.getTitle());
            ((TextView) this.findViewById(R.id.body)).setText(item.getDescription());
            ((TextView) this.findViewById(R.id.price)).setText(String.format("%s₽", item.getPrice()));
            if (item.getCategory() != null) {
                ((TextView) this.findViewById(R.id.category)).setText(item.getCategory().getTitle());
            }
            if (item.isSales()) {
                this.findViewById(R.id.is_sales).setVisibility(View.VISIBLE);
                this.findViewById(R.id.count).setVisibility(View.GONE);
            } else {
                ((TextView) this.findViewById(R.id.count)).setText(String.format("На складе - %sшт", item.getCount()));
            }
            Glide.with(Application.getInstance()).load(item.getPreviewLink()).into(((ImageView) this.findViewById(R.id.preview)));
            this.loader.setVisibility(View.GONE);
        });
    }
}
