package com.example.myapplication.MyPage.Manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Login.LoginActivity;
import com.example.myapplication.MyPage.Manage.OfferingConfirm.OfferingConfirmActivity;
import com.example.myapplication.R;

import org.json.JSONObject;

public class ManageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        //뒤로가기 버튼 생성
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("회원정보 관리");

        TextView textviewPasswordChange = findViewById(R.id.textview_password_change);
        TextView textviewOfferingConfirm = findViewById(R.id.textview_offfering_confirm);
        TextView textviewTermsConfirm = findViewById(R.id.textview_terms_confirm);
        TextView textviewWithdraw = findViewById(R.id.textview_withdraw);

        String userEmail = getIntent().getStringExtra("userEmail");
        String userPassword = getIntent().getStringExtra("userPassword");
        String userName = getIntent().getStringExtra("userName");
        String userGroup = getIntent().getStringExtra("userGroup");
        String userProfile = getIntent().getStringExtra("userProfile");

        //비밀번호 변경
        textviewPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageActivity.this, PasswordChangeActivity.class);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                intent.putExtra("userProfile",userProfile);
                ManageActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        //헌금 내역 확인
        textviewOfferingConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageActivity.this, OfferingConfirmActivity.class);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                intent.putExtra("userProfile",userProfile);
                ManageActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        //약관 확인
        textviewTermsConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageActivity.this, TermsConfirmActivity.class);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                intent.putExtra("userProfile",userProfile);
                ManageActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        //회원 탈퇴
        textviewWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageActivity.this);
                builder.setTitle("정말 탈퇴하시겠습니까?");
                builder.setItems(R.array.Yes_Or_No, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int position)
                    {
                        String[] items = getResources().getStringArray(R.array.Yes_Or_No);
                        switch(items[position]){
                            case "예" :
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String Response) {
                                        try{
                                            JSONObject jsonobject = new JSONObject(Response);
                                            boolean Success = jsonobject.getBoolean("success");
                                            if(Success){ // 회원 탈퇴에 성공한 경우
                                                SharedPreferences preference = getSharedPreferences("userInfo", MODE_PRIVATE);
                                                SharedPreferences.Editor editor =preference.edit();
                                                editor.remove("userEmail");
                                                editor.remove("userName");
                                                editor.remove("userPassword");
                                                editor.remove("userGroup");
                                                editor.remove("userProfile");
                                                editor.apply();

                                                Intent intent = new Intent(ManageActivity.this, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                ManageActivity.this.startActivity(intent);

                                                Toast.makeText(getApplicationContext(), "회원탈퇴가 완료되었습니다", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "회원탈퇴에 실패하였습니다",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                // 서버로 Volley를 이용해서 요청을 하는 부분 (회원 탈퇴)
                                UserDeleteRequest userDeleteRequest = new UserDeleteRequest(userEmail, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(ManageActivity.this);
                                queue.add(userDeleteRequest);
                                break;
                            case "아니요":
                                break;
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
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