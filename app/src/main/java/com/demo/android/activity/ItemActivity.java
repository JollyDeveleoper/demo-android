package com.demo.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.helpers.OkHttpHelper;
import com.demo.android.helpers.PrefsHelper;
import com.demo.android.helpers.RoleHelper;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        this.loader = this.findViewById(R.id.loader);
        this.loader.setVisibility(View.VISIBLE);
        this.id = getIntent().getExtras().getInt("id");
        fetchItem(this.id);

        if (PrefsHelper.getRole() == RoleHelper.ROLE_ADMIN) {
            findViewById(R.id.editBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.editBtn).setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateOrEditItemActivity.class);
                intent.putExtra("id", this.item.getId());
                intent.putExtra("title", this.item.getTitle());
                intent.putExtra("count", this.item.getCount());
                intent.putExtra("description", this.item.getDescription());
                intent.putExtra("price", this.item.getPrice());
                intent.putExtra("url", this.item.getPreviewLink());
                startActivity(intent);
            });
        }
    }

    private void fetchItem(int id) {
        Request request = OkHttpHelper.getRequest("items/" + id);
        OkHttpHelper.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TAG", e.getMessage());
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
        this.runOnUiThread(() -> {
            ((TextView) this.findViewById(R.id.title)).setText(String.format("Товар №%s", item.getId()));
            ((TextView) this.findViewById(R.id.titleProduct)).setText(item.getTitle());
            ((TextView) this.findViewById(R.id.body)).setText(item.getDescription());
            ((TextView) this.findViewById(R.id.count)).setText(String.format("На складе - %sшт", item.getCount()));
            ((TextView) this.findViewById(R.id.price)).setText(item.getPrice() + "₽");
            Glide.with(Application.getInstance()).load(item.getPreviewLink()).into(((ImageView) this.findViewById(R.id.preview)));
            this.loader.setVisibility(View.GONE);
        });
    }
}
