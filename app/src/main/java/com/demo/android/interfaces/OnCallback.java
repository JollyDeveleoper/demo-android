package com.demo.android.interfaces;

import okhttp3.Call;
import okhttp3.Response;

public interface OnCallback {
    void OnSuccess(Response response);
    void OnError(Call response);
}
