package com.example.myapplication.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.R;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("");

        EditText edittextEmail = findViewById(R.id.edittext_email);
        EditText edittextPassword = findViewById(R.id.edittext_password);

        Button buttonLogin = findViewById(R.id.button_login);
        Button buttonSignup = findViewById(R.id.button_signup);
        Button buttonFindPassword = findViewById(R.id.button_find_password);

        //로그인
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edittextEmail.getText().toString().trim();
                String password = edittextPassword.getText().toString().trim();
                if(email.equals("")){
                    Toast.makeText(getApplicationContext(), "이메일을 입력해주세요",Toast.LENGTH_SHORT).show();
                    edittextEmail.requestFocus();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){ //로그인에 성공한 경우
                                String userEmail = jsonResponse.getString("userEmail");
                                String userPassword = jsonResponse.getString("userPassword");
                                String userName = jsonResponse.getString("userName");
                                String userGroup = jsonResponse.getString("userGroup");
                                String userProfile = jsonResponse.getString("userProfile");

                                SharedPreferences preference = getSharedPreferences("userInfo", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preference.edit();
                                editor.putString("userEmail", userEmail);
                                editor.putString("userName", userName);
                                editor.putString("userPassword", userPassword);
                                editor.putString("userGroup", userGroup);
                                editor.putString("userProfile", userProfile);
                                editor.apply();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("userEmail",userEmail);
                                intent.putExtra("userName",userName);
                                intent.putExtra("userPassword",userPassword);
                                intent.putExtra("userGroup",userGroup);
                                intent.putExtra("userProfile",userProfile);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                Toast.makeText(LoginActivity.this, "제이피플에 오신것을 환영합니다",Toast.LENGTH_SHORT).show();
                                finish();
                            } else { //로그인에 실패한 경우
                                Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, TermsActivity.class);
                LoginActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        buttonFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PasswordfindActivity.class);
                LoginActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

}