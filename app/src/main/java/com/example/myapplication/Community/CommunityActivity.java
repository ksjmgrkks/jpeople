package com.example.myapplication.Community;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Community.Comment.CommentActivity;
import com.example.myapplication.Community.Like.LikeActivity;
import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.MyPage.MyPageActivity;
import com.example.myapplication.R;
import com.example.myapplication.SproutMeeting.SproutMeetingActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommunityActivity extends AppCompatActivity {

    String userEmail;
    String userPassword;
    static String userName;
    static String StringCwNumber;
    static String cwWriter;
    static String cwWriterGroup;
    static String filePathArray;
    static String totalPage;
    static String cwImage;
    String userGroup;
    String userProfile;

    static String cwTitle;
    static String cwCreated;
    static String cwContents;
    static String cwNumber;
    static String cwLike;
    static String cwComment;
    static String cwProfile;
    static String cwLikeIsset;

    int allConfirm = 0;

    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TabLayout tabLayout;
    FloatingActionButton buttonWrite;
    ImageView imageViewSearch;
    ImageView imageViewJpeople;
    EditText editTextSearch;

    ArrayList<CommunityDictionary> cwArrayList = new ArrayList<>();
    CommunityAdapter cwAdapter;

    // 1페이지에 3개씩 데이터를 불러온다
    int page = 0, limit = 3;

    private long back_button_time = 0;
    int pos;
    int search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        userEmail = getIntent().getStringExtra("userEmail");
        userPassword = getIntent().getStringExtra("userPassword");
        userName = getIntent().getStringExtra("userName");
        userGroup = getIntent().getStringExtra("userGroup");
        userProfile = getIntent().getStringExtra("userProfile");

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        search = 0;
        buttonWrite = (FloatingActionButton)findViewById(R.id.button_write);

        nestedScrollView = findViewById(R.id.scroll_view);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        imageViewSearch = findViewById(R.id.imageView_search);
        imageViewJpeople = findViewById(R.id.imageView_jpeople);
        editTextSearch = findViewById(R.id.edittext_search);
        imageViewSearch.setVisibility(View.GONE);

        cwAdapter = new CommunityAdapter(CommunityActivity.this, cwArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cwAdapter);

        getStartData(page, limit);

        // 스크롤이 맨 아래로 내려올 때마다 게시물을 3개씩 불러온다 (무한 스크롤뷰 페이징)
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())
                {
                    if(allConfirm == 0){
                        page++;
                        progressBar.setVisibility(View.VISIBLE);
                        Log.d("여러번", "오니?");
                        getData(page, limit);
                    }else{
                        Toast.makeText(getApplicationContext(), "모든 게시물을 확인했습니다",Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        cwAdapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int itemPosition, int clickItem) {
                // 옵션을 클릭했을 때
                if(clickItem == 0){
                    if(cwWriter.equals(userName)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityActivity.this);
                        builder.setItems(R.array.CommunityMenu, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int position)
                            {
                                String[] items = getResources().getStringArray(R.array.CommunityMenu);
                                switch(items[position]){
                                    case "게시물 수정하기":
                                        Intent intent = new Intent(CommunityActivity.this, CommunityWriteActivity.class);
                                        intent.putExtra("cwTitle",cwTitle);
                                        intent.putExtra("cwCreated",cwCreated);
                                        intent.putExtra("cwContents",cwContents);
                                        intent.putExtra("userName",userName);
                                        intent.putExtra("cwNumber",cwNumber);
                                        intent.putExtra("cwLike",cwLike);
                                        intent.putExtra("cwComment",cwComment);
                                        intent.putExtra("cwProfile",cwProfile);
                                        intent.putExtra("cwLikeIsset",cwLikeIsset);
                                        intent.putExtra("cwGroup",cwWriterGroup);
                                        intent.putExtra("cwImage",cwImage);
                                        pos = itemPosition;
                                        startActivityForResult(intent, 2);
                                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                        break;

                                    case "게시물 삭제하기":
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityActivity.this);
                                        builder.setTitle("정말 게시물을 삭제하시겠습니까?");
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
                                                                    if(Success){
                                                                        cwArrayList.remove(itemPosition);
                                                                        cwAdapter.notifyItemRemoved(itemPosition);
                                                                        cwAdapter.notifyItemRangeChanged(itemPosition, cwArrayList.size());
                                                                        Toast.makeText(getApplicationContext(), "게시물 삭제에 성공하였습니다",Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(), "게시물 삭제에 실패하였습니다",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }catch (Exception e){
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        };
                                                        CommunityDeleteRequest communityDeleteRequest = new CommunityDeleteRequest(StringCwNumber, responseListener);
                                                        RequestQueue queue = Volley.newRequestQueue(CommunityActivity.this);
                                                        queue.add(communityDeleteRequest);
                                                        break;
                                                    case "아니요":
                                                        break;
                                                }
                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                        break;

                                    case "게시물 공유하기":
                                        Toast.makeText(getApplicationContext(), "공유하기",Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityActivity.this);
                        builder.setItems(R.array.CommunityMenuShare, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int position)
                            {
                                String[] items = getResources().getStringArray(R.array.CommunityMenuShare);
                                if ("게시물 공유하기".equals(items[position])) {
                                    Toast.makeText(getApplicationContext(), "공유하기",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                }
                // 글 내용을 클릭했을 때
                if(clickItem == 1){
                    Toast.makeText(getApplicationContext(), "글 내용",Toast.LENGTH_SHORT).show();
                }
                // 좋아요를 클릭했을 때
//                if(clickItem == 2){
//                }
                // 좋아요 텍스트를 클릭했을 때
                if(clickItem == 3){
                    if(!CommunityAdapter.likeCount.equals("0")){
                        Intent intent = new Intent(CommunityActivity.this, LikeActivity.class);
                        intent.putExtra("cwNumber",StringCwNumber);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                }
                // 댓글을 클릭했을 때
                if(clickItem == 4){
                    Intent intent = new Intent(CommunityActivity.this, CommentActivity.class);
                    intent.putExtra("cwNumber",StringCwNumber);
                    intent.putExtra("cwWriter",cwWriter);
                    intent.putExtra("cwWriterGroup",cwWriterGroup);
                    intent.putExtra("cwTitle",cwTitle);
                    intent.putExtra("cwContents",cwContents);
                    intent.putExtra("userName",userName);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            }
        }) ;

        //커뮤니티 게시판 작성버튼
        buttonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this, CommunityWriteActivity.class);
                intent.putExtra("cwTitle","");
                intent.putExtra("cwCreated","");
                intent.putExtra("cwContents","");
                intent.putExtra("cwImage","");
                intent.putExtra("userName",userName);
                intent.putExtra("cwNumber","");
                intent.putExtra("cwLike","");
                intent.putExtra("cwComment","");
                intent.putExtra("cwProfile","");
                intent.putExtra("cwLikeIsset","");
                intent.putExtra("cwGroup","");
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        //검색버튼을 눌렀을 때
//        imageViewSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(search == 0){
//                    imageViewJpeople.setVisibility(View.GONE);
//                    imageViewSearch.setImageResource(R.drawable.ic_cancel);
//                    editTextSearch.setVisibility(View.VISIBLE);
//                    search = 1;
//                }else{
//                    imageViewJpeople.setVisibility(View.VISIBLE);
//                    imageViewSearch.setImageResource(R.drawable.ic_search);
//                    editTextSearch.setVisibility(View.GONE);
//                    search = 0;
//                }
//
//            }
//        });

        //TabLayout 을 하단에 배치하여 유저가 어플의 주요 기능을 알아보기 쉽고,
        // 이용이 편리하도록 하였습니다.
        tabLayout = (TabLayout) findViewById(R.id.tab) ;
        tabLayout.selectTab(tabLayout.getTabAt(1));
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
        Intent intent;
        switch (index) {
            case 0 :
                intent = new Intent(CommunityActivity.this, HomeActivity.class);
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
                break ;
            case 2 :
                intent = new Intent(CommunityActivity.this, SproutMeetingActivity.class);
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
                intent = new Intent(CommunityActivity.this, MyPageActivity.class);
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

    private void getData(int page, int limit)
    {
        String pageString = ""+page+"";
        String limitString = ""+limit+"";
        //DB에 저장된 글 불러오기 -> 어레이 리스트 -> 리사이클러뷰에 표현
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    if(response.equals("end")){
                        //end 라는 response를 받으면 allConfirm 변수를 1로 바꾼다.
                        //그다음 스크롤을 맨 밑으로 내리면 게시물을 모두 확인했다는 토스트 메세지를 보내도록 한다.
                        Log.d("여러번", "1");
                        progressBar.setVisibility(View.GONE);
                        allConfirm = 1;
                    }
                    JSONArray jsonResponse = new JSONArray(response);
                    Log.d("디버그 response", response);
                    Log.d("디버그 response", String.valueOf(jsonResponse));
                    for (int i = 0; i < jsonResponse.length() ; i++){
                        try {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            String cwNumber = jsonObject.getString("cwNumber");
                            String cwWriter = jsonObject.getString("cwWriter");
                            String cwCreated = jsonObject.getString("cwCreated");
                            String cwTitle = jsonObject.getString("cwTitle");
                            String cwContents = jsonObject.getString("cwContents");
                            String likeCount = jsonObject.getString("likeCount");
                            String likeIsset = jsonObject.getString("likeIsset");
                            String userGroup = jsonObject.getString("userGroup");
                            String cwImage = jsonObject.getString("cwImage");
                            String totalPage = jsonObject.getString("totalPage");

                            if(likeCount.equals("0")){
                                CommunityDictionary dict = new CommunityDictionary(cwNumber, cwWriter, cwCreated, cwTitle, cwContents, likeCount, "0", "http://49.247.146.128/image/"+cwWriter+".png", likeIsset, userGroup, cwImage, totalPage);
                                cwArrayList.add(dict);
                            }else{
                                CommunityDictionary dict = new CommunityDictionary(cwNumber, cwWriter, cwCreated, cwTitle, cwContents, likeCount+"명이 좋아합니다", "0", "http://49.247.146.128/image/"+cwWriter+".png", likeIsset, userGroup, cwImage, totalPage);
                                cwArrayList.add(dict);
                            }
                            cwAdapter.notifyItemInserted(cwArrayList.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        CommunityReadRequest communityReadRequest = new CommunityReadRequest(pageString, limitString, userName, responseListener);
        RequestQueue queue = Volley.newRequestQueue(CommunityActivity.this);
        queue.add(communityReadRequest);
    }

    private void getStartData(int page, int limit)
    {
        String pageString = ""+page+"";
        String limitString = ""+limit+"";
        //DB에 저장된 글 불러오기 -> 어레이 리스트 -> 리사이클러뷰에 표현
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jsonResponse = new JSONArray(response);
                    Log.d("디버그 response", response);
                    Log.d("디버그 response", String.valueOf(jsonResponse));
                    for (int i = 0; i < jsonResponse.length() ; i++){
                        try {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            String cwNumber = jsonObject.getString("cwNumber");
                            String cwWriter = jsonObject.getString("cwWriter");
                            String cwCreated = jsonObject.getString("cwCreated");
                            String cwTitle = jsonObject.getString("cwTitle");
                            String cwContents = jsonObject.getString("cwContents");
                            String likeCount = jsonObject.getString("likeCount");
                            String likeIsset = jsonObject.getString("likeIsset");
                            String userGroup = jsonObject.getString("userGroup");
                            String cwImage = jsonObject.getString("cwImage");
                            String totalPage = jsonObject.getString("totalPage");

                            if(likeCount.equals("0")){
                                CommunityDictionary dict = new CommunityDictionary(
                                        cwNumber,
                                        cwWriter,
                                        cwCreated,
                                        cwTitle,
                                        cwContents,
                                        likeCount,
                                        "0",
                                        "http://49.247.146.128/image/"+cwWriter+".png",
                                        likeIsset,
                                        userGroup,
                                        cwImage,
                                        totalPage);
                                cwArrayList.add(dict);
                            }else{
                                CommunityDictionary dict = new CommunityDictionary(
                                        cwNumber,
                                        cwWriter,
                                        cwCreated,
                                        cwTitle,
                                        cwContents,
                                        likeCount+"명이 좋아합니다",
                                        "0",
                                        "http://49.247.146.128/image/"+cwWriter+".png",
                                        likeIsset,
                                        userGroup,
                                        cwImage,
                                        totalPage);
                                cwArrayList.add(dict);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    cwAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        CommunityReadRequest communityReadRequest = new CommunityReadRequest(pageString, limitString, userName, responseListener);
        RequestQueue queue = Volley.newRequestQueue(CommunityActivity.this);
        queue.add(communityReadRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 게시판을 글을 처음 작성할 때
        if (requestCode == 1 && resultCode == RESULT_OK) {
            assert data != null;
            cwTitle = data.getStringExtra("cwTitle");
            cwCreated = data.getStringExtra("cwCreated");
            cwContents = data.getStringExtra("cwContents");
            filePathArray = data.getStringExtra("filePathArray");
            totalPage = data.getStringExtra("totalPage");
            Log.d("디버그태그1", filePathArray);

            //게시판 글 등록 (서버+리사이클러뷰)
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success){
                            ProgressDialog mProgressDialog = new ProgressDialog(CommunityActivity.this);
                            mProgressDialog.setTitle("로딩중...");
                            mProgressDialog.setMessage("게시물을 업로드중입니다...");
                            mProgressDialog.setCanceledOnTouchOutside(false);
                            mProgressDialog.setIndeterminate(false);
                            mProgressDialog.show();

                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable()  {
                                public void run() {
                                    // 시간 지난 후 실행할 코딩
                                    Toast.makeText(getApplicationContext(), "게시글 작성에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent = new Intent(CommunityActivity.this, CommunityActivity.class);
                                    intent.putExtra("userEmail",userEmail);
                                    intent.putExtra("userPassword",userPassword);
                                    intent.putExtra("userName",userName);
                                    intent.putExtra("userGroup",userGroup);
                                    intent.putExtra("userProfile",userProfile);
                                    CommunityActivity.this.startActivity(intent);
                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                }
                            }, 1500); // 1.5초후
                        } else {
                            Toast.makeText(getApplicationContext(), "게시글 작성에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            CommunityCreateRequest communityCreateRequest = new CommunityCreateRequest(userName, cwCreated, cwTitle, cwContents, filePathArray, totalPage, responseListener);
            RequestQueue queue = Volley.newRequestQueue(CommunityActivity.this);
            queue.add(communityCreateRequest);
        }

        //게시판 글을 수정할 때
        if (requestCode == 2 && resultCode == RESULT_OK) {
            assert data != null;

            cwNumber = data.getStringExtra("cwNumber");
            cwLike = data.getStringExtra("cwLike");
            cwComment = data.getStringExtra("cwComment");
            cwProfile = data.getStringExtra("cwProfile");
            cwLikeIsset = data.getStringExtra("cwLikeIsset");
            cwWriterGroup = data.getStringExtra("cwGroup");
            cwTitle = data.getStringExtra("cwTitle");
            cwContents = data.getStringExtra("cwContents");
            cwCreated = data.getStringExtra("cwCreated");
            filePathArray = data.getStringExtra("filePathArray");
            Log.d("뭐라고 오니1", filePathArray);
            totalPage = data.getStringExtra("totalPage");
            //게시판 글 수정 (서버+리사이클러뷰)
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success){
                            ProgressDialog mProgressDialog = new ProgressDialog(CommunityActivity.this);
                            mProgressDialog.setTitle("로딩중...");
                            mProgressDialog.setMessage("게시물을 수정하는 중입니다...");
                            mProgressDialog.setCanceledOnTouchOutside(false);
                            mProgressDialog.setIndeterminate(false);
                            mProgressDialog.show();

                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable()  {
                                public void run() {
                                    // 시간 지난 후 실행할 코딩
                                    finish();
                                    Intent intent = new Intent(CommunityActivity.this, CommunityActivity.class);
                                    intent.putExtra("userEmail",userEmail);
                                    intent.putExtra("userPassword",userPassword);
                                    intent.putExtra("userName",userName);
                                    intent.putExtra("userGroup",userGroup);
                                    intent.putExtra("userProfile",userProfile);
                                    CommunityActivity.this.startActivity(intent);
                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    Toast.makeText(getApplicationContext(), "게시글 수정에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                    // 일반적으로 수정하려고 하면 뷰페이저 쪽에 오류가 많이 나서 페이지 새로고침으로 대체하였다.
//                                    Log.d("뭐라고 오니2", filePathArray);
//                                    CommunityDictionary dict = new CommunityDictionary(cwNumber,userName,cwCreated,cwTitle,cwContents,cwLike,cwComment,cwProfile,cwLikeIsset,cwWriterGroup,filePathArray,totalPage);
//                                    cwArrayList.set(pos, dict);
//                                    cwAdapter.notifyItemChanged(pos);
//                                    mProgressDialog.dismiss();
//                                    Toast.makeText(getApplicationContext(), "게시글 수정에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                }
                            }, 1500); // 1.5초후
                        } else {
                            Toast.makeText(getApplicationContext(), "게시판 글 수정에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            CommunityUpdateRequest communityUpdateRequest = new CommunityUpdateRequest(cwTitle, cwContents, filePathArray, totalPage, StringCwNumber, responseListener);
            RequestQueue queue = Volley.newRequestQueue(CommunityActivity.this);
            queue.add(communityUpdateRequest);
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