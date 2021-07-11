package com.example.myapplication.SproutMeeting;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class SproutUpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprout_update);

        //뒤로가기 버튼 생성
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("순 정보 수정하기");

        String sLink = getIntent().getStringExtra("sLink");
        String sNotice = getIntent().getStringExtra("sNotice");

        final Button buttonSubmit = findViewById(R.id.button_submit);
        final EditText edittextLink = findViewById(R.id.edittext_link);
        final EditText edittextNotice = findViewById(R.id.edittext_notice);

        edittextLink.setText(sLink);
        edittextNotice.setText(sNotice);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String sLink = edittextLink.getText().toString();
                String sNotice = edittextNotice.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("sLink",sLink);
                intent.putExtra("sNotice",sNotice);
                setResult(RESULT_OK,intent);
                finish();
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