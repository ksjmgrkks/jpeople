package com.example.myapplication.SproutMeeting;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myapplication.Community.CommunityActivity;
import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.MyPage.MyPageActivity;
import com.example.myapplication.R;
import com.example.myapplication.Test.MainActivity;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

public class SproutMeetingActivity extends AppCompatActivity {

    private long back_button_time = 0;
    String sLeaderName;
    String sName;
    String sNotice;
    String sLink;

    String userEmail;
    String userPassword;
    String userName;
    String userGroup;
    String userProfile;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprout_meeting);

        TextView textviewGroup = findViewById(R.id.textview_group);
        TextView textviewNotice = findViewById(R.id.textview_notice);
        TextView textviewExplain = findViewById(R.id.textView_explain);
        TextView textviewSproutEdit = findViewById(R.id.textView_sprout_edit);
        ScrollView scrollView = findViewById(R.id.scrollView2);

        textviewExplain.setVisibility(View.INVISIBLE);
        textviewSproutEdit.setVisibility(View.INVISIBLE);

        Button buttonJoinChatting = findViewById(R.id.button_join_chatting);
        Button buttonJoinMeet = findViewById(R.id.button_join_meet);

        ImageView imageViewLeaderProfile = findViewById(R.id.imageview_leader_profile);

        userEmail = getIntent().getStringExtra("userEmail");
        userPassword = getIntent().getStringExtra("userPassword");
        userName = getIntent().getStringExtra("userName");
        userGroup = getIntent().getStringExtra("userGroup");
        userProfile = getIntent().getStringExtra("userProfile");

        //순 정보 불러오기
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){ //로그인에 성공한 경우
                        sName = jsonResponse.getString("sName");
                        sLeaderName = jsonResponse.getString("sLeaderName");
                        sNotice = jsonResponse.getString("sNotice");
                        Log.d("공지사항", sNotice);
                        sLink = jsonResponse.getString("sLink");

                        if(userName.equals(sLeaderName)){
                            textviewSproutEdit.setVisibility(View.VISIBLE);
                        }
                        textviewNotice.setText(sNotice);
                        textviewGroup.setText(sName);
                        Glide.with(SproutMeetingActivity.this)
                                .load(Uri.parse("http://49.247.146.128/image/"+sLeaderName+".png"))
                                .circleCrop()
                                .placeholder(R.drawable.firstprofile)
                                .into(imageViewLeaderProfile);

                    } else { //순이 존재하지 않을경우
                        scrollView.setVisibility(View.GONE);
                        textviewExplain.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        SproutReadRequest sproutReadRequest = new SproutReadRequest(userGroup, responseListener);
        RequestQueue queue = Volley.newRequestQueue(SproutMeetingActivity.this);
        queue.add(sproutReadRequest);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        buttonJoinChatting.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_chat, 0,0,0);
        buttonJoinMeet.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.meetlogo, 0,0,0);

        buttonJoinChatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        buttonJoinMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("uri", String.valueOf(Uri.parse(sLink)));
                Intent implicit_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sLink));
                startActivity(implicit_intent);
            }
        });

        textviewSproutEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SproutMeetingActivity.this, SproutUpdateActivity.class);
                intent.putExtra("sNotice",sNotice);
                intent.putExtra("sLink",sLink);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        //TabLayout 을 하단에 배치하여 유저가 어플의 주요 기능을 알아보기 쉽고,
        // 이용이 편리하도록 하였습니다.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab) ;
        tabLayout.selectTab(tabLayout.getTabAt(2));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition() ;
                changeView(position) ;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        }) ;
    }

    private void changeView(int index) {
        String userEmail = getIntent().getStringExtra("userEmail");
        String userPassword = getIntent().getStringExtra("userPassword");
        String userName = getIntent().getStringExtra("userName");
        String userGroup = getIntent().getStringExtra("userGroup");
        String userProfile = getIntent().getStringExtra("userProfile");
        Intent intent;
        switch (index) {
            case 0 :
                intent = new Intent(SproutMeetingActivity.this, HomeActivity.class);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                intent.putExtra("userProfile",userProfile);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break ;
            case 1 :
                intent = new Intent(SproutMeetingActivity.this, CommunityActivity.class);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                intent.putExtra("userProfile",userProfile);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break ;
            case 2 :
                break ;
            case 3 :
                intent = new Intent(SproutMeetingActivity.this, MyPageActivity.class);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                intent.putExtra("userProfile",userProfile);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break ;
        }
    }

    @Override
    public void onBackPressed() {
        //뒤로가기를 한번 눌렀을 때 어플이 종료된다면,
        //유저가 뒤로가기를 잘못 눌렀을 때 불편할 수 있기 때문에 이 기능을 추가했습니다.
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - back_button_time;

        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        }
        else {
            back_button_time = curTime;
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 앱이 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //노트를 수정할 때
        if (requestCode == 1 && resultCode == RESULT_OK) {
            assert data != null;
            String sNotice = data.getStringExtra("sNotice");
            String sLink = data.getStringExtra("sLink");

            //설교노트 수정 (서버+리사이클러뷰)
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    finish();
                    Intent intent = new Intent(SproutMeetingActivity.this, SproutMeetingActivity.class);
                    intent.putExtra("userEmail",userEmail);
                    intent.putExtra("userPassword",userPassword);
                    intent.putExtra("userName",userName);
                    intent.putExtra("userGroup",userGroup);
                    intent.putExtra("userProfile",userProfile);
                    SproutMeetingActivity.this.startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            };
            SproutUpdateRequest sproutUpdateRequest = new SproutUpdateRequest(sName, sNotice, sLink, responseListener);
            RequestQueue queue = Volley.newRequestQueue(SproutMeetingActivity.this);
            queue.add(sproutUpdateRequest);
        }
    }
}