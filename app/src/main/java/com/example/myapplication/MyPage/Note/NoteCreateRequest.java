package com.example.myapplication.MyPage.Note;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class NoteCreateRequest extends StringRequest {
    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http://49.247.146.128/note-create.php";
    private final Map<String, String> parameters;

    public NoteCreateRequest(String userEmail, String noteTitle, String noteDate, String noteContents, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userEmail", userEmail);
        parameters.put("noteTitle", noteTitle);
        parameters.put("noteDate", noteDate);
        parameters.put("noteContents", noteContents);
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
