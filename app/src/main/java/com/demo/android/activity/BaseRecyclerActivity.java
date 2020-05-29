package com.demo.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.android.R;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseRecyclerActivity extends Activity {
    protected ProgressBar loader;
    protected RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_recycler);
        this.loader = findViewById(R.id.loader);
        this.recyclerView = findViewById(R.id.recycler);
        fetchItems();
    }

    protected abstract void fetchItems();
    protected abstract void fetchDelete(int id);
    protected abstract void setData(JSONObject object) throws JSONException;

    protected void setAdapter() {
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
