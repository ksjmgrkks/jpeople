package com.example.myapplication.MyPage.Offering;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PayActivity extends AppCompatActivity {

    // Request 작업을 할 Queue
    static RequestQueue requestQueue;

    // 결제 정보를 받을 변수
    static String offeringName; // 상품 이름
    static String offeringMoney; // 상품 가격

    // 웹 뷰
    WebView webView;

    // json 파싱
    Gson gson;

    // 커스텀 웹 뷰 클라이언트
    MyWebViewClient myWebViewClient;

    // 결제 고유 번호
    String tidPin;

    // 결제 요청 토큰
    String pgToken;

    //유지 이름과 헌금 내용
    String userName;
    String offeringContents;

    // 기본 생성자
    // - Activity는 기본 생성자가 없으면 Manifest에서 사용하지 못함.
    // - 만약 생성자를 오버라이딩 했다면 기본 생성자를 작성해 둘것!
    public PayActivity() {

    }

    // 상품 이름과 가격을 초기화할 생성자
    public PayActivity(String offeringName, String offeringMoney) {
        PayActivity.offeringName = offeringName;
        PayActivity.offeringMoney = offeringMoney;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        setTitle("헌금 드리기");
        userName = getIntent().getStringExtra("userName");
        offeringContents = getIntent().getStringExtra("offeringContents");

        // 초기화
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        myWebViewClient = new MyWebViewClient();
        webView = findViewById(R.id.webView);
        gson = new Gson();

        // 웹 뷰 설정
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(myWebViewClient);

        // 실행 시 바로 결제 Http 통신 실행
        requestQueue.add(myWebViewClient.readyRequest);
    }

    public class MyWebViewClient extends WebViewClient {

        // 에러 - 통신을 받을 Response 변수
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "잔액이 부족합니다.\n카카오페이 잔액을 확인해주세요.",Toast.LENGTH_LONG).show();
                Log.e("Debug", "Error : " + error);
                finish();
            }
        };

        // 결제 준비 단계 - 통신을 받을 Response 변수
        Response.Listener<String> readyResponse = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Debug", response);
                // 결제가 성공 했다면 돌려받는 JSON객체를 파싱함.
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(response);

                // get("받을 Key")로 Json 데이터를 받음
                // - 결제 요청에 필요한 next_redirect_mobile_url, tid를 파싱
                String url = element.getAsJsonObject().get("next_redirect_mobile_url").getAsString();
                String tid = element.getAsJsonObject().get("tid").getAsString();
                Log.e("Debug", "url : " + url);
                Log.e("Debug", "tid : " + tid);

                webView.loadUrl(url);
                tidPin = tid;
            }
        };

        // 결제 준비 단계 - 통신을 넘겨줄 Request 변수
        StringRequest readyRequest = new StringRequest(Request.Method.POST, "https://kapi.kakao.com/v1/payment/ready", readyResponse, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("Debug", "name : " + offeringName);
                Log.e("Debug", "price : " + offeringMoney);

                Map<String, String> params = new HashMap<>();
                params.put("cid", "TC0ONETIME"); // 가맹점 코드
                params.put("partner_order_id", "1000"); // 가맹점 주문 번호
                params.put("partner_user_id", "제이피플"); // 가맹점 회원 아이디
                params.put("item_name", offeringName); // 상품 이름
                params.put("quantity", "1"); // 상품 수량
                params.put("total_amount", offeringMoney); // 상품 총액
                params.put("tax_free_amount", "0"); // 상품 비과세
                params.put("approval_url", "http://49.247.146.128/kakaopay/success.php"); // 결제 성공시 돌려 받을 url 주소
                params.put("cancel_url", "http://49.247.146.128/kakaopay/cancel.php"); // 결제 취소시 돌려 받을 url 주소
                params.put("fail_url", "http://49.247.146.128/kakaopay/fail.php"); // 결제 실패시 돌려 받을 url 주소
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "KakaoAK " + "3809a2c2308b636e35b84d542f236b43");
                return headers;
            }
        };

        // 결제 요청 단계 - 통신을 받을 Response 변수
        Response.Listener<String> approvalResponse = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Debug", response);
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String created = formatNow.format(date);

                DecimalFormat moneyFormatter = new DecimalFormat("###,###,###");
                int integer = Integer.parseInt(offeringMoney);
                String money = moneyFormatter.format(integer);

                // 헌금 내역을 DB에 저장
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                Toast.makeText(getApplicationContext(), "정상적으로 헌금을 드렸습니다.",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "'회원정보 관리' → '헌금 내역 확인'에서 헌금 내역을 확인할 수 있습니다.",Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "헌금 내역 저장에 실패하였습니다. 관리자에게 문의해주세요.",Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                OfferingCreateRequest offeringCreateRequest = new OfferingCreateRequest(userName, offeringName, money, created, offeringContents, responseListener);
                RequestQueue queue = Volley.newRequestQueue(PayActivity.this);
                queue.add(offeringCreateRequest);

                finish();
            }
        };

        // 결제 요청 단계 - 통신을 넘겨줄 Request 변수
        StringRequest approvalRequest = new StringRequest(Request.Method.POST, "https://kapi.kakao.com/v1/payment/approve", approvalResponse, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cid", "TC0ONETIME");
                params.put("tid", tidPin);
                params.put("partner_order_id", "1000");
                params.put("partner_user_id", "제이피플");
                params.put("pg_token", pgToken);
                params.put("total_amount", offeringMoney);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "KakaoAK " + "3809a2c2308b636e35b84d542f236b43");
                return headers;
            }
        };

        // URL 변경시 발생 이벤트
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.e("Debug", "url" + url);
            if (url != null && url.contains("pg_token=")) {
                String pg_Token = url.substring(url.indexOf("pg_token=") + 9);
                pgToken = pg_Token;

                requestQueue.add(approvalRequest);

            } else if (url != null && url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            view.loadUrl(url);
            return false;
        }
    }
}