package com.example.myapplication.MyPage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.Community.CommunityActivity;
import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.Login.LoginActivity;
import com.example.myapplication.MyPage.Manage.ManageActivity;
import com.example.myapplication.MyPage.Note.NoteActivity;
import com.example.myapplication.MyPage.Offering.OfferingActivity;
import com.example.myapplication.MyPage.QRCheck.QRCheckActivity;
import com.example.myapplication.R;
import com.example.myapplication.SproutMeeting.SproutMeetingActivity;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MyPageActivity extends AppCompatActivity {

    private long back_button_time = 0;

    private String TAGLOG = "MyPageLog";
    // 서버로 업로드할 파일관련 변수
    public String uploadFilePath;
    private int REQ_CODE_PICK_PICTURE = 1;
    // 파일을 업로드 하기 위한 변수 선언
    private int serverResponseCode = 0;
    ImageView imageviewProfile;
    String userEmail;
    String userName;

    //카메라 관련 변수
    File file;

    private static final int MY_PERMISSION_CAMERA = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //권한 체크
        checkPermission();

        imageviewProfile = findViewById(R.id.imageview_profile);
        ImageView imageviewCamera = findViewById(R.id.imageview_camera);

        TextView textviewName = findViewById(R.id.textview_name);
        TextView textviewEmail = findViewById(R.id.textview_email);
        TextView textviewGroup = findViewById(R.id.textview_group);
        TextView textviewManage = findViewById(R.id.textview_manage);
        TextView textviewNote = findViewById(R.id.textview_note);
        TextView textviewOffering = findViewById(R.id.textview_offering);
        TextView textviewQR = findViewById(R.id.textview_qr);
        TextView textviewLogout = findViewById(R.id.textview_logout);

        userEmail = getIntent().getStringExtra("userEmail");
        String userPassword = getIntent().getStringExtra("userPassword");
        userName = getIntent().getStringExtra("userName");
        Log.d(TAGLOG, userName);
        String userGroup = getIntent().getStringExtra("userGroup");
        String userProfile = getIntent().getStringExtra("userProfile");

        textviewEmail.setText(userEmail);
        textviewName.setText(userName);
        textviewGroup.setText(userGroup);

        //프로필사진 설정
        //글라이드로 프로필 다운로드
        Uri profileUri = Uri.parse(userProfile);
        Glide.with(this).load(profileUri).placeholder(R.drawable.firstprofile).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).error(R.drawable.firstprofile).circleCrop().into(imageviewProfile);
        Glide.with(this).load(R.drawable.cameraimage).circleCrop().into(imageviewCamera);

        imageviewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
                builder.setTitle("프로필사진 변경");
                builder.setItems(R.array.Image_Pick, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int position)
                    {
                        String[] items = getResources().getStringArray(R.array.Image_Pick);
                        switch(items[position]){
                            case "카메라에서 가져오기" :
                                File sdcard = Environment.getExternalStorageDirectory();
                                file = new File(sdcard, ""+userName+".png");
                                Log.d(TAGLOG, String.valueOf(sdcard));
                                capture();
                                break;
                            case "갤러리에서 가져오기":
                                Intent i = new Intent(Intent.ACTION_PICK);
                                i.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
                                // 결과를 리턴하는 Activity 호출
                                startActivityForResult(i, REQ_CODE_PICK_PICTURE);
                                break;
                            case "기본 이미지로 변경하기":
                                Glide.with(MyPageActivity.this).load(R.drawable.firstprofile).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).error(R.drawable.firstprofile).into(imageviewProfile);
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String Response) {
                                        try{
                                            JSONObject jsonobject = new JSONObject(Response);
                                            boolean Success = jsonobject.getBoolean("success");
                                            if(Success){ // 비밀번호 변경에 성공한 경우
                                                Toast.makeText(getApplicationContext(), "기본이미지로 변경되었습니다",Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "프로필 변경에 실패하였습니다",Toast.LENGTH_SHORT).show();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                // 서버로 Volley를 이용해서 요청을 하는 부분
                                ProfileDeleteRequest profileDeleteRequest = new ProfileDeleteRequest(userEmail, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(MyPageActivity.this);
                                queue.add(profileDeleteRequest);
                                break;
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //회원정보 관리
        textviewManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageActivity.this, ManageActivity.class);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                intent.putExtra("userProfile",userProfile);
                MyPageActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        //설교노트
        textviewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageActivity.this, NoteActivity.class);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                intent.putExtra("userProfile",userProfile);
                MyPageActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        //헌금
        textviewOffering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageActivity.this, OfferingActivity.class);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                intent.putExtra("userProfile",userProfile);
                MyPageActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        //QR 체크인
        textviewQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageActivity.this, QRCheckActivity.class);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                intent.putExtra("userProfile",userProfile);
                MyPageActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
        //로그 아웃
        textviewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
                builder.setTitle("로그아웃 하시겠습니까?");
                builder.setItems(R.array.Yes_Or_No, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int position)
                    {
                        String[] items = getResources().getStringArray(R.array.Yes_Or_No);
                        switch(items[position]){
                            case "예" :
                                SharedPreferences preference = getSharedPreferences("userInfo", MODE_PRIVATE);
                                SharedPreferences.Editor editor =preference.edit();
                                editor.remove("userEmail");
                                editor.remove("userName");
                                editor.remove("userPassword");
                                editor.remove("userGroup");
                                editor.remove("userProfile");
                                editor.apply();

                                Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                MyPageActivity.this.startActivity(intent);

                                Toast.makeText(getApplicationContext(), "로그아웃 완료", Toast.LENGTH_SHORT).show();
                                break;
                            case "아니요":
                                break;
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //TabLayout 을 하단에 배치하여 유저가 어플의 주요 기능을 알아보기 쉽고,
        // 이용이 편리하도록 하였습니다.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab) ;
        tabLayout.selectTab(tabLayout.getTabAt(3));
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
        String userEmail = getIntent().getStringExtra("userEmail");
        String userPassword = getIntent().getStringExtra("userPassword");
        String userName = getIntent().getStringExtra("userName");
        String userGroup = getIntent().getStringExtra("userGroup");
        String userProfile = getIntent().getStringExtra("userProfile");
        Intent intent;
        switch (index) {
            case 0 :
                intent = new Intent(MyPageActivity.this, HomeActivity.class);
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
                intent = new Intent(MyPageActivity.this, CommunityActivity.class);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userName",userName);
                intent.putExtra("userGroup",userGroup);
                intent.putExtra("userProfile",userProfile);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break ;
            case 2 :
                intent = new Intent(MyPageActivity.this, SproutMeetingActivity.class);
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
                break ;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //갤러리
        if (requestCode == REQ_CODE_PICK_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                String path = getPath(uri);

                uploadFilePath = path;

                if (uploadFilePath != null) {
                    UploadImageToServer uploadimagetoserver = new UploadImageToServer();
                    uploadimagetoserver.execute("http://49.247.146.128/upload-profile.php");
                } else {
                    Toast.makeText(MyPageActivity.this, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        }

        //카메라
        if(requestCode == MY_PERMISSION_CAMERA  /*&& resultCode == Activity.RESULT_OK*/){

            uploadFilePath = "/storage/emulated/0/"+userName+".png";
            Log.d(TAGLOG, uploadFilePath);

            if (uploadFilePath != null) {
                UploadImageToServer uploadimagetoserver = new UploadImageToServer();
                uploadimagetoserver.execute("http://49.247.146.128/upload-profile.php");
            } else {
                Toast.makeText(MyPageActivity.this, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 실제 경로 찾기
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // ============================== 사진을 서버에 전송하기 위한 스레드 ========================
    private class UploadImageToServer extends AsyncTask<String, String, String> {
        ProgressDialog mProgressDialog;
        String fileName = uploadFilePath;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 10240 * 10240;
        File sourceFile = new File(uploadFilePath);

        @Override
        protected void onPreExecute() {
            // 후에 제이피플 애니메이션으로 교체하기
            mProgressDialog = new ProgressDialog(MyPageActivity.this);
            mProgressDialog.setTitle("로딩중...");
            mProgressDialog.setMessage("프로필사진을 업로드중입니다...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }
        @Override
        protected String doInBackground(String... serverUrl) {
            if (!sourceFile.isFile()) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i(TAGLOG, "[UploadImageToServer] Source File not exist :" + uploadFilePath);
                    }
                });
                return null;
            } else {
                try {
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(serverUrl[0]);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);
                    Log.i(TAGLOG, "fileName: " + fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    // 사용자 이름으로 폴더를 생성하기 위해 사용자 이름을 서버로 전송한다.
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"data\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    Log.d(TAGLOG, userName);
                    dos.writeBytes( URLEncoder.encode (userName, "utf-8"));
                    dos.writeBytes(lineEnd);

                    // 이미지 전송
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + fileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();


                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];



                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);



                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();
                    Log.i(TAGLOG, "[UploadImageToServer] HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                    if (serverResponseCode == 200) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                BufferedReader br = null;
                                try {
                                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String output = "";
                                int i = 0 ;
                                for(;;){
                                    //웹상에 보이는 텍스트를 라인단위로 읽어 저장
                                    String line = null;
                                    try {
                                        line = br.readLine();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if(line == null) {
                                        break;
                                    }
                                    i++;
                                    output += line;
                                }
                                Log.d("디버그태그", output);
                                Toast.makeText(MyPageActivity.this, "프로필사진 변경 완료", Toast.LENGTH_SHORT).show();
                                String userEmail = getIntent().getStringExtra("userEmail");
                                String userPassword = getIntent().getStringExtra("userPassword");
                                String userName = getIntent().getStringExtra("userName");
                                String userGroup = getIntent().getStringExtra("userGroup");
                                String userProfile = getIntent().getStringExtra("userProfile");
                                finish();
                                Intent intent = new Intent(MyPageActivity.this, MyPageActivity.class);
                                intent.putExtra("userEmail",userEmail);
                                intent.putExtra("userPassword",userPassword);
                                intent.putExtra("userName",userName);
                                intent.putExtra("userGroup",userGroup);
                                intent.putExtra("userProfile",userProfile);
                                MyPageActivity.this.startActivity(intent);
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                            }
                        });
                    }

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MyPageActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.i(TAGLOG, "[UploadImageToServer] error: " + ex.getMessage(), ex);
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MyPageActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.i(TAGLOG, "[UploadImageToServer] Upload file to server Exception Exception : " + e.getMessage(), e);
                }
                Log.i(TAGLOG, "[UploadImageToServer] Finish");
                return null;
            } // End else block
        }
        @Override
        protected void onPostExecute(String s) {
            mProgressDialog.dismiss();
        }
    }


    public void capture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(MyPageActivity.this, "com.example.myapplication.fileprovider", file));
        Log.i(TAGLOG, String.valueOf(intent));
        startActivityForResult(intent, MY_PERMISSION_CAMERA);
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CAMERA:
                for (int i = 0; i < grantResults.length; i++) {
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if (grantResults[i] < 0) {
                        Toast.makeText(MyPageActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면 이 부분에서..

                break;
        }
    }

}