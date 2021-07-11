package com.example.myapplication.Community.Like;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.ArrayList;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.CustomViewHolder> {
    private ArrayList<LikeDictionary> likeArrayList;
    private android.content.Context Context;
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener likeListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position, int clickItem) ;
    }
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.likeListener = listener ;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView clNumber;
        protected TextView clName;
        protected TextView userGroup;
        protected ImageView imageviewProfile;

        public CustomViewHolder(View itemView) { //리사이클러뷰에서 보여지는 부분
            super(itemView);
            this.clName = (TextView) itemView.findViewById(R.id.textView_name);
            this.userGroup = (TextView) itemView.findViewById(R.id.textview_group);
            this.imageviewProfile = (ImageView) itemView.findViewById(R.id.imageView_profile);
        }
    }

    //CustomAdapter 의 생성자
    public LikeAdapter(android.content.Context context, ArrayList<LikeDictionary> list) {
        likeArrayList = list;
        Context = context;
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //뷰홀더 객체를 만들어 주는 곳
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.like_list, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {
        viewholder.clName.setText(likeArrayList.get(position).getUserName());
        viewholder.userGroup.setText(likeArrayList.get(position).getUserGroup());
        String profilePath = "http://49.247.146.128/image/"+likeArrayList.get(position).getUserName()+".png";
        Uri profileUri = Uri.parse(profilePath);
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
        return (null != likeArrayList ? likeArrayList.size() : 0);
    }

}
