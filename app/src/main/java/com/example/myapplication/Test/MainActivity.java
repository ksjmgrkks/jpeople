package com.example.myapplication.Test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.SproutMeeting.Chat.ChatReadRequest;
import com.example.myapplication.SproutMeeting.Chat.ChatSaveRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    Socket client;
    BufferedWriter output;
    BufferedReader input;
    StringBuffer clientdata;
    String serverdata;

    private String ip = "49.247.146.128";
    private int port = 5000;

    String userName;
    String userGroup;
    Button chatbutton;
    ImageButton imageButtonUpload;
    EditText message;
    String sendmsg;
    String imageFileName;

    String SEPARATOR = "|";
    int REQ_LOGOUT = 1000;
    int REQ_LOGON = 1001;
    int REQ_SENDIMAGE = 1002;
    int REQ_SENDWORDS = 1021;
    int REQ_SENDMEMBERS = 1022;
    int command;

    String chatName;
    private ArrayList<Item> dataList;
    private MyAdapter myAdapter;
    private RecyclerView recyclerView;

    private final int SELECT_IMAGE = 100;

    private String TAGLOG = "MainActivityLog";
    // ????????? ???????????? ???????????? ??????
    public String uploadFilePath;
    private int REQ_CODE_PICK_PICTURE = 1;
    // ????????? ????????? ?????? ?????? ?????? ??????
    private int serverResponseCode = 0;
    //????????? ?????? ??????
    File file;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.actionbar, menu) ;
//        return true ;
//    }
//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_menu :
//                Dialog chatUserDialog;
//                chatUserDialog = new Dialog(MainActivity.this);
//                chatUserDialog.setContentView(R.layout.chat_user_dialog);
//
//                WindowManager.LayoutParams params = chatUserDialog.getWindow().getAttributes();
//                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                chatUserDialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
//                chatUserDialog.show();
//                return true ;
//            default :
//                return super.onOptionsItemSelected(item) ;
//        }
//    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = findViewById(R.id.message);
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        userGroup = intent.getStringExtra("userGroup");

        setTitle(userGroup);
        chatbutton = findViewById(R.id.chatbutton);
        imageButtonUpload = findViewById(R.id.imageButton_upload);

        dataList = new ArrayList<>(); // ?????????????????? ??????????????????

        recyclerView = findViewById(R.id.RecyclerView);
        LinearLayoutManager manager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager); // LayoutManager ??????
        myAdapter = new MyAdapter(dataList);
        recyclerView.setAdapter(myAdapter);

        //DB??? ????????? ????????? ????????? ?????? ???????????? -> ????????? ????????? -> ????????????????????? ??????
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
                            String chatName = jsonObject.getString("chatName");
                            String chatSort = jsonObject.getString("chatSort");
                            String chatContents = jsonObject.getString("chatContents");
                            if(chatName.equals(userName)){
                                if(chatSort.equals("text")){
                                    //????????? ??? ??????????????? ???????????? ???
                                    dataList.add(new Item(chatContents, null, "", null, null, ViewType.RIGHT_CHAT));
                                }else{
                                    //????????? ??? ??????????????? ???????????? ???
                                    dataList.add(new Item(null, null, "", null, chatContents, ViewType.RIGHT_IMAGE));
                                }
                            }else{
                                if(chatSort.equals("text")){
                                    //???????????? ??? ??????????????? ???????????? ???
                                    dataList.add(new Item(chatContents, chatName, "", "http://49.247.146.128/image/"+chatName+".png", null, ViewType.LEFT_CHAT));
                                }else{
                                    //???????????? ??? ??????????????? ???????????? ???
                                    dataList.add(new Item(null, chatName, "", "http://49.247.146.128/image/"+chatName+".png", chatContents, ViewType.LEFT_IMAGE));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        ChatReadRequest chatReadRequest = new ChatReadRequest(userGroup, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(chatReadRequest);

        clientdata = new StringBuffer(); // ??????????????? ?????? ??????


        new Thread() {
            public void run() {
                try {
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    client = new Socket(serverAddr, port);

                    input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
//                    while(true){
//                        serverdata = input.readLine();
//                        if(serverdata!=null){
//                            items.add(serverdata);
//                            Message msg = handler.obtainMessage();
//                            handler.sendMessage(msg);
//                            output.flush();
//                        }
//                    }
                    while((serverdata = input.readLine()) != null) {
                        StringTokenizer st = new StringTokenizer(serverdata, SEPARATOR);
                        command = Integer.parseInt(st.nextToken());

                        if(command == REQ_LOGON){
                            String GroupName = st.nextToken();
                            if(GroupName.equals(userGroup)){
                                String enterMessage = st.nextToken();
                                dataList.add(new Item(enterMessage, null, null, null,null,ViewType.CENTER_JOIN));
                                Message msg = handler.obtainMessage();
                                handler.sendMessage(msg);
                                output.flush();
                            }
                        }

                        if(command == REQ_LOGOUT){
                            String GroupName = st.nextToken();
                            if(GroupName.equals(userGroup)){
                                String exitMessage = st.nextToken();
                                dataList.add(new Item(exitMessage, null, null, null,null,ViewType.CENTER_JOIN));
                                Message msg = handler.obtainMessage();
                                handler.sendMessage(msg);
                                output.flush();
                            }
                        }

                        if(command == REQ_SENDMEMBERS){
                            String GroupName = st.nextToken();
                            if(GroupName.equals(userGroup)){
                                String jsonArray = st.nextToken();
                                dataList.add(new Item(jsonArray, null, null, null,null,ViewType.CENTER_JOIN));
                                Message msg = handler.obtainMessage();
                                handler.sendMessage(msg);
                                output.flush();
                            }
                        }

                        if(command == REQ_SENDWORDS){
                            String GroupName = st.nextToken();
                            if(GroupName.equals(userGroup)){
                                chatName = st.nextToken();
                                String message = st.nextToken();
                                if(chatName.equals(userName)){
                                    //????????? ?????????????????? ????????????
                                    dataList.add(new Item(message, null, "", null, null, ViewType.RIGHT_CHAT));
                                }else{
                                    //?????? ?????????????????? ????????????
                                    dataList.add(new Item(message, chatName, "", "http://49.247.146.128/image/"+chatName+".png", null, ViewType.LEFT_CHAT));
                                }
                                Message msg = handler.obtainMessage();
                                handler.sendMessage(msg);
                                output.flush();
                            }
                        }

                        if(command == REQ_SENDIMAGE){
                            String GroupName = st.nextToken();
                            if(GroupName.equals(userGroup)){
                                chatName = st.nextToken();
                                String fileurl = st.nextToken();
                                if(chatName.equals(userName)){
                                    //????????? ?????????????????? ????????????
                                    dataList.add(new Item(null, null, "", null, fileurl, ViewType.RIGHT_IMAGE));
                                }else{
                                    //?????? ?????????????????? ????????????
                                    dataList.add(new Item(null, chatName, "", "http://49.247.146.128/image/"+chatName+".png", fileurl, ViewType.LEFT_IMAGE));
                                }
                                Message msg = handler.obtainMessage();
                                handler.sendMessage(msg);
                                output.flush();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }}.start();

        clientdata.setLength(0);
        clientdata.append(REQ_LOGON);
        clientdata.append(SEPARATOR);
        clientdata.append(userGroup);
        clientdata.append(SEPARATOR);
        clientdata.append(userName);
        Log.d("???????????????", String.valueOf(clientdata));
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(300); // ?????????????????? ?????? ???????????? ????????? ??????????????? ?????? ????????? 0.3??? ????????? ?????????.
                    output.write(clientdata.toString()+"\r\n");
                    output.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg = message.getText().toString();
                message.setText("");

                clientdata.setLength(0);
                clientdata.append(REQ_SENDWORDS);
                clientdata.append(SEPARATOR);
                clientdata.append(userGroup);
                clientdata.append(SEPARATOR);
                clientdata.append(userName);
                clientdata.append(SEPARATOR);
                clientdata.append(sendmsg);
                Log.d("???????????????", String.valueOf(clientdata));
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            output.write(clientdata.toString()+"\r\n");
                            output.flush();

                            // ????????? ????????? ?????? ??????
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try{
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");
                                        if (!success) {
                                            Toast.makeText(getApplicationContext(), "?????? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            };
                            ChatSaveRequest chatSaveRequest = new ChatSaveRequest(userGroup, userName, "text", sendmsg, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                            queue.add(chatSaveRequest);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("?????? ?????? ?????????", String.valueOf(e));
                        }
                    }
                }.start();
            }
        });

        imageButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("????????? ?????????");
                builder.setItems(R.array.Chat_Image_Pick, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int position)
                    {
                        String[] items = getResources().getStringArray(R.array.Image_Pick);
                        switch(items[position]){
                            case "??????????????? ????????????" :
                                File sdcard = Environment.getExternalStorageDirectory();
                                file = new File(sdcard, ""+userName+".png");
                                capture();
                                break;
                            case "??????????????? ????????????":
                                Intent i = new Intent(Intent.ACTION_PICK);
                                i.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
                                // ????????? ???????????? Activity ??????
                                startActivityForResult(i, REQ_CODE_PICK_PICTURE);
                                break;
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    // ?????????????????? ?????? UI??? ????????? ??? ?????? ????????? ???????????? ????????????!
    final Handler handler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            //?????????????????? ui ?????? ??????
            myAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(myAdapter.getItemCount() - 1);
        }
    };
    // System.currentTimeMillis??? ??????:?????? am/pm ????????? ???????????? ??????
    private String toDate(long currentMiliis) {
        return new SimpleDateFormat("hh:mm a").format(new Date(currentMiliis));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clientdata.setLength(0);
        clientdata.append(REQ_LOGOUT);
        clientdata.append(SEPARATOR);
        clientdata.append(userGroup);
        clientdata.append(SEPARATOR);
        clientdata.append(userName);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    output.write(clientdata.toString()+"\r\n");
                    output.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ??????????????? ????????? ????????? ?????? ??????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //?????????
        if (requestCode == REQ_CODE_PICK_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                Log.d("???????????????1", String.valueOf(uri));
                String path = getPath(uri);

                uploadFilePath = path;
                Log.d("uri??????(?????????)2", uploadFilePath);

                if (uploadFilePath != null) {
                    UploadImageToServer uploadimagetoserver = new UploadImageToServer();
                    uploadimagetoserver.execute("http://49.247.146.128/chat-upload.php");
                } else {
                    Toast.makeText(MainActivity.this, "???????????? ??????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        }

        //?????????
        if(requestCode == 101  && resultCode == Activity.RESULT_OK){

            uploadFilePath = "/storage/emulated/0/"+userName+".png";
            Log.d("uri??????(?????????)2", uploadFilePath);

            if (uploadFilePath != null) {
                UploadImageToServer uploadimagetoserver = new UploadImageToServer();
                uploadimagetoserver.execute("http://49.247.146.128/chat-upload.php");
            } else {
                Toast.makeText(MainActivity.this, "???????????? ??????????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ?????? ?????? ??????
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // ============================== ????????? ????????? ???????????? ?????? ????????? ========================
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
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("?????????...");
            mProgressDialog.setMessage("???????????? ?????????????????????...");
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
                    Log.i(TAGLOG, "fileName: " + fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    // ????????? ???????????? ????????? ???????????? ?????? ????????? ????????? ????????? ????????????.
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"data\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    imageFileName = userName+getTime;
                    dos.writeBytes( URLEncoder.encode (imageFileName, "utf-8"));
                    dos.writeBytes(lineEnd);

                    // ????????? ??????
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
                                String outputString = "";
                                int i = 0 ;
                                for(;;){
                                    //????????? ????????? ???????????? ??????????????? ?????? ??????
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
                                Log.d("???????????????", outputString);

                                clientdata.setLength(0);
                                clientdata.append(REQ_SENDIMAGE);
                                clientdata.append(SEPARATOR);
                                clientdata.append(userGroup);
                                clientdata.append(SEPARATOR);
                                clientdata.append(userName);
                                clientdata.append(SEPARATOR);
                                clientdata.append("http://49.247.146.128/image/"+imageFileName+".png");
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            output.write(clientdata.toString()+"\r\n");
                                            output.flush();

                                            // ????????? ????????? ?????? ??????
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try{
                                                        JSONObject jsonResponse = new JSONObject(response);
                                                        boolean success = jsonResponse.getBoolean("success");
                                                        if (!success) {
                                                            Toast.makeText(getApplicationContext(), "?????? ????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }catch (Exception e){
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            ChatSaveRequest chatSaveRequest = new ChatSaveRequest(userGroup, userName, "image", "http://49.247.146.128/image/"+imageFileName+".png", responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                            queue.add(chatSaveRequest);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                Toast.makeText(MainActivity.this, "????????? ????????? ??????", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.i(TAGLOG, "[UploadImageToServer] error: " + ex.getMessage(), ex);
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(MainActivity.this, "com.example.myapplication.fileprovider", file));
        startActivityForResult(intent, 101);
    }

}

