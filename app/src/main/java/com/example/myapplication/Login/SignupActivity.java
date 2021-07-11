package com.example.myapplication.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class SignupActivity extends AppCompatActivity {
    TextView textviewValidate;
    TextView textviewCountdown;
    TextView textviewCertify;
    TextView textviewEmailValidate;

    EditText edittextEmail;
    EditText edittextName;
    EditText edittextPassword;
    EditText edittextPasswordRepeat;
    EditText edittextCertifyEmail;

    Button ButtonCertifySubmit;
    Button ButtonValidate;
    Button ButtonSignupApply;
    Button ButtonCertifyEmail;
    Button ButtonEmailValidate;

    private int emailValidate = 0;
    private int nameConfirm = 0;
    private int emailConfirm = 0;
    String GmailCode;
    CountDownTimer countDownTimer;
    final int MILLISINFUTURE = 300 * 1000; //총 시간 (300초)
    final int COUNT_DOWN_INTERVAL = 1000; //onTick 메소드를 호출할 간격 (1초)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //뒤로가기 버튼 생성
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("");

        textviewValidate = (TextView) findViewById(R.id.textview_validate);
        textviewCountdown = (TextView) findViewById(R.id.textView_countdown);
        textviewCertify = (TextView) findViewById(R.id.textview_certify);
        textviewEmailValidate = (TextView) findViewById(R.id.textView_email_validate);

        edittextEmail = (EditText) findViewById(R.id.edittext_email);
        edittextName = (EditText) findViewById(R.id.edittext_name);
        edittextPassword = (EditText) findViewById(R.id.edittext_pw);
        edittextPasswordRepeat = (EditText) findViewById(R.id.edittext_pw_repeat);
        edittextCertifyEmail = (EditText) findViewById(R.id.edittext_certify_email);

        ButtonCertifySubmit = (Button) findViewById(R.id.button_certify_submit);
        ButtonValidate = (Button) findViewById(R.id.button_validate);
        ButtonSignupApply = (Button) findViewById(R.id.button_signup_apply);
        ButtonCertifyEmail = (Button) findViewById(R.id.button_certify_email);
        ButtonEmailValidate = (Button) findViewById(R.id.button_email_validate);

        ButtonEmailValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edittextEmail.getText().toString().trim();
                if(email.equals("")){
                    Toast.makeText(getApplicationContext(), "이메일을 입력해주세요",Toast.LENGTH_SHORT).show();
                    edittextEmail.requestFocus();
                    return;
                }
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (!pattern.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), "이메일 형식을 지켜주세요",Toast.LENGTH_SHORT).show();
                    edittextEmail.requestFocus();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                textviewEmailValidate.setText("사용 가능한 이메일입니다.");
                                textviewEmailValidate.setTextColor(Color.parseColor("#0000FF"));
                                edittextEmail.setEnabled(false);
                                Toast.makeText(getApplicationContext(), "이메일 인증을 진행해주세요", Toast.LENGTH_LONG).show();
                                emailValidate = 1;
                            }else{
                                textviewEmailValidate.setText("이미 사용중인 이메일입니다.");
                                textviewEmailValidate.setTextColor(Color.parseColor("#FF0000"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                EmailValidateRequest emailValidateRequest = new EmailValidateRequest(email, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
                queue.add(emailValidateRequest);
            }
        });

        ButtonCertifySubmit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(emailValidate == 0){
                  Toast.makeText(getApplicationContext(), "이메일 중복확인을 완료해주세요", Toast.LENGTH_LONG).show();
                  edittextEmail.requestFocus();
                  return;
              }
              //                이메일 인증부분을 보여준다.
              try {
                  Toast.makeText(getApplicationContext(), "이메일을 전송합니다. 잠시 기다려주세요", Toast.LENGTH_LONG).show();
                  GMailSender gMailSender = new GMailSender("jpeople1365@gmail.com", "rlarbtjd1365!");
                  //GMailSender.sendMail(제목, 본문내용, 받는사람);
                  //인증코드
                  GmailCode=gMailSender.getEmailCode();
                  gMailSender.sendMail("[제이피플] 회원가입 이메일 인증코드입니다.", "인증코드 : "+GmailCode , edittextEmail.getText().toString());
                  Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();


                  //이메일이 보내지면 이 부분을 실행시킨다.
                  ButtonCertifySubmit.setVisibility(View.GONE);
                  textviewCountdown.setVisibility(View.VISIBLE);
                  edittextCertifyEmail.setVisibility(View.VISIBLE);
                  ButtonCertifyEmail.setVisibility(View.VISIBLE);
                  textviewCertify.setVisibility(View.VISIBLE);
                  countDownTimer();

              } catch (SendFailedException e) {
                  Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다", Toast.LENGTH_SHORT).show();
              } catch (MessagingException e) {
                  System.out.println("인터넷 문제"+e);
                  Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }
        });

        //인증하는 버튼이다
        //혹시 이거랑 같으면 인증을 성공시켜라라
        ButtonCertifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이메일로 전송한 인증코드와 내가 입력한 인증코드가 같을 때
                if(edittextCertifyEmail.getText().toString().equals(GmailCode)){
                    edittextCertifyEmail.setEnabled(false);
                    ButtonCertifyEmail.setEnabled(false);
                    ButtonCertifyEmail.setText("인증완료");
                    countDownTimer.cancel();
                    textviewCountdown.setVisibility(View.INVISIBLE);
                    textviewCertify.setTextColor(Color.parseColor("#0000FF"));
                    textviewCertify.setText("이메일 인증이 완료되었습니다.");
                    emailConfirm = 1;
                }else{
                    Toast.makeText(getApplicationContext(), "인증번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        ButtonValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edittextName.getText().toString();
                if(name.equals("")){
                    Toast.makeText(getApplicationContext(), "이름을 입력해주세요",Toast.LENGTH_SHORT).show();
                    edittextName.requestFocus();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                textviewValidate.setTextSize(15);
                                textviewValidate.setText("사용 가능한 이름입니다.");
                                textviewValidate.setTextColor(Color.parseColor("#0000FF"));
                                edittextName.setEnabled(false);
                                edittextPassword.requestFocus();
                                nameConfirm = 1;
                            }else{
                                textviewValidate.setTextSize(11);
                                textviewValidate.setText("이미 사용중인 이름입니다.\n동명 이인일 시 \n교회에 등록된 이름으로 입력해주세요.\nex) 김이름A, 김이름B");
                                textviewValidate.setTextColor(Color.parseColor("#FF0000"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(name, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
                queue.add(validateRequest);
            }
        });

        ButtonSignupApply.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String password = edittextPassword.getText().toString().trim();
              String passwordRepeat = edittextPasswordRepeat.getText().toString().trim();
              if (emailConfirm == 0){
                  Toast.makeText(getApplicationContext(), "이메일 인증 절차를 진행해주세요",Toast.LENGTH_SHORT).show();
                  edittextEmail.requestFocus();
                  return;
              }
              if (nameConfirm == 0){
                  Toast.makeText(getApplicationContext(), "이름 중복확인 절차를 진행해주세요",Toast.LENGTH_SHORT).show();
                  edittextName.requestFocus();
                  return;
              }
              if (edittextPassword.length() == 0){
                  Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show();
                  edittextPassword.requestFocus();
                  return;
              }
              if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,16}$", password))
              {
                  Toast.makeText(SignupActivity.this,"비밀번호 형식을 지켜주세요.",Toast.LENGTH_SHORT).show();
                  return;
              }
              if (!password.equals(passwordRepeat)){
                  Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                  edittextPasswordRepeat.requestFocus();
                  return;
              }

              String email = edittextEmail.getText().toString().trim();
              String name = edittextName.getText().toString().trim();

              long now = System.currentTimeMillis();
              Date date = new Date(now);
              @SuppressLint("SimpleDateFormat")
              SimpleDateFormat formatNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
              String created = formatNow.format(date);
              String signupConfirm = "0";

              Response.Listener<String> responseListener = new Response.Listener<String>() {
                  @Override
                  public void onResponse(String response) {
                      try{
                          JSONObject jsonResponse = new JSONObject(response);
                          boolean success = jsonResponse.getBoolean("success");
                          if(success){
                              Toast.makeText(getApplicationContext(), "회원가입 신청에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                              Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                              SignupActivity.this.startActivity(intent);
                          } else {
                              Toast.makeText(getApplicationContext(), "회원가입 신청에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                              return;
                          }
                      }catch (Exception e){
                          e.printStackTrace();
                      }
                  }
              };
              // 서버로 Volley를 이용해서 요청을 하는 부분
              RegisterRequest registerRequest = new RegisterRequest(email, name, password, created, signupConfirm, responseListener);
              RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
              queue.add(registerRequest);
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

    public void countDownTimer() { //카운트 다운 메소드
        TextView textviewCountdown = (TextView) findViewById(R.id.textView_countdown);
        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) { //(300초에서 1초 마다 계속 줄어듬)
                long emailAuthCount = millisUntilFinished / 1000;
                Log.d("Alex", emailAuthCount + "");
                if ((emailAuthCount - ((emailAuthCount / 60) * 60)) >= 10) { //초가 10보다 크면 그냥 출력
                    textviewCountdown.setText((emailAuthCount / 60) + " : " + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                } else { //초가 10보다 작으면 앞에 '0' 붙여서 같이 출력. ex) 02,03,04...
                    textviewCountdown.setText((emailAuthCount / 60) + " : 0" + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                }
            }
            @Override
            public void onFinish() { //시간이 다 되면
                countDownTimer.cancel();
                ButtonCertifySubmit.setVisibility(View.VISIBLE);
                textviewCountdown.setVisibility(View.INVISIBLE);
                edittextCertifyEmail.setVisibility(View.GONE);
                ButtonCertifyEmail.setVisibility(View.GONE);
                textviewCertify.setTextColor(Color.parseColor("#FF0000"));
                textviewCertify.setText("이메일 인증을 다시해주세요 (시간초과)");
            }
        }.start();
    }
}