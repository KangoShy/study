package com.flink.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Http连接工具
 */
public class HttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

    private static OkHttpClient httpClient;

    private OkHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (this) {
                if (httpClient == null) {
                    httpClient = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return httpClient;
    }

    /**
     * 创建Get请求
     */
    private Response createGetRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        if (LOG.isDebugEnabled()) {
            LOG.debug("get Request \r\nUrl: {}", url);
        }
        Response response = null;
        try {
            response = getHttpClient().newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException("io errors on execute get request, please retry", e);
        }
        return response;
    }

    /**
     * 获取服务器返回的内容
     */
    private Map<String, Object> getResponseBody(Response response) {
        Map<String, Object> result;
        if (response.isSuccessful()) {
            String responseBody = null;
            try {
                responseBody = Objects.requireNonNull(response.body()).string();
            } catch (IOException e) {
                throw new RuntimeException("io errors on get response body string, please retry", e);
            }
            result = JSONObject.parseObject(responseBody, new TypeReference<Map<String, Object>>() {
            });

            if (LOG.isDebugEnabled()) {
                LOG.debug("Response Body: {}", responseBody);
            }
        } else {
            result = new HashMap<>();
            result.put("status", "ERROR");
            result.put("result", response.message());
        }
        response.close();
        return result;
    }

    /**
     * 创建Get请求并返回Map
     */
    @Contract
    public Map<String, Object> fetchGetMap(String url) {
        return getResponseBody(createGetRequest(url));
    }

}
