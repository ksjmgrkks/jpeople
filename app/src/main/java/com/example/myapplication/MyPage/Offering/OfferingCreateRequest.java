package com.example.myapplication.MyPage.Offering;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class OfferingCreateRequest extends StringRequest {
    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http://49.247.146.128/offering-create.php";
    private final Map<String, String> parameters;

    public OfferingCreateRequest(String userName, String offeringSort, String offeringMoney, String offeringDate, String offeringContents, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userName", userName);
        parameters.put("offeringSort", offeringSort);
        parameters.put("offeringMoney", offeringMoney);
        parameters.put("offeringDate", offeringDate);
        parameters.put("offeringContents", offeringContents);
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
