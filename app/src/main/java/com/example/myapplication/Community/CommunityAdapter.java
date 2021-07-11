package com.example.myapplication.Community;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myapplication.Community.Like.LikeRequest;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CustomViewHolder> {
    private ArrayList<CommunityDictionary> cwArrayList;
    private android.content.Context Context;
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener cwListener = null ;
    static String likeCount;
    //뷰페이저 관련 변수
    private ArrayList<cwViewPagerDictionary> ViewPagerList;
    cwViewPagerAdapter cwViewPagerAdapter;
    LinearLayout layoutIndicator;
    JSONArray jsonArray;
    String StringFilepathArray;

    public interface OnItemClickListener {
        void onItemClick(View v, int position, int clickItem) ;
    }
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.cwListener = listener ;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView cwNumber;
        protected TextView cwWriter;
        protected TextView cwCreated;
        protected TextView cwTitle;
        protected TextView cwContents;
        protected TextView cwLike;
        protected TextView cwComment;
        protected TextView userGroup;
        protected ImageView imageviewOption;
        protected ImageView imageviewProfile;
        protected ViewPager2 viewpagerCommunity;
        protected Button ButtonLike;
        protected ImageView imageviewComment;
        protected TextView textViewPage;
        protected TextView textViewTotal;
        protected TextView textViewImage;
        protected TextView textViewNumber;
        protected TextView textViewLikeisset;
        protected TextView textViewProfile;


        @SuppressLint("SetTextI18n")
        public CustomViewHolder(View itemView) { //리사이클러뷰에서 보여지는 부분
            super(itemView);
            this.cwNumber = (TextView) itemView.findViewById(R.id.communityNumber);
            this.cwWriter = (TextView) itemView.findViewById(R.id.textView_name);
            this.cwCreated = (TextView) itemView.findViewById(R.id.textView_date);
            this.cwTitle = (TextView) itemView.findViewById(R.id.community_title);
            this.cwContents = (TextView) itemView.findViewById(R.id.community_contents);
            this.cwLike = (TextView) itemView.findViewById(R.id.textView_like_count);
            this.cwComment = (TextView) itemView.findViewById(R.id.textView_comment_count);
            this.userGroup = (TextView) itemView.findViewById(R.id.textView_group);
            this.imageviewOption = (ImageView) itemView.findViewById(R.id.imageview_option);
            this.imageviewProfile = (ImageView) itemView.findViewById(R.id.imageView_profile);
            this.viewpagerCommunity = (ViewPager2) itemView.findViewById(R.id.viewpager_community);
            this.ButtonLike = (Button) itemView.findViewById(R.id.button_like);
            this.imageviewComment = (ImageView) itemView.findViewById(R.id.imageView_comment);
            layoutIndicator = (LinearLayout) itemView.findViewById(R.id.layoutIndicators);
            this.textViewPage = (TextView) itemView.findViewById(R.id.textView_page);
            this.textViewTotal = (TextView) itemView.findViewById(R.id.textView_total);
            this.textViewImage = (TextView) itemView.findViewById(R.id.textView_cwImage);
            this.textViewNumber = (TextView) itemView.findViewById(R.id.communityNumber);
            this.textViewLikeisset = (TextView) itemView.findViewById(R.id.textView_likeisset);
            this.textViewProfile = (TextView) itemView.findViewById(R.id.textView_profile);

            viewpagerCommunity.setOffscreenPageLimit(1);
            ViewPagerList = new ArrayList<>(); // 리사이클러뷰 어레이리스트
            cwViewPagerAdapter = new cwViewPagerAdapter(Context, ViewPagerList);
            viewpagerCommunity.setAdapter(cwViewPagerAdapter);

            // 옵션 클릭 이벤트 처리.
            imageviewOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommunityActivity.StringCwNumber = cwNumber.getText().toString();
                    CommunityActivity.cwWriter = cwWriter.getText().toString();
                    CommunityActivity.cwWriterGroup = userGroup.getText().toString();
                    CommunityActivity.cwTitle = cwTitle.getText().toString();
                    CommunityActivity.cwContents = cwContents.getText().toString();
                    CommunityActivity.cwCreated = cwCreated.getText().toString();
                    CommunityActivity.cwImage = textViewImage.getText().toString();
                    CommunityActivity.cwNumber = textViewNumber.getText().toString();
                    CommunityActivity.cwLike = cwLike.getText().toString();
                    CommunityActivity.cwComment = cwComment.getText().toString();
                    CommunityActivity.cwProfile = textViewProfile.getText().toString();
                    CommunityActivity.cwLikeIsset = textViewLikeisset.getText().toString();
                    int position = getAdapterPosition() ;
                    if (position != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (cwListener != null) {
                            cwListener.onItemClick(v, position, 0);
                        }
                    }
                }
            });

            // 글 내용 더보기 클릭 이벤트 처리.
            cwContents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition() ;
                    if (position != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (cwListener != null) {
                            cwListener.onItemClick(v, position, 1) ;

                        }
                    }
                }
            });

            // 좋아요 클릭 이벤트 처리.
            ButtonLike.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                Log.d("잘왔니", response);
                                //todo: likeCount가 1일경우 삭제하면 되고, likeCount가 0일 경우 추가하면 된다.
                                v.setSelected(!v.isSelected()); // 좋아요 버튼 선택여부 반전시키기
                                if(!response.isEmpty()){
                                    //행이 존재할 때
                                    int idx = cwLike.getText().toString().indexOf("명");
                                    String Count = cwLike.getText().toString().substring(0, idx);
                                    int count = Integer.parseInt(Count);
                                    if(count == 1){
//                                        cwLike.setText(""+ --count +"");
                                        cwLike.setText("0");
                                    }else{
                                        cwLike.setText(--count+"명이 좋아합니다");
                                    }

                                }else{
                                    //행이 존재하지 않을 때
                                    if(cwLike.getText().toString().equals("0")){
                                        int count = Integer.parseInt(cwLike.getText().toString());
                                        cwLike.setText(++count+"명이 좋아합니다");
                                    }else{
                                        int idx = cwLike.getText().toString().indexOf("명");
                                        String Count = cwLike.getText().toString().substring(0, idx);
                                        int count = Integer.parseInt(Count);
                                        cwLike.setText(++count+"명이 좋아합니다");
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    };
                    LikeRequest likeRequest = new LikeRequest("0", cwNumber.getText().toString(), CommunityActivity.userName, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(Context);
                    queue.add(likeRequest);

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (cwListener != null) {
                            cwListener.onItemClick(v, position, 2);

                        }
                    }
                }
            });

            // 좋아요 명단 클릭 이벤트 처리.
            cwLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(cwLike.getText().toString().equals("0")){
                           likeCount = "0";
                    }else{
                           likeCount = "1";
                    }
                    int position = getAdapterPosition() ;
                    CommunityActivity.StringCwNumber = cwNumber.getText().toString();
                    if (position != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (cwListener != null) {
                            cwListener.onItemClick(v, position, 3) ;
                        }
                    }
                }
            });

            // 댓글 클릭 이벤트 처리.
            imageviewComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition() ;
                    CommunityActivity.StringCwNumber = cwNumber.getText().toString();
                    CommunityActivity.cwWriter = cwWriter.getText().toString();
                    CommunityActivity.cwWriterGroup = userGroup.getText().toString();
                    CommunityActivity.cwTitle = cwTitle.getText().toString();
                    CommunityActivity.cwContents = cwContents.getText().toString();
                    if (position != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (cwListener != null) {
                            cwListener.onItemClick(v, position, 4) ;
                        }
                    }
                }
            });
        }
    }

    //CustomAdapter 의 생성자
    public CommunityAdapter(android.content.Context context, ArrayList<CommunityDictionary> list) {
        cwArrayList = list;
        Context = context;
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //뷰홀더 객체를 만들어 주는 곳
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.community_list, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {
        //생성된 뷰홀더에 데이터를 묶어주는(바인딩) 메소드
        //스크롤을 해서 데이터 바인딩이 필요할 때마다 호출되는 함수다.
        //데이터를 추가할 때는 onCreateViewHolder 와 onBindViewHolder 둘다 호출되고,
        //스크롤을 해서 뷰홀더를 재사용할때는 onBindViewHolder 만 호출된다.
        viewholder.cwNumber.setText(cwArrayList.get(position).getCwNumber());
        viewholder.cwWriter.setText(cwArrayList.get(position).getCwWriter());
        viewholder.cwCreated.setText(cwArrayList.get(position).getCwCreated());
        viewholder.cwTitle.setText(cwArrayList.get(position).getCwTitle());
        viewholder.cwContents.setText(cwArrayList.get(position).getCwContents());
        viewholder.cwLike.setText(cwArrayList.get(position).getCwLike());
        viewholder.cwComment.setText(cwArrayList.get(position).getCwComment());
        viewholder.userGroup.setText(cwArrayList.get(position).getUserGroup());
        viewholder.textViewImage.setText(cwArrayList.get(position).getCwImage());
        viewholder.textViewLikeisset.setText(cwArrayList.get(position).getCwLikeIsset());
        viewholder.textViewProfile.setText(cwArrayList.get(position).getCwProfile());
        StringFilepathArray = cwArrayList.get(position).getCwImage();
        if (!cwArrayList.get(position).getTotalPage().equals("1")) {
            viewholder.textViewTotal.setText(cwArrayList.get(position).getTotalPage());
        }
        viewholder.viewpagerCommunity.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d("리스트사이즈", ViewPagerList.size()+"");
                viewholder.textViewPage.setText(position+1+"/  ");
            }
        });

        try {
            jsonArray = new JSONArray(StringFilepathArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonArray == null){
                    viewholder.textViewPage.setVisibility(View.GONE);
                    viewholder.viewpagerCommunity.setVisibility(View.GONE);
        }else if (jsonArray.length() == 1){
            viewholder.textViewPage.setVisibility(View.GONE);
            try {
                ViewPagerList.add(new cwViewPagerDictionary(jsonArray.getString(0)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cwViewPagerAdapter.notifyItemInserted(0);
            jsonArray = null;
        }else{
            for (int i = 0; i < jsonArray.length() ; i++){
                try {
                      Log.d("제이슨어레이", jsonArray.getString(i));
                      ViewPagerList.add(new cwViewPagerDictionary(jsonArray.getString(i)));
                      cwViewPagerAdapter.notifyItemInserted(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            jsonArray = null;
        }

        int count = Integer.parseInt(cwArrayList.get(position).getCwLikeIsset());
        if(count == 1){
            //좋아요가 있을 때
            viewholder.ButtonLike.setSelected(true);
        }else{
            viewholder.ButtonLike.setSelected(false);
        }

        Uri profileUri = Uri.parse(cwArrayList.get(position).getCwProfile());
        Glide.with(viewholder.itemView.getContext())
                .load(profileUri)
//                .placeholder(R.drawable.firstprofile)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .circleCrop()
                .into(viewholder.imageviewProfile);
    }

    //데이터의 길이를 측정하는 메소드
    @Override
    public int getItemCount() {
        return (null != cwArrayList ? cwArrayList.size() : 0);
    }
}
