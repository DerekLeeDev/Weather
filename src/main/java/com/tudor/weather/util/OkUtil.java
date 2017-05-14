package com.tudor.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by LiDongduo on 2017/4/24.
 */

public class OkUtil {
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
