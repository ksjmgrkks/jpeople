package com.example.myapplication.Community;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CommunityCreateRequest extends StringRequest {
    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http://49.247.146.128/community-create.php";
    private final Map<String, String> parameters;

    public CommunityCreateRequest(String cwWriter, String cwCreated, String cwTitle, String cwContents, String cwImage, String totalPage, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("cwWriter", cwWriter);
        parameters.put("cwCreated", cwCreated);
        parameters.put("cwTitle", cwTitle);
        parameters.put("cwContents", cwContents);
        parameters.put("cwImage", cwImage);
        parameters.put("totalPage", totalPage);
    }
    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}

