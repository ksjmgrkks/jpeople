package com.example.myapplication.Login;

import android.os.Bundle;
import android.os.StrictMode;
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
import com.example.myapplication.R;

import org.json.JSONObject;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class PasswordfindActivity extends AppCompatActivity {

    String Changedpassword;
    int emailCertify = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        //뒤로가기 버튼 생성
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("");

        EditText edittextEmail = findViewById(R.id.edittext_email);
        EditText edittextName = findViewById(R.id.edittext_name);

        Button buttonSubmit = findViewById(R.id.button_submit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edittextEmail.getText().toString().trim();
                String name = edittextName.getText().toString().trim();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){ //인증에 성공한 경우

                                try {
                                    GMailSender gMailSender = new GMailSender("jpeople1365@gmail.com", "rlarbtjd1365!");
                                    //인증코드
                                    Changedpassword=gMailSender.getEmailCode();
                                    gMailSender.sendMail("[제이피플] 변경된 비밀번호를 확인해주세요!", "변경된 비밀번호 : "+Changedpassword , edittextEmail.getText().toString());
                                    //이메일이 보내지면 이 부분을 실행시킨다.
                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String Response) {
                                            try{
                                                JSONObject jsonobject = new JSONObject(Response);
                                                boolean Success = jsonobject.getBoolean("success");
                                                if(Success){ // 비밀번호 변경에 성공한 경우
                                                    Toast.makeText(getApplicationContext(), edittextEmail.getText().toString()+"에 변경된 비밀번호를 전송했습니다.",Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "비밀번호 변경에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    // 서버로 Volley를 이용해서 요청을 하는 부분 (비밀번호 변경)
                                    PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(Changedpassword, email, responseListener);
                                    RequestQueue queue = Volley.newRequestQueue(PasswordfindActivity.this);
                                    queue.add(passwordChangeRequest);

                                } catch (SendFailedException e) {
                                    Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                } catch (MessagingException e) {
                                    System.out.println("인터넷 문제"+e);
                                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                        .permitDiskReads()
                                        .permitDiskWrites()
                                        .permitNetwork().build());

                            } else { //인증에 실패한 경우
                                Toast.makeText(getApplicationContext(), "이메일과 이름을 올바르게 적어주세요",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                // 서버로 Volley를 이용해서 요청을 하는 부분 (이메일 인증)
                PasswordfindRequest passwordfindRequest = new PasswordfindRequest(email, name, responseListener);
                RequestQueue queue = Volley.newRequestQueue(PasswordfindActivity.this);
                queue.add(passwordfindRequest);
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