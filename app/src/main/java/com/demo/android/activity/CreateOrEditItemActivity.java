package com.demo.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.demo.android.Application;
import com.demo.android.R;
import com.demo.android.helpers.API.FetchHelper;
import com.demo.android.helpers.APIHelper;
import com.demo.android.helpers.OkHttpHelper;
import com.demo.android.interfaces.OnCallback;
import com.demo.android.models.Category;
import com.demo.android.models.Role;
import com.demo.android.utils.Validator;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.demo.android.helpers.OkHttpHelper.JSON;

public class CreateOrEditItemActivity extends Activity {

    private TextInputLayout title, description, url, count, price;
    private TextInputEditText titleEt, descriptionEt, urlEt, countEt, priceEt;
    private MaterialCheckBox isSales;

    private AutoCompleteTextView category;
    private List<String> categoriesSpinner = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    private int id;
    private boolean isEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        title = this.findViewById(R.id.textFieldTitle);
        description = this.findViewById(R.id.textFieldDescription);
        count = this.findViewById(R.id.textFieldCount);
        url = this.findViewById(R.id.textFieldUrl);
        price = this.findViewById(R.id.textFieldPrice);
        isSales = this.findViewById(R.id.isSales);
        category = this.findViewById(R.id.filled_exposed_dropdown);
        initViews();
        initWatchers();

        getRoles();

        this.findViewById(R.id.primary_btn).setOnClickListener(v -> {
            if (validate()) {
                fetchSave();
            }
        });

        fillData();

        ((TextView) this.findViewById(R.id.title)).setText(this.isEdit ? "Редактирование товара" : "Добавление товара");
        if (this.isEdit) {
            ((Button) findViewById(R.id.primary_btn)).setText("Сохранить");
        }
    }

    private void fillData() {
        Bundle intent = getIntent().getExtras();
        if (intent == null) return;
        this.id = getIntent().getExtras().getInt("id");
        titleEt.setText(intent.getString("title"));
        descriptionEt.setText(intent.getString("description"));
        countEt.setText(String.valueOf(intent.getInt("count")));
        priceEt.setText(String.valueOf(intent.getInt("price")));
        urlEt.setText(intent.getString("url"));
        isSales.setChecked(intent.getBoolean("is_sales", false));
        category.setText(intent.getString("category"));
        this.isEdit = true;
    }

    private Category findCategoryByName(String name) {
        Category category = null;
        for (Category category1 : this.categories) {
            if (category1.getTitle().equals(name)) category = category1;
        }
        return category;
    }

    private void getRoles() {
        FetchHelper.fetchCategories(new OnCallback() {
            @Override
            public void OnSuccess(Response response) {
                if (response.isSuccessful()) {
                    try {
                        Log.e("TAG", "ad");
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray array = jsonObject.getJSONArray("data");
                        fillCategories(array);

                        runOnUiThread(() -> {
                            setCategoriesAdapter();
                            if (getIntent().getExtras() != null) {
                                fillData();
                            }
                            ((TextView) findViewById(R.id.title)).setText(isEdit ? "Редактирование" : "Создание");
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

    private void fillCategories(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String name = object.getString("title");
            Category category = new Category();
            category.setTitle(name);
            category.setId(object.getInt("id"));
            categories.add(category);
            categoriesSpinner.add(name);
        }
    }

    private void setCategoriesAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, categoriesSpinner);
        category.setAdapter(adapter);
        findViewById(R.id.loader).setVisibility(View.GONE);
        category.setVisibility(View.VISIBLE);
    }

    // Todo вынести в валидатор
    private boolean validate() {
        TextInputEditText[] fields = {
                titleEt, descriptionEt, urlEt, countEt, priceEt
        };
        boolean valid = true;
        for (TextInputEditText field : fields) {
            if (field.getText() == null || TextUtils.isEmpty(field.getText().toString())) {
                ((TextInputLayout) field.getParent().getParent()).setError("Это обязательное поле");
                ((TextInputLayout) field.getParent().getParent()).setErrorEnabled(true);
                valid = false;
            } else {
                ((TextInputLayout) field.getParent().getParent()).setErrorEnabled(false);
            }
        }
        return valid;
    }

    private void fetchSave() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (this.isEdit) {
                jsonObject.put("id", this.id);
            }
            jsonObject.put("title", titleEt.getText().toString());
            jsonObject.put("description", descriptionEt.getText().toString());
            jsonObject.put("image_url", urlEt.getText().toString());
            jsonObject.put("count", countEt.getText().toString());
            jsonObject.put("price", priceEt.getText().toString());
            jsonObject.put("is_sales", isSales.isChecked());
            Category category = findCategoryByName(this.category.getText().toString());
            if (category != null) {
                jsonObject.put("category_id", category.getId());
            }
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
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("TAG", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    onSave(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onSave(Response response) throws JSONException, IOException {
        String data = response.body().string();

        JSONObject jsonObject = new JSONObject(data);
        if (jsonObject.has("error")) {
            this.runOnUiThread(() -> {
                try {
                    Toast.makeText(Application.getInstance(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            return;
        }
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void initViews() {
        this.titleEt = this.findViewById(R.id.titleProduct);
        this.countEt = this.findViewById(R.id.count);
        this.urlEt = this.findViewById(R.id.imageUrl);
        this.descriptionEt = this.findViewById(R.id.description);
        this.priceEt = this.findViewById(R.id.price);
    }

    private void initWatchers() {
        this.titleEt.addTextChangedListener(new Validator(title, false));
        this.descriptionEt.addTextChangedListener(new Validator(description, false));
        this.priceEt.addTextChangedListener(new Validator(price, false));
        this.countEt.addTextChangedListener(new Validator(count, false));
        this.urlEt.addTextChangedListener(new Validator(url, true));
    }
}
