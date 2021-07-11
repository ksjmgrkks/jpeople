package com.example.myapplication.Home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.Community.CommunityActivity;
import com.example.myapplication.MyPage.MyPageActivity;
import com.example.myapplication.R;
import com.example.myapplication.SproutMeeting.SproutMeetingActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private long back_button_time = 0;

    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    static String pageNumber;

    ArrayList<HomeSliderDictionary> SliderList;
    HomeSliderAdapter imageSliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sliderViewPager = findViewById(R.id.sliderViewPager);
        layoutIndicator = findViewById(R.id.layoutIndicators);

        sliderViewPager.setOffscreenPageLimit(1);
        SliderList = new ArrayList<>(); // 뷰페이저 어레이리스트
        imageSliderAdapter = new HomeSliderAdapter(this, SliderList);
        sliderViewPager.setAdapter(imageSliderAdapter);

        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

        SliderList.add(new HomeSliderDictionary("http://49.247.146.128/image/home.jpg","1"));
        SliderList.add(new HomeSliderDictionary("http://49.247.146.128/image/homeimageq.jpg","2"));
        SliderList.add(new HomeSliderDictionary("http://49.247.146.128/image/hometwo.jpg","3"));
        imageSliderAdapter.notifyDataSetChanged();
        setupIndicators(SliderList.size());

        String userEmail = getIntent().getStringExtra("userEmail");
        String userPassword = getIntent().getStringExtra("userPassword");
        String userName = getIntent().getStringExtra("userName");
        String userGroup = getIntent().getStringExtra("userGroup");
        String userProfile = getIntent().getStringExtra("userProfile");

        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ImageView imageviewYoutube = findViewById(R.id.imageView_youtube);
        ImageView imageviewInstagram = findViewById(R.id.imageView_instagram);
        ImageView imageviewKakaotalk = findViewById(R.id.imageView_kakaotalk);
        ImageView imageviewNaverblog = findViewById(R.id.imageView_naverblog);

        // 뷰페이저 이미지를 클릭했을 때 액티비티 이동함
        imageSliderAdapter.setOnItemClickListener(new HomeSliderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if(pageNumber.equals("1")){
                    Intent intent = new Intent(HomeActivity.this, YoutubeActivity.class);
                    intent.putExtra("userEmail",userEmail);
                    intent.putExtra("userPassword",userPassword);
                    intent.putExtra("userName",userName);
                    intent.putExtra("userGroup",userGroup);
                    intent.putExtra("userProfile",userProfile);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
                if(pageNumber.equals("2")){
                    Toast.makeText(getApplicationContext(), "하계수련회",Toast.LENGTH_SHORT).show();
                }
                if(pageNumber.equals("3")){
                    Toast.makeText(getApplicationContext(), "소개 페이지",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //유튜브 링크
        imageviewYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent implicit_intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/channel/UCR8CRkDdmjRVzfa3ro4Twmg"));
                startActivity(implicit_intent);
            }
        });
        //인스타그램 링크
        imageviewInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent implicit_intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.instagram.com/jpeople_official/"));
                startActivity(implicit_intent);
            }
        });
        //카카오톡 링크
        imageviewKakaotalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent implicit_intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://pf.kakao.com/_PPAvj"));
                startActivity(implicit_intent);
            }
        });
        //블로그 링크
        imageviewNaverblog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent implicit_intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://blog.naver.com/j-people"));
                startActivity(implicit_intent);
            }
        });

        //TabLayout 을 하단에 배치하여 유저가 어플의 주요 기능을 알아보기 쉽고,
        // 이용이 편리하도록 하였습니다.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab) ;
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

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
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
                break ;
            case 1 :
                intent = new Intent(HomeActivity.this, CommunityActivity.class);
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
                intent = new Intent(HomeActivity.this, SproutMeetingActivity.class);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                intent.putExtra("userProfile",userProfile);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break ;
            case 3 :
                intent = new Intent(HomeActivity.this, MyPageActivity.class);
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
}