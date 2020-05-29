package com.demo.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.demo.android.R;
import com.demo.android.helpers.APIHelper;
import com.demo.android.helpers.PrefsHelper;
import com.demo.android.helpers.RoleHelper;

public class MenuActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ((TextView) findViewById(R.id.title)).setText("Меню");
        findViewById(R.id.exit).setOnClickListener(v -> {
            finishAffinity();
            APIHelper.expiredToken(this);
        });
        ((TextView) findViewById(R.id.username)).setText(PrefsHelper.getRole());
        if (RoleHelper.isAdmin()) {
            findViewById(R.id.users).setVisibility(View.VISIBLE);
            findViewById(R.id.users).setOnClickListener(v -> startActivity(new Intent(this, UsersActivity.class)));
        }
        if (RoleHelper.isAdmin() || RoleHelper.isModerator()) {
            findViewById(R.id.roles).setVisibility(View.VISIBLE);
            findViewById(R.id.roles).setOnClickListener(v -> startActivity(new Intent(this, RolesActivity.class)));
            findViewById(R.id.categories).setVisibility(View.VISIBLE);
            findViewById(R.id.categories).setOnClickListener(v -> startActivity(new Intent(this, CategoryActivity.class)));
        }
    }
}
