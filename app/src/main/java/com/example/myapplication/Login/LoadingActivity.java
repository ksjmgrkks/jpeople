package com.example.myapplication.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.R;

import org.json.JSONObject;

public class LoadingActivity extends AppCompatActivity {

    int loginSuccess;

    String userEmail;
    String userPassword;
    String userName;
    String userGroup;
    String userProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //자동로그인

        //기존 로그인 정보 불러오기
        SharedPreferences UserinfoSharedPreferences =  getSharedPreferences("userInfo", MODE_PRIVATE);
        userEmail = UserinfoSharedPreferences.getString("userEmail", "");
        userPassword = UserinfoSharedPreferences.getString("userPassword", "");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){ //로그인에 성공한 경우
                        userEmail = jsonResponse.getString("userEmail");
                        userPassword = jsonResponse.getString("userPassword");
                        userName = jsonResponse.getString("userName");
                        userGroup = jsonResponse.getString("userGroup");
                        userProfile = jsonResponse.getString("userProfile");

                        SharedPreferences preference = getSharedPreferences("userInfo", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putString("userEmail", userEmail);
                        editor.putString("userName", userName);
                        editor.putString("userPassword", userPassword);
                        editor.putString("userGroup", userGroup);
                        editor.putString("userProfile", userProfile);
                        editor.apply();

                        loginSuccess = 1;
                    } else { //로그인에 실패한 경우
                        loginSuccess = 0;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        LoginRequest loginRequest = new LoginRequest(userEmail, userPassword, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoadingActivity.this);
        queue.add(loginRequest);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setTitle("");
        startLoading();

        ImageView image = (ImageView)findViewById(R.id.imageview_logo);
        ImageView imageChurch = (ImageView)findViewById(R.id.imageView_church);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadeinlogo);

        image.startAnimation(animation);
        imageChurch.startAnimation(animation);
    }

    private void startLoading() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if(loginSuccess == 1){
                    if(userEmail.equals("")){
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }else{
                        intent = new Intent(LoadingActivity.this, HomeActivity.class);
                        intent.putExtra("userEmail",userEmail);
                        intent.putExtra("userName",userName);
                        intent.putExtra("userPassword",userPassword);
                        intent.putExtra("userGroup",userGroup);
                        intent.putExtra("userProfile",userProfile);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        Toast.makeText(LoadingActivity.this, "제이피플에 오신것을 환영합니다",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
                finish();

            }
        }, 1500);
    }
}