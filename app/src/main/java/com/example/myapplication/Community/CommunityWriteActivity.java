package com.example.myapplication.Community;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.Test.ViewPager2.ImageSliderAdapter;
import com.example.myapplication.Test.ViewPager2.SliderDictionary;

import org.json.JSONArray;
import org.json.JSONException;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommunityWriteActivity extends AppCompatActivity {
    public String uploadFilePath;
    // 파일을 업로드 하기 위한 변수 선언
    private int serverResponseCode = 0;

    ViewPager2 sliderViewPager;
    LinearLayout layoutIndicator;
    TextView textViewPage;

    Calendar cal = Calendar.getInstance();
    //현재 년도, 월, 일
    int year = cal.get (Calendar.YEAR);
    int month = cal.get (Calendar.MONTH);
    int date = cal.get (Calendar.DATE) ;

    int PICK_IMAGE_MULTIPLE = 1;
    List<String> imagesEncodedList;

    private ArrayList<SliderDictionary> SliderList;
    ImageSliderAdapter imageSliderAdapter;
    ArrayList<Uri> mArrayUri;
    String imageFileName;
    String userName;
    JSONArray filePathArray;
    int vpPage;
    JSONArray jsonArray;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_write);
        setTitle("게시글 작성하기");

        sliderViewPager = findViewById(R.id.sliderViewPager);
        layoutIndicator = findViewById(R.id.layoutIndicators);
        textViewPage = findViewById(R.id.textView_page);
        textViewPage.setVisibility(View.INVISIBLE);
        sliderViewPager.setVisibility(View.GONE);

        sliderViewPager.setOffscreenPageLimit(1);
        SliderList = new ArrayList<>(); // 뷰페이저 어레이리스트
        filePathArray = new JSONArray(); // db에 저장할 json 어레이

        imageSliderAdapter = new ImageSliderAdapter(this, SliderList);
        sliderViewPager.setAdapter(imageSliderAdapter);
        // 뷰페이저 페이지가 변경될 때마다 실행할 코드
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                textViewPage.setText(String.format("%d/%d", position+1, SliderList.size()));
                vpPage = position;
            }
        });

        // 페이지를 수정할 때 가져오는 값들
        userName = getIntent().getStringExtra("userName");
        String cwTitle = getIntent().getStringExtra("cwTitle");
        String cwDate = getIntent().getStringExtra("cwCreated");
        String cwContents = getIntent().getStringExtra("cwContents");
        String cwImage = getIntent().getStringExtra("cwImage");
        String cwNumber = getIntent().getStringExtra("cwNumber");
        String cwLike = getIntent().getStringExtra("cwLike");
        String cwComment = getIntent().getStringExtra("cwComment");
        String cwProfile = getIntent().getStringExtra("cwProfile");
        String cwLikeIsset = getIntent().getStringExtra("cwLikeIsset");
        String cwGroup = getIntent().getStringExtra("cwGroup");

        final Button buttonSubmit = findViewById(R.id.button_submit);
        final EditText edittextTitle = findViewById(R.id.edittext_community_title);
        final EditText edittextContents = findViewById(R.id.edittext_community_contents);
        final ImageView imageViewAddImage = findViewById(R.id.imageView_add_image);

        // 가져오는 값이 있냐 없냐에 따라 게시글을 작성하는 상황인지, 수정하는 상황인지 구분해주기 위한 코드
        if(!cwTitle.equals("")){
            edittextTitle.setText(cwTitle);
        }
        if(!cwContents.equals("")){
            edittextContents.setText(cwContents);
        }
        if(!cwImage.equals("")){
            Log.d("이미지 잘 왔니", cwImage);
            sliderViewPager.setVisibility(View.VISIBLE);
            //제이슨 어레이에 이미지 넣어주기
            try {
                jsonArray = new JSONArray(cwImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(jsonArray.length() > 1){
                textViewPage.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < jsonArray.length() ; i++){
                try {
                    Log.d("제이슨어레이", jsonArray.getString(i));
                    SliderList.add(new SliderDictionary(Uri.parse(jsonArray.getString(i))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            imageSliderAdapter.notifyDataSetChanged();
        }
        if(!cwDate.equals("")){
            buttonSubmit.setText("게시글 수정하기");
        }else{
            // 게시글을 작성하는 경우에는 날짜를 현재 날짜로 설정해준다.
            cwDate = String.format("%d년 %d월 %d일", year,month+1,date);
            buttonSubmit.setText("게시글 작성하기");
        }

        // 뷰페이저에서 삭제 아이콘을 클릭했을 때 처리
        imageSliderAdapter.setOnItemClickListener(new ImageSliderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, int clickItem) {
                // 아이템 삭제를 클릭했을 때
                if(clickItem == 0){
                    SliderList.remove(position);
                    imageSliderAdapter.notifyItemRemoved(position);
                    if(SliderList.size()==0){
                        // 아이템이 없을 경우에는 뷰페이저 자체를 보이지 않게 한다.
                        sliderViewPager.setVisibility(View.GONE);
                    }
                    if(SliderList.size()==1){
                        // 아이템이 1개일 경우에는 페이지표시하는 텍스트뷰를 보이지 않게 한다.
                        textViewPage.setVisibility(View.INVISIBLE);
                    }
                    if(vpPage == 0){
                        // 첫번째 아이템을 보고있는 상태에서 아이템 삭제를 하면 오류가 생겨서 텍스트뷰를 갱신해줌
                        textViewPage.setText(String.format("%d/%d", vpPage+1, SliderList.size()));
                    }
                }
            }
        });

        // 게시물을 작성하거나 수정할 때
        String cwCreated = cwDate;
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (edittextTitle.length() == 0){
                    Toast.makeText(CommunityWriteActivity.this, "제목을 입력해주세요",Toast.LENGTH_SHORT).show();
                    edittextTitle.requestFocus();
                    return;
                }
                if (edittextContents.length() == 0){
                    Toast.makeText(CommunityWriteActivity.this, "내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                    edittextContents.requestFocus();
                    return;
                }

                //SliderList 는 뷰페이저의 이미지를 담는 어레이리스트다.
                //mArrayUri 는 뷰페이저에 담긴 이미지의 Uri를 담는 어레이리스트다.
//                if(mArrayUri == null || SliderList.size() == 0){
                 if(SliderList.size() == 0){
                    // 어레이에 담긴 Uri 가 없거나, 뷰페이저에 이미지가 추가되지 않았을 때 실행하는 코드
                    String noteTitle = edittextTitle.getText().toString();
                    String noteContents = edittextContents.getText().toString();

                    Intent intent = new Intent();
                     intent.putExtra("cwNumber",cwNumber);
                     intent.putExtra("cwLike",cwLike);
                     intent.putExtra("cwComment",cwComment);
                     intent.putExtra("cwProfile",cwProfile);
                     intent.putExtra("cwLikeIsset",cwLikeIsset);
                     intent.putExtra("cwGroup",cwGroup);

                    intent.putExtra("cwTitle",noteTitle);
                    intent.putExtra("cwCreated", cwCreated);
                    intent.putExtra("cwContents",noteContents);
                    intent.putExtra("filePathArray", "");
                    intent.putExtra("totalPage", "0");
                    setResult(RESULT_OK,intent);
                    finish();
                }else{
                    Log.d("디버그태그", String.valueOf(mArrayUri));
                    for (int i = 0; i < SliderList.size(); i++){
                        String confirm = SliderList.get(i).getImageUri().toString().substring(0,4);
                        if(confirm.equals("http")){
                            filePathArray.put(SliderList.get(i).getImageUri().toString());
                        }else{
                            String path = getRealPathFromURI(SliderList.get(i).getImageUri());
                            uploadFilePath = path;

                            if (uploadFilePath != null) {
                                UploadImageToServer uploadimagetoserver = new UploadImageToServer();
                                uploadimagetoserver.execute("http://49.247.146.128/community-upload.php");
                            } else {
                                Toast.makeText(CommunityWriteActivity.this, "이미지 없음", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    while(true){
                        // SliderList 와 filePathArray 의 크기가 다를 때가 있어서 써준 while 문이다.(같아질 때까지 대기)
                        if(filePathArray.length() == SliderList.size()){
                            String cwTitle = edittextTitle.getText().toString();
                            String cwContents = edittextContents.getText().toString();

                            Intent intent = new Intent();//startActivity()를 할것이 아니므로 그냥 빈 인텐트로 만듦
                            intent.putExtra("cwNumber",cwNumber);
                            intent.putExtra("cwLike",cwLike);
                            intent.putExtra("cwComment",cwComment);
                            intent.putExtra("cwProfile",cwProfile);
                            intent.putExtra("cwLikeIsset",cwLikeIsset);
                            intent.putExtra("cwGroup",cwGroup);

                            intent.putExtra("cwTitle",cwTitle);
                            intent.putExtra("cwCreated", cwCreated);
                            intent.putExtra("cwContents",cwContents);
                            intent.putExtra("filePathArray", filePathArray.toString());
                            intent.putExtra("totalPage", filePathArray.length()+"");
                            setResult(RESULT_OK,intent);
                            finish();
                            return;

                        }
                    }
                }

            }
        });

        // 이미지를 추가 버튼 클릭 -> 이미지 다중선택
        imageViewAddImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // 출처 : https://stackoverflow.com/questions/19585815/select-multiple-images-from-android-gallery
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                imagesEncodedList = new ArrayList<String>();
                mArrayUri = new ArrayList<Uri>();
                if(data.getData()!=null){
                    sliderViewPager.setVisibility(View.VISIBLE);

                    Uri mImageUri = data.getData();
                    Log.d("디버그태그1", String.valueOf(mImageUri));

                    mArrayUri.add(mImageUri);

                    Log.d("mImageUri 디버그", String.valueOf(mImageUri));
                    SliderList.add(new SliderDictionary(mImageUri));
                    imageSliderAdapter.notifyDataSetChanged();
                    Log.d("size 디버그", String.valueOf(SliderList.size()));
                    if (SliderList.size() != 1) {
                        textViewPage.setVisibility(View.VISIBLE);
                        textViewPage.setText(String.format("%d/%d", 1, SliderList.size()));
                    }

                } else {
                    if (data.getClipData() != null) {
                        sliderViewPager.setVisibility(View.VISIBLE);

                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        Log.d("mArrayUri 디버그", String.valueOf(mArrayUri));
                        for (int i = 0; i < mArrayUri.size(); i++){
                            SliderList.add(new SliderDictionary(mArrayUri.get(i)));
                        }
                        imageSliderAdapter.notifyDataSetChanged();
                        Log.d("size 디버그", String.valueOf(SliderList.size()));
                        textViewPage.setVisibility(View.VISIBLE);
                        textViewPage.setText(String.format("%d/%d", 1, SliderList.size()));
                    }
                }
            } else {
                Toast.makeText(this, "이미지를 선택하지 않았습니다",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "오류가 발생했습니다", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }
        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = { MediaStore.Files.FileColumns.DATA };
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } finally {
            cursor.close();
        } return null;
    }

    // ============================== 이미지를 서버에 전송하기 위한 스레드 ========================
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
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(CommunityWriteActivity.this);
            mProgressDialog.setTitle("로딩중...");
            mProgressDialog.setMessage("이미지를 업로드중입니다...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }
        @Override
        protected String doInBackground(String... serverUrl) {
            if (!sourceFile.isFile()) {
                runOnUiThread(new Runnable() {
                    public void run() {
                    }
                });
                return null;
            } else {
                try {
                    long now = System.currentTimeMillis();
                    Date mDate = new Date(now);
                    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMddhhmmss");
                    String getTime = simpleDate.format(mDate);

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

                    dos = new DataOutputStream(conn.getOutputStream());

                    // 사용자 이름으로 폴더를 생성하기 위해 사용자 이름을 서버로 전송한다.
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"data\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    String[] str = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                            "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
                    String newCode = "";

                    for (int x = 0; x < 8; x++) {
                        int random = (int) (Math.random() * str.length);
                        newCode += str[random];
                    }
                    imageFileName = userName+"커뮤니티"+newCode;
                    // 업로드 한 후 파일의 경로를 Array에 담아서 DB에 저장해야 한다.
                    filePathArray.put("http://49.247.146.128/image/"+imageFileName+".png");
                    dos.writeBytes( URLEncoder.encode (imageFileName, "utf-8"));
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

                    if (serverResponseCode == 200) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                BufferedReader br = null;
                                try {
                                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String outputString = "";
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
                                    outputString += line;
                                }

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
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }
                return null;
            } // End else block
        }
        @Override
        protected void onPostExecute(String s) {
//            mProgressDialog.dismiss();
        }
    }

}