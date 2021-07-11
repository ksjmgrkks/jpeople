package com.example.myapplication.MyPage.Manage.OfferingConfirm;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

public class OfferingConfirmActivity extends AppCompatActivity {

    private ArrayList<OfferingConfirmDictionary> offeringArrayList;
    private OfferingConfirmCustomAdapter offeringAdapter;

    TextView textviewExplain;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offering_confirm);

        //뒤로가기 버튼 생성
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        userName = getIntent().getStringExtra("userName");
        setTitle(userName+"님의 헌금내역");
        textviewExplain = findViewById(R.id.textview_explain);

        //아이템뷰가 나열되는 형태를 관리하기위한 레이아웃매니저 객체 생성
        RecyclerView offeringRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_offering);
        LinearLayoutManager offeringLinearLayoutManager = new LinearLayoutManager(this);
        offeringRecyclerView.setLayoutManager(offeringLinearLayoutManager);
        //어레이리스트와 어탭터 연결
        offeringArrayList = new ArrayList<>();

        offeringAdapter = new OfferingConfirmCustomAdapter( this, offeringArrayList);
        offeringRecyclerView.setAdapter(offeringAdapter);

        //DB에 저장된 헌금내역 불러오기 -> 어레이 리스트 -> 리사이클러뷰에 표현
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
                            String offeringSort = jsonObject.getString("offeringSort");
                            String offeringDate = jsonObject.getString("offeringDate");
                            String offeringMoney = jsonObject.getString("offeringMoney");
                            String offeringContents = jsonObject.getString("offeringContents");

                            OfferingConfirmDictionary dict = new OfferingConfirmDictionary("헌금 종류 : "+ offeringSort,
                                    "헌금 금액 : "+offeringMoney+"원",
                                    "헌금 드린 시간 : "+offeringDate,
                                    "헌금 내용 : "+offeringContents);

                            offeringArrayList.add(0, dict);
                            offeringAdapter.notifyItemInserted(0);
                            textviewExplain.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        OfferingReadRequest offeringReadRequest = new OfferingReadRequest(userName, responseListener);
        RequestQueue queue = Volley.newRequestQueue(OfferingConfirmActivity.this);
        queue.add(offeringReadRequest);

        Log.d("어레이리스트 사이즈", String.valueOf(offeringArrayList.size()));
        if(offeringArrayList.size()==0){
            textviewExplain.setVisibility(View.VISIBLE);
        }else{
            textviewExplain.setVisibility(View.GONE);
        }

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