package com.example.myapplication.Community;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CommunityUpdateRequest extends StringRequest {
    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http://49.247.146.128/community-update.php";
    private final Map<String, String> parameters;

    public CommunityUpdateRequest(String cwTitle, String cwContents, String cwImage, String totalPage, String cwNumber, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("cwTitle", cwTitle);
        parameters.put("cwContents", cwContents);
        parameters.put("cwImage", cwImage);
        parameters.put("totalPage", totalPage);
        parameters.put("cwNumber", cwNumber);
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
