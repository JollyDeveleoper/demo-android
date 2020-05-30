package com.demo.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.demo.android.R;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseRecyclerActivity extends Activity {
    protected ProgressBar loader;
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout refreshLayout;

    /**
     * Загружаем базовые вьюшки
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_recycler);
        this.loader = findViewById(R.id.loader);
        this.recyclerView = findViewById(R.id.recycler);
        this.refreshLayout = findViewById(R.id.refresher);
        this.refreshLayout.setOnRefreshListener(this::OnRefresh);
        fetchItems();
    }

    protected abstract void OnRefresh();

    /**
     * Получение списка
     */
    protected abstract void fetchItems();

    /**
     * Удаление элемента
     *
     * @param id
     */
    protected abstract void fetchDelete(int id);

    /**
     * Обрабатываем ответ от сервера
     *
     * @param object
     * @throws JSONException
     */
    protected abstract void setData(JSONObject object) throws JSONException;

    /**
     * Ставим адаптер для списка (recylerview)
     */
    protected void setAdapter() {
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
