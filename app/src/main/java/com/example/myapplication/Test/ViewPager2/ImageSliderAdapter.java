package com.example.myapplication.Test.ViewPager2;

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

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<SliderDictionary> sliderArrayList;

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener vpListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position, int clickItem) ;
    }
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.vpListener = listener ;
    }

    public ImageSliderAdapter(Context context, ArrayList<SliderDictionary> list) {
        this.context = context;
        this.sliderArrayList = list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false);
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
        private ImageView imageViewCancel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageSlider);
            imageViewCancel = itemView.findViewById(R.id.imageView_cancel);

            // X 아이콘을 누르면 뷰페이저 아이템을 삭제한다.
            imageViewCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition() ;
                    if (position != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (vpListener != null) {
                            vpListener.onItemClick(v, position, 0);
                        }
                    }
                }
            });
        }

        public void bindSliderImage(Uri imageUri) {
            Glide.with(context)
                    .load(imageUri)
                    .into(mImageView);
        }
    }
}
