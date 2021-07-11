package com.example.myapplication.Community.Like;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LikeRequest extends StringRequest {
    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http://49.247.146.128/like-request.php";
    private final Map<String, String> parameters;

    public LikeRequest(String clSort, String writingNumber, String clName, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("clSort", clSort);
        parameters.put("writingNumber", writingNumber);
        parameters.put("clName", clName);
    }
    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
