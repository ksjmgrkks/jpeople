package com.example.myapplication.Community.Like;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LikeActivity extends AppCompatActivity {


    String cwNumber;

    int allConfirm = 0;

    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    ArrayList<LikeDictionary> likeArrayList = new ArrayList<>();
    LikeAdapter likeAdapter;

    // 1페이지에 5개씩 데이터를 불러온다
    int page = 0, limit = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

        //뒤로가기 버튼 생성
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        cwNumber = getIntent().getStringExtra("cwNumber");
        Log.d("확인1", cwNumber);

        setTitle("좋아요");

        nestedScrollView = findViewById(R.id.scroll_view);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        likeAdapter = new LikeAdapter(LikeActivity.this, likeArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(likeAdapter);

        getStartData(page, limit, cwNumber);

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
                        getData(page, limit, cwNumber);
                    }

                }
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

    private void getData(int page, int limit, String cwNumber)
    {
        String pageString = ""+page+"";
        String limitString = ""+limit+"";
        //DB에 저장된 글 불러오기 -> 어레이 리스트 -> 리사이클러뷰에 표현
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    if(response.equals("end")){
                        progressBar.setVisibility(View.GONE);
                        allConfirm = 1;
                    }
                    JSONArray jsonResponse = new JSONArray(response);
                    for (int i = 0; i < jsonResponse.length() ; i++){
                        try {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            String clName = jsonObject.getString("clName");
                            String userGroup = jsonObject.getString("userGroup");
                            LikeDictionary dict = new LikeDictionary(clName, userGroup);
                            likeArrayList.add(dict);
                            likeAdapter.notifyItemInserted(likeArrayList.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        LikeReadRequest likeReadRequest = new LikeReadRequest(pageString, limitString, cwNumber, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LikeActivity.this);
        queue.add(likeReadRequest);
    }

    private void getStartData(int page, int limit, String cwNumber)
    {
        progressBar.setVisibility(View.GONE);
        String pageString = ""+page+"";
        String limitString = ""+limit+"";
        //DB에 저장된 글 불러오기 -> 어레이 리스트 -> 리사이클러뷰에 표현
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    if(response.equals("end")){
                        progressBar.setVisibility(View.GONE);
                        allConfirm = 1;
                    }
                    JSONArray jsonResponse = new JSONArray(response);
                    for (int i = 0; i < jsonResponse.length() ; i++){
                        try {
                            JSONObject jsonObject = jsonResponse.getJSONObject(i);
                            String clName = jsonObject.getString("clName");
                            Log.d("디버그태그", clName);
                            String userGroup = jsonObject.getString("userGroup");
                            Log.d("디버그태그", userGroup);
                            LikeDictionary dict = new LikeDictionary(clName, userGroup);
                            likeArrayList.add(dict);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    likeAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        LikeReadRequest likeReadRequest = new LikeReadRequest(pageString, limitString, cwNumber, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LikeActivity.this);
        queue.add(likeReadRequest);
    }
}