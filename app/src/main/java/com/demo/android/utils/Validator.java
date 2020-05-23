package com.demo.android.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.webkit.URLUtil;

import com.google.android.material.textfield.TextInputLayout;

public class Validator implements TextWatcher {

    private TextInputLayout textInputLayout;
    private boolean isUrl;

    public Validator(TextInputLayout textInputLayout, boolean isUrl) {
        this.isUrl = isUrl;
        this.textInputLayout = textInputLayout;
    }

    private boolean isValid(String text) {
        if (!validateEmpty(text)) {
            textInputLayout.setError("Это обязательное поле");
            return false;
        }
        if (isUrl && !validateUrl(text)) {
            textInputLayout.setError("Некорректная ссылка");
            return false;
        }
        return true;
    }

    private boolean validateUrl(String text) {
        return URLUtil.isValidUrl(text);
    }

    private boolean validateEmpty(String text) {
        return !TextUtils.isEmpty(text);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        textInputLayout.setErrorEnabled(!isValid(s.toString()));
    }
}