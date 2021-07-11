package com.example.myapplication.Home;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.ArrayList;

public class HomeSliderAdapter extends RecyclerView.Adapter<HomeSliderAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<HomeSliderDictionary> sliderArrayList;

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener vpListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.vpListener = listener ;
    }

    public HomeSliderAdapter(Context context, ArrayList<HomeSliderDictionary> list) {
        this.context = context;
        this.sliderArrayList = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_slider, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindSliderImage(sliderArrayList.get(position).getImageUri());
    }

    @Override
    public int getItemCount() {
        return (null != sliderArrayList ? sliderArrayList.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageSlider);

            // 이미지를 클릭하면 onItemClick 메소드를 실행함.
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition() ;
                    if (position != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (vpListener != null) {
                            HomeActivity.pageNumber = sliderArrayList.get(position).getSlidePage();
                            vpListener.onItemClick(v, position);
                        }
                    }
                }
            });
        }

        public void bindSliderImage(String imageUri) {
            Glide.with(context)
                    .load(Uri.parse(imageUri))
                    .into(mImageView);
        }
    }
}
