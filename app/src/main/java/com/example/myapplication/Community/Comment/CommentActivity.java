package com.example.myapplication.Community.Comment;
import android.view.View;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.Test.Expandable.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    int CommentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        setTitle("게시물");

        //뒤로가기 버튼 생성
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String cwNumber = getIntent().getStringExtra("cwNumber");
        String cwWriter = getIntent().getStringExtra("cwWriter");
        String cwWriterGroup = getIntent().getStringExtra("cwWriterGroup");
        String cwTitle = getIntent().getStringExtra("cwTitle");
        String cwContents = getIntent().getStringExtra("cwContents");
        String userName = getIntent().getStringExtra("userName");

        TextView textViewCwWriter = findViewById(R.id.textView_writer);
        TextView textViewCwWriterGroup = findViewById(R.id.textView_group);
        TextView textViewCwTitle = findViewById(R.id.textView_title);
        TextView textViewCwContents = findViewById(R.id.textView_contents);
        ImageView imageViewCwProfile = findViewById(R.id.imageView_profile);
        ImageView imageViewUserProfile = findViewById(R.id.imageView_user_profile);

        EditText editTextWriteComment = findViewById(R.id.editText_write_comment);
        Button buttonWriteComment = findViewById(R.id.button_write_comment);

        buttonWriteComment.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              // 댓글을 남길 때 (해당 글에 해당하는 정보를 가져와 댓글을 db에 기록함)
              // CommentState == 0
              String stringComment = editTextWriteComment.getText().toString();
              editTextWriteComment.setText("");

              //답글을 남길 때 (@이름 이 입력되고 답글을 남기는 중 레이아웃을 아래에서 위로 올라가게 표시함)
              // x를 누르면 일반 댓글 상태로 전환됨
              // CommentState == 1


          }
        });

        // 글
        textViewCwWriter.setText(cwWriter);
        textViewCwWriterGroup.setText(cwWriterGroup);
        textViewCwTitle.setText(cwTitle);
        textViewCwContents.setText(cwContents);
        Glide.with(this)
                .load(Uri.parse("http://49.247.146.128/image/"+cwWriter+".png"))
                .circleCrop()
                .into(imageViewCwProfile);
        Glide.with(this)
                .load(Uri.parse("http://49.247.146.128/image/"+userName+".png"))
                .circleCrop()
                .into(imageViewUserProfile);



        // 댓글, 대댓글

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        List<ExpandableListAdapter.Item> data = new ArrayList<>();

        ExpandableListAdapter.Item ss = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "123");
        ss.invisibleChildren = new ArrayList<>();
        data.add(ss);

        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Cars"));
        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Audi"));
        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Aston Martin"));
        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "BMW"));
        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Cadillac"));

        ExpandableListAdapter.Item places = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Places");
        places.invisibleChildren = new ArrayList<>();
        places.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Kerala"));
        places.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Tamil Nadu"));
        places.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Karnataka"));
        places.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Maharashtra"));

        data.add(places);

        recyclerview.setAdapter(new ExpandableListAdapter(data));

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