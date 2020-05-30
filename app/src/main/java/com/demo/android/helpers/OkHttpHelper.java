package com.demo.android.helpers;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpHelper {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client;

    /**
     * Клиент для запросов
     * @return
     */
    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    /**
     * GET запрос
     *
     * @param route
     * @return
     */
    public static Request getRequest(String route) {
        return buildBaseRequest(route)
                .get()
                .build();
    }

    /**
     * POST запрос
     *
     * @param route
     * @param body
     * @return
     */
    public static Request getPostRequest(String route, RequestBody body) {
        return buildBaseRequest(route)
                .post(body)
                .build();
    }

    /**
     * Билдим базовый request с access_token
     *
     * @param route
     * @return
     */
    private static Request.Builder buildBaseRequest(String route) {
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + APIHelper.TOKEN)
                .url(APIHelper.BASE_DOMAIN + "/" + route);
    }
}
