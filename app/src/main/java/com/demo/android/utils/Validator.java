package com.demo.android.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.webkit.URLUtil;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Кастомный валидатор для полей
 */
public class Validator implements TextWatcher {

    private TextInputLayout textInputLayout;
    private boolean isUrl;

    /**
     * Принимаем параметры. 1 объект валидатора - 1 инпут
     *
     * @param textInputLayout
     * @param isUrl
     */
    public Validator(TextInputLayout textInputLayout, boolean isUrl) {
        this.isUrl = isUrl;
        this.textInputLayout = textInputLayout;
    }

    /**
     * Проверяем в целом на валидность поле
     *
     * @param text
     * @return
     */
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

    /**
     * Проверяем корректность ссылки (базовая версия)
     *
     * @param text
     * @return
     */
    private boolean validateUrl(String text) {
        return URLUtil.isValidUrl(text);
    }

    /**
     * Проверяем на пустоту
     *
     * @param text
     * @return
     */
    private boolean validateEmpty(String text) {
        return !TextUtils.isEmpty(text);
    }

    /**
     * Валидируем все входящие поля
     *
     * @param fields
     * @return
     */
    public static final boolean validate(TextInputEditText[] fields) {
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

    // Дефолтные обработчики от TextWatcher
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        textInputLayout.setErrorEnabled(!isValid(s.toString()));
    }
}