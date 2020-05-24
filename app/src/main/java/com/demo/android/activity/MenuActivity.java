package com.demo.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.demo.android.R;
import com.demo.android.helpers.APIHelper;

public class MenuActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ((TextView) findViewById(R.id.title)).setText("Меню");
        findViewById(R.id.users).setOnClickListener(v -> startActivity(new Intent(this, UsersActivity.class)));
        findViewById(R.id.roles).setOnClickListener(v -> startActivity(new Intent(this, RolesActivity.class)));
        findViewById(R.id.exit).setOnClickListener(v -> {
            finishAffinity();
            APIHelper.expiredToken(this);
        });
    }
}
