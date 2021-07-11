package com.example.myapplication.SproutMeeting;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SproutUpdateRequest extends StringRequest {
    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http://49.247.146.128/sprout-update.php";
    private final Map<String, String> parameters;

    public SproutUpdateRequest(String sName, String sNotice, String sLink, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("sName", sName);
        parameters.put("sNotice", sNotice);
        parameters.put("sLink", sLink);
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
