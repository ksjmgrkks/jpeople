package com.example.myapplication.MyPage.Offering;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.text.DecimalFormat;

public class OfferingActivity extends AppCompatActivity {
    String userName;
    String offeringContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offering);

        //뒤로가기 버튼 생성
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("헌금");

        userName = getIntent().getStringExtra("userName");

        Spinner spinnerOffering = findViewById(R.id.spinner_offering);
        Button buttonSubmit = findViewById(R.id.button_submit);
        EditText edittextOfferingMoney = findViewById(R.id.edittext_offering_money);
        EditText edittextOfferingContents = findViewById(R.id.edittext_offering_contents);

        ArrayAdapter offeringAdapter = ArrayAdapter.createFromResource(this, R.array.offering, android.R.layout.simple_spinner_item);
        offeringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOffering.setAdapter(offeringAdapter); //어댑터에 연결해줍니다.
        spinnerOffering.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectHint = "헌금 종류를 선택해주세요";
                String select = ""+parent.getItemAtPosition(position)+"";

                if(!selectHint.equals(select)){
                    buttonSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(edittextOfferingMoney.getText().toString().equals("")){
                                Toast.makeText(OfferingActivity.this, "헌금 금액을 입력해주세요",Toast.LENGTH_SHORT).show();
                                edittextOfferingMoney.requestFocus();
                                return;
                            }
                            DecimalFormat moneyFormatter = new DecimalFormat("###,###,###");
                            int integer = Integer.parseInt(edittextOfferingMoney.getText().toString());
                            if(integer>=1000000000){
                                Toast.makeText(OfferingActivity.this, "10자리 이하의 금액을 입력해주세요",Toast.LENGTH_SHORT).show();
                            }else{
                                String money = moneyFormatter.format(integer);
                                Log.d("디버그태그", money);
                                AlertDialog.Builder builder = new AlertDialog.Builder(OfferingActivity.this);
                                builder.setTitle("헌금 종류와 금액을 확인해주세요");
                                builder.setMessage("헌금 종류 : "+select+"\n"
                                        +"헌금 금액 : "+money+"원\n");
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        offeringContents = edittextOfferingContents.getText().toString();

                                        PayActivity payActivity = new PayActivity(select, edittextOfferingMoney.getText().toString());
                                        Intent intent = new Intent(getApplicationContext(), payActivity.getClass());
                                        intent.putExtra("userName", userName);
                                        intent.putExtra("offeringContents", offeringContents);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                builder.setNegativeButton("취소",null);
                                builder.create().show();
                            }

                        }
                    });
                }else{
                    buttonSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(OfferingActivity.this, "헌금 종류를 선택해주세요",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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