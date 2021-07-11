package com.example.myapplication.MyPage.Note;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    private ArrayList<NoteDictionary> noteArrayList;
    private NoteCustomAdapter noteAdapter;

    EditText edittextSearch;

    TextView textviewExplain;
    String userEmail;
    String userName;
    String noteNumber;

    String noteTitle;
    String noteDate;
    String noteContents;
    int pos;

    Animation fadeinAnimation;
    Animation fadeoutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        //뒤로가기 버튼 생성
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        fadeinAnimation = AnimationUtils.loadAnimation(NoteActivity.this, R.anim.fadeinlogo);
        fadeoutAnimation = AnimationUtils.loadAnimation(NoteActivity.this, R.anim.fadeout);

        userEmail = getIntent().getStringExtra("userEmail");
        userName = getIntent().getStringExtra("userName");
        setTitle(userName+"님의 설교노트");
        textviewExplain = findViewById(R.id.textview_explain);
        edittextSearch = findViewById(R.id.edittext_search);

        //아이템뷰가 나열되는 형태를 관리하기위한 레이아웃매니저 객체 생성
        RecyclerView noteRecyclerview = (RecyclerView) findViewById(R.id.recyclerview_note);
        LinearLayoutManager noteLinearLayoutManager = new LinearLayoutManager(this);
        noteRecyclerview.setLayoutManager(noteLinearLayoutManager);
        //어레이리스트와 어탭터 연결
        noteArrayList = new ArrayList<>();

        noteAdapter = new NoteCustomAdapter( this, noteArrayList);
        noteRecyclerview.setAdapter(noteAdapter);

        FloatingActionButton FloatingActionButtonInsert = (FloatingActionButton)findViewById(R.id.FloatingActionButton_write);

        //DB에 저장된 노트 불러오기 -> 어레이 리스트 -> 리사이클러뷰에 표현
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
                            String noteTitle = jsonObject.getString("noteTitle");
                            String noteDate = jsonObject.getString("noteDate");
                            String noteContents = jsonObject.getString("noteContents");
                            String noteNumber = jsonObject.getString("noteNumber");

                            NoteDictionary dict = new NoteDictionary(noteTitle, noteDate, noteContents, noteNumber);
                            noteArrayList.add(0, dict);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    noteAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        NoteReadRequest noteReadRequest = new NoteReadRequest(userEmail, responseListener);
        RequestQueue queue = Volley.newRequestQueue(NoteActivity.this);
        queue.add(noteReadRequest);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                // 시간 지난 후 실행할 코딩
                Log.d("어레이리스트 사이즈", String.valueOf(noteArrayList.size()));
                if(noteArrayList.size()==0){
                    textviewExplain.setVisibility(View.VISIBLE);
                    textviewExplain.setText("화면 우측 하단의 아이콘을 누르고 설교노트를 기록해보세요!");
                    textviewExplain.startAnimation(fadeinAnimation);
                }else{
                    textviewExplain.setVisibility(View.GONE);
                }
            }
        }, 1000); // 0.7초후

        edittextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    String Search = edittextSearch.getText().toString();
                    String email = userEmail;

                    noteArrayList.clear();
//                    noteAdapter.notifyDataSetChanged();

                    //검색된 결과를 다시 배열함.
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
                                        String noteTitle = jsonObject.getString("noteTitle");
                                        String noteDate = jsonObject.getString("noteDate");
                                        String noteContents = jsonObject.getString("noteContents");
                                        String noteNumber = jsonObject.getString("noteNumber");
                                        Log.d("디버그태그", noteTitle);

                                        NoteDictionary dict = new NoteDictionary(noteTitle, noteDate, noteContents, noteNumber);
                                        noteArrayList.add(0, dict);
//                                        noteAdapter.notifyItemInserted(0);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                noteAdapter.notifyDataSetChanged();
                                textviewExplain.setVisibility(View.GONE);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    };
                    NoteSearchRequest noteSearchRequest = new NoteSearchRequest(email, Search, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(NoteActivity.this);
                    queue.add(noteSearchRequest);
                }
                return false;
            }
        });

        //설교노트 작성 버튼
        FloatingActionButtonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this, NoteWriteActivity.class);
                intent.putExtra("noteTitle","");
                intent.putExtra("noteDate","");
                intent.putExtra("noteContents","");
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        noteAdapter.setOnItemClickListener(new NoteCustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int itemPosition) {
                // 아이템에 있는 메뉴를 클릭했을 때 실행하는 메소드
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
                builder.setItems(R.array.noteMenu, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int position)
                    {
                        String[] items = getResources().getStringArray(R.array.noteMenu);
                        switch(items[position]){
                            case "설교노트 수정하기" :
                                //해당 아이템의 노트넘버 가져오기
                                noteNumber = noteArrayList.get(itemPosition).getNoteNumber();
                                noteTitle = noteArrayList.get(itemPosition).getNoteTitle();
                                noteDate = noteArrayList.get(itemPosition).getNoteDate();
                                noteContents = noteArrayList.get(itemPosition).getNoteContents();
                                Intent intent = new Intent(NoteActivity.this, NoteWriteActivity.class);
                                intent.putExtra("noteTitle",noteTitle);
                                intent.putExtra("noteDate",noteDate);
                                intent.putExtra("noteContents",noteContents);
                                pos = itemPosition;
                                startActivityForResult(intent, 2);
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                break;

                            case "설교노트 삭제하기":
                                //해당 아이템의 노트넘버 가져오기
                                noteNumber = noteArrayList.get(itemPosition).getNoteNumber();
                                //DB의 데이터 삭제 + 리사이클러뷰 보이는 것도 삭제
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String Response) {
                                        try{
                                            JSONObject jsonobject = new JSONObject(Response);
                                            boolean Success = jsonobject.getBoolean("success");
                                            if(Success){
                                                noteArrayList.remove(itemPosition);
                                                noteAdapter.notifyItemRemoved(itemPosition);
                                                noteAdapter.notifyItemRangeChanged(itemPosition, noteArrayList.size());
                                            } else {
                                                Toast.makeText(getApplicationContext(), "노트 삭제에 실패하였습니다",Toast.LENGTH_SHORT).show();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                NoteDeleteRequest noteDeleteRequest = new NoteDeleteRequest(noteNumber, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(NoteActivity.this);
                                queue.add(noteDeleteRequest);
                                break;

                            case "설교노트 공유하기":
                                noteTitle = noteArrayList.get(itemPosition).getNoteTitle();
                                noteContents = noteArrayList.get(itemPosition).getNoteContents();
                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                sharingIntent.setType("text/html");
                                sharingIntent.putExtra(Intent.EXTRA_TEXT, "제목 : " + noteTitle +"\n\n내용 : "+noteContents);
                                startActivity(Intent.createChooser(sharingIntent,"설교노트 내용 공유"));
                                break;
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }) ;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 노트를 처음 작성할 때
        if (requestCode == 1 && resultCode == RESULT_OK) {
            assert data != null;
            noteTitle = data.getStringExtra("noteTitle");
            noteDate = data.getStringExtra("noteDate");
            noteContents = data.getStringExtra("noteContents");

            //설교노트 등록 (서버+리사이클러뷰)
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success){
                            Toast.makeText(getApplicationContext(), "설교노트 작성이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                            // 작성한 파일을 바로 수정,삭제하려 했을 때 안돼서 내린 조치임(액티비티 재시작)
                            textviewExplain.setVisibility(View.GONE);
                            finish();
                            Intent intent = new Intent(NoteActivity.this, NoteActivity.class);
                            intent.putExtra("userEmail",userEmail);
                            intent.putExtra("userName",userName);
                            NoteActivity.this.startActivity(intent);
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        } else {
                            Toast.makeText(getApplicationContext(), "설교노트 작성에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            NoteCreateRequest noteCreateRequest = new NoteCreateRequest(userEmail, noteTitle, noteDate, noteContents, responseListener);
            RequestQueue queue = Volley.newRequestQueue(NoteActivity.this);
            queue.add(noteCreateRequest);
        }

        //노트를 수정할 때
        if (requestCode == 2 && resultCode == RESULT_OK) {
            assert data != null;
            noteTitle = data.getStringExtra("noteTitle");
            noteDate = data.getStringExtra("noteDate");
            noteContents = data.getStringExtra("noteContents");

            //설교노트 수정 (서버+리사이클러뷰)
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success){
                            NoteDictionary dict = new NoteDictionary(noteTitle, noteDate, noteContents,"");
                            noteArrayList.set(pos, dict);
                            noteAdapter.notifyItemChanged(pos);
                        } else {
                            Toast.makeText(getApplicationContext(), "설교노트 수정에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            NoteUpdateRequest noteUpdateRequest = new NoteUpdateRequest(noteTitle, noteContents, noteNumber, responseListener);
            RequestQueue queue = Volley.newRequestQueue(NoteActivity.this);
            queue.add(noteUpdateRequest);
        }
    }

}