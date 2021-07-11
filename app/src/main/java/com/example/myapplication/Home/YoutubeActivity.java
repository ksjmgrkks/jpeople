package com.example.myapplication.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MyPage.Note.NoteCreateRequest;
import com.example.myapplication.MyPage.Offering.OfferingActivity;
import com.example.myapplication.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONObject;

import java.util.Calendar;

public class YoutubeActivity extends YouTubeBaseActivity {
    Calendar cal = Calendar.getInstance();
    //현재 년도, 월, 일
    int year = cal.get (Calendar.YEAR);
    int month = cal.get (Calendar.MONTH);
    int date = cal.get (Calendar.DATE) ;

    YouTubePlayerView playerView;
    YouTubePlayer player;

    private static String API_KEY = "AIzaSyBzrwOjwQwKlw7wOFjJxwHm1d19dA4nH1k";
    private static String videoID = "pX-g5VsSwyI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        String userEmail = getIntent().getStringExtra("userEmail");
        String userPassword = getIntent().getStringExtra("userPassword");
        String userName = getIntent().getStringExtra("userName");
        String userGroup = getIntent().getStringExtra("userGroup");
        String userProfile = getIntent().getStringExtra("userProfile");

        final EditText edittextTitle = findViewById(R.id.edittext_title);
        final EditText edittextContents = findViewById(R.id.edittext_contents);

        initPlayer();
        ImageView liveStrart = findViewById(R.id.imageView_live_start);
        liveStrart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo();
                liveStrart.setVisibility(View.GONE);
            }
        });

        Button buttonNoteSave = (Button) findViewById(R.id.button_note_save);
        buttonNoteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edittextTitle.length() == 0){
                    Toast.makeText(YoutubeActivity.this, "제목을 입력해주세요",Toast.LENGTH_SHORT).show();
                    edittextTitle.requestFocus();
                    return;
                }
                if (edittextContents.length() == 0){
                    Toast.makeText(YoutubeActivity.this, "내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                    edittextContents.requestFocus();
                    return;
                }
                String noteTitle = edittextTitle.getText().toString();
                String noteDate = String.format("%d년 %d월 %d일", year,month+1,date);
                String noteContents = edittextContents.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                // 성공했을 때
                                edittextTitle.setText("");
                                edittextContents.setText("");
                                Toast.makeText(getApplicationContext(), "설교노트가 기록되었습니다.",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "작성한 설교노트는 \n '마이페이지→설교노트'에서 확인할 수 있습니다.",Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "설교노트 작성에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                NoteCreateRequest noteCreateRequest = new NoteCreateRequest(userEmail, noteTitle, noteDate, noteContents, responseListener);
                RequestQueue queue = Volley.newRequestQueue(YoutubeActivity.this);
                queue.add(noteCreateRequest);

            }
        });

        Button buttonOffering = (Button) findViewById(R.id.button_offering);
        buttonOffering.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(YoutubeActivity.this, OfferingActivity.class);
              intent.putExtra("userEmail",userEmail);
              intent.putExtra("userPassword",userPassword);
              intent.putExtra("userName",userName);
              intent.putExtra("userGroup",userGroup);
              intent.putExtra("userProfile",userProfile);
              startActivity(intent);
              overridePendingTransition(R.anim.fadein, R.anim.fadeout);
          }
        });
    }

    private void playVideo() {
        if(player != null) {
            if(player.isPlaying()) {
                player.pause();
            }
            player.cueVideo(videoID);
        }
    }
    //유튜브 플레이어 메서드
    private void initPlayer() {
        playerView = findViewById(R.id.youtubeView);
        playerView.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player = youTubePlayer;

                player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }
                    @Override
                    public void onLoaded(String id) {
                        player.play();
                    }
                    @Override
                    public void onAdStarted(){}
                    @Override
                    public void onVideoStarted(){}
                    @Override
                    public void onVideoEnded(){}
                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                    }
                });
            }
            @Override public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) { }
        });
    }


}