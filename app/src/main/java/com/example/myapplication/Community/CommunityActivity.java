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

    // 1???????????? 3?????? ???????????? ????????????
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

        // ???????????? ??? ????????? ????????? ????????? ???????????? 3?????? ???????????? (?????? ???????????? ?????????)
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
                        Log.d("?????????", "???????");
                        getData(page, limit);
                    }else{
                        Toast.makeText(getApplicationContext(), "?????? ???????????? ??????????????????",Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        cwAdapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int itemPosition, int clickItem) {
                // ????????? ???????????? ???
                if(clickItem == 0){
                    if(cwWriter.equals(userName)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityActivity.this);
                        builder.setItems(R.array.CommunityMenu, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int position)
                            {
                                String[] items = getResources().getStringArray(R.array.CommunityMenu);
                                switch(items[position]){
                                    case "????????? ????????????":
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

                                    case "????????? ????????????":
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityActivity.this);
                                        builder.setTitle("?????? ???????????? ?????????????????????????");
                                        builder.setItems(R.array.Yes_Or_No, new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int position)
                                            {
                                                String[] items = getResources().getStringArray(R.array.Yes_Or_No);
                                                switch(items[position]){
                                                    case "???" :
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
                                                                        Toast.makeText(getApplicationContext(), "????????? ????????? ?????????????????????",Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(), "????????? ????????? ?????????????????????",Toast.LENGTH_SHORT).show();
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
                                                    case "?????????":
                                                        break;
                                                }
                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                        break;

                                    case "????????? ????????????":
                                        Toast.makeText(getApplicationContext(), "????????????",Toast.LENGTH_SHORT).show();
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
                                if ("????????? ????????????".equals(items[position])) {
                                    Toast.makeText(getApplicationContext(), "????????????",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                }
                // ??? ????????? ???????????? ???
                if(clickItem == 1){
                    Toast.makeText(getApplicationContext(), "??? ??????",Toast.LENGTH_SHORT).show();
                }
                // ???????????? ???????????? ???
//                if(clickItem == 2){
//                }
                // ????????? ???????????? ???????????? ???
                if(clickItem == 3){
                    if(!CommunityAdapter.likeCount.equals("0")){
                        Intent intent = new Intent(CommunityActivity.this, LikeActivity.class);
                        intent.putExtra("cwNumber",StringCwNumber);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                }
                // ????????? ???????????? ???
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

        //???????????? ????????? ????????????
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

        //??????????????? ????????? ???
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

        //TabLayout ??? ????????? ???????????? ????????? ????????? ?????? ????????? ???????????? ??????,
        // ????????? ??????????????? ???????????????.
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
        //DB??? ????????? ??? ???????????? -> ????????? ????????? -> ????????????????????? ??????
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    if(response.equals("end")){
                        //end ?????? response??? ????????? allConfirm ????????? 1??? ?????????.
                        //????????? ???????????? ??? ????????? ????????? ???????????? ?????? ??????????????? ????????? ???????????? ???????????? ??????.
                        Log.d("?????????", "1");
                        progressBar.setVisibility(View.GONE);
                        allConfirm = 1;
                    }
                    JSONArray jsonResponse = new JSONArray(response);
                    Log.d("????????? response", response);
                    Log.d("????????? response", String.valueOf(jsonResponse));
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
                                CommunityDictionary dict = new CommunityDictionary(cwNumber, cwWriter, cwCreated, cwTitle, cwContents, likeCount+"?????? ???????????????", "0", "http://49.247.146.128/image/"+cwWriter+".png", likeIsset, userGroup, cwImage, totalPage);
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
        //DB??? ????????? ??? ???????????? -> ????????? ????????? -> ????????????????????? ??????
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jsonResponse = new JSONArray(response);
                    Log.d("????????? response", response);
                    Log.d("????????? response", String.valueOf(jsonResponse));
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
                                        likeCount+"?????? ???????????????",
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

        // ???????????? ?????? ?????? ????????? ???
        if (requestCode == 1 && resultCode == RESULT_OK) {
            assert data != null;
            cwTitle = data.getStringExtra("cwTitle");
            cwCreated = data.getStringExtra("cwCreated");
            cwContents = data.getStringExtra("cwContents");
            filePathArray = data.getStringExtra("filePathArray");
            totalPage = data.getStringExtra("totalPage");
            Log.d("???????????????1", filePathArray);

            //????????? ??? ?????? (??????+??????????????????)
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success){
                            ProgressDialog mProgressDialog = new ProgressDialog(CommunityActivity.this);
                            mProgressDialog.setTitle("?????????...");
                            mProgressDialog.setMessage("???????????? ?????????????????????...");
                            mProgressDialog.setCanceledOnTouchOutside(false);
                            mProgressDialog.setIndeterminate(false);
                            mProgressDialog.show();

                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable()  {
                                public void run() {
                                    // ?????? ?????? ??? ????????? ??????
                                    Toast.makeText(getApplicationContext(), "????????? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
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
                            }, 1500); // 1.5??????
                        } else {
                            Toast.makeText(getApplicationContext(), "????????? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
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

        //????????? ?????? ????????? ???
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
            Log.d("????????? ??????1", filePathArray);
            totalPage = data.getStringExtra("totalPage");
            //????????? ??? ?????? (??????+??????????????????)
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success){
                            ProgressDialog mProgressDialog = new ProgressDialog(CommunityActivity.this);
                            mProgressDialog.setTitle("?????????...");
                            mProgressDialog.setMessage("???????????? ???????????? ????????????...");
                            mProgressDialog.setCanceledOnTouchOutside(false);
                            mProgressDialog.setIndeterminate(false);
                            mProgressDialog.show();

                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable()  {
                                public void run() {
                                    // ?????? ?????? ??? ????????? ??????
                                    finish();
                                    Intent intent = new Intent(CommunityActivity.this, CommunityActivity.class);
                                    intent.putExtra("userEmail",userEmail);
                                    intent.putExtra("userPassword",userPassword);
                                    intent.putExtra("userName",userName);
                                    intent.putExtra("userGroup",userGroup);
                                    intent.putExtra("userProfile",userProfile);
                                    CommunityActivity.this.startActivity(intent);
                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    Toast.makeText(getApplicationContext(), "????????? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
                                    // ??????????????? ??????????????? ?????? ???????????? ?????? ????????? ?????? ?????? ????????? ?????????????????? ???????????????.
//                                    Log.d("????????? ??????2", filePathArray);
//                                    CommunityDictionary dict = new CommunityDictionary(cwNumber,userName,cwCreated,cwTitle,cwContents,cwLike,cwComment,cwProfile,cwLikeIsset,cwWriterGroup,filePathArray,totalPage);
//                                    cwArrayList.set(pos, dict);
//                                    cwAdapter.notifyItemChanged(pos);
//                                    mProgressDialog.dismiss();
//                                    Toast.makeText(getApplicationContext(), "????????? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
                                }
                            }, 1500); // 1.5??????
                        } else {
                            Toast.makeText(getApplicationContext(), "????????? ??? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
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
        //??????????????? ?????? ????????? ??? ????????? ???????????????,
        //????????? ??????????????? ?????? ????????? ??? ????????? ??? ?????? ????????? ??? ????????? ??????????????????.
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - back_button_time;

        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        }
        else {
            back_button_time = curTime;
            Toast.makeText(this, "'??????' ????????? ?????? ??? ????????? ?????? ???????????????.",Toast.LENGTH_SHORT).show();
        }
    }
}