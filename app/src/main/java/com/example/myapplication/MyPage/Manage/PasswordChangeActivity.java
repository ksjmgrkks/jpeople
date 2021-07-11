package com.example.myapplication.MyPage.Manage;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Login.PasswordChangeRequest;
import com.example.myapplication.R;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class PasswordChangeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        EditText edittextCurrentPassword = findViewById(R.id.edittext_current_password);
        EditText edittextNewPassword = findViewById(R.id.edittext_new_password);
        EditText edittextNewPasswordConfirm = findViewById(R.id.edittext_new_password_confirm);

        Button buttonSubmit = findViewById(R.id.button_submit);

        //뒤로가기 버튼 생성
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("비밀번호 변경");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = getIntent().getStringExtra("userEmail");
                String userPassword = getIntent().getStringExtra("userPassword");
                String currentPassword = edittextCurrentPassword.getText().toString().trim();
                String newPassword = edittextNewPassword.getText().toString().trim();
                String newPasswordRepeat = edittextNewPasswordConfirm.getText().toString().trim();
                if (!userPassword.equals(currentPassword)){
                    Toast.makeText(getApplicationContext(), "현재 비밀번호를 확인해주세요",Toast.LENGTH_SHORT).show();
                    edittextCurrentPassword.requestFocus();
                    return;
                }
                if (newPassword.length() == 0){
                    Toast.makeText(getApplicationContext(), "새 비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show();
                    edittextNewPassword.requestFocus();
                    return;
                }
                if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,16}$", newPassword))
                {
                    Toast.makeText(getApplicationContext(), "비밀번호 형식을 지켜주세요",Toast.LENGTH_SHORT).show();
                    edittextNewPassword.requestFocus();
                    return;
                }
                if (!newPassword.equals(newPasswordRepeat)){
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show();
                    edittextNewPasswordConfirm.requestFocus();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        try{
                            JSONObject jsonobject = new JSONObject(Response);
                            boolean Success = jsonobject.getBoolean("success");
                            if(Success){ // 비밀번호 변경에 성공한 경우
                                Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "비밀번호 변경에 실패하였습니다",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                // 서버로 Volley를 이용해서 요청을 하는 부분 (비밀번호 변경)
                PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(newPassword, userEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(PasswordChangeActivity.this);
                queue.add(passwordChangeRequest);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}