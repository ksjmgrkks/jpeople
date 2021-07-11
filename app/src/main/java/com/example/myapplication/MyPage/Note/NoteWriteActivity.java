package com.example.myapplication.MyPage.Note;
import android.util.Log;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.util.Calendar;

public class NoteWriteActivity extends AppCompatActivity {

    Calendar cal = Calendar.getInstance();
    //현재 년도, 월, 일
    int year = cal.get (Calendar.YEAR);
    int month = cal.get (Calendar.MONTH);
    int date = cal.get (Calendar.DATE) ;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_write);

        setTitle("설교노트");

        String noteTitle = getIntent().getStringExtra("noteTitle");
        Log.d("noteTitle", noteTitle);
        String noteDate = getIntent().getStringExtra("noteDate");
        Log.d("noteDate", noteDate);
        String noteContents = getIntent().getStringExtra("noteContents");
        Log.d("noteContents", noteContents);

        final Button buttonSubmit = findViewById(R.id.button_submit);
        final TextView textviewDate = findViewById(R.id.textview_date);
        final EditText edittextTitle = findViewById(R.id.edittext_title);
        final EditText edittextContents = findViewById(R.id.edittext_contents);
        if(!noteTitle.equals("")){
            edittextTitle.setText(noteTitle);
        }
        if(!noteContents.equals("")){
            edittextContents.setText(noteContents);
        }
        if(!noteTitle.equals("")){
            textviewDate.setText(noteDate);
            buttonSubmit.setText("설교노트 수정하기");
        }else{
            textviewDate.setText(String.format("%d년 %d월 %d일", year,month+1,date));
            buttonSubmit.setText("설교노트 작성하기");
        }

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (edittextTitle.length() == 0){
                    Toast.makeText(NoteWriteActivity.this, "제목을 입력해주세요",Toast.LENGTH_SHORT).show();
                    edittextTitle.requestFocus();
                    return;
                }
                if (edittextContents.length() == 0){
                    Toast.makeText(NoteWriteActivity.this, "내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                    edittextContents.requestFocus();
                    return;
                }
                String noteTitle = edittextTitle.getText().toString();
                String noteDate = textviewDate.getText().toString();
                String noteContents = edittextContents.getText().toString();

                Intent intent = new Intent();//startActivity()를 할것이 아니므로 그냥 빈 인텐트로 만듦
                intent.putExtra("noteTitle",noteTitle);
                intent.putExtra("noteDate",noteDate);
                intent.putExtra("noteContents",noteContents);
                setResult(RESULT_OK,intent);
                finish();

            }
        });
    }
}