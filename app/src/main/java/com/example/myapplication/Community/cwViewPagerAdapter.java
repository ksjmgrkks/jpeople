package com.example.myapplication.Community;

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

public class cwViewPagerAdapter extends RecyclerView.Adapter<cwViewPagerAdapter.CustomViewHolder> {
    private Context context;
    private ArrayList<cwViewPagerDictionary> cwViewPagerDictionaryArrayList;


    public cwViewPagerAdapter(Context context, ArrayList<cwViewPagerDictionary> list) {
        this.context = context;
        this.cwViewPagerDictionaryArrayList = list;
    }
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cw_item_slider, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.bindSliderImage(cwViewPagerDictionaryArrayList.get(position).getImageUri());
    }

    @Override
    public int getItemCount() {
        return (null != cwViewPagerDictionaryArrayList ? cwViewPagerDictionaryArrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageSlider);
        }

        public void bindSliderImage(String imageUriString) {
            Uri imageUri = Uri.parse(imageUriString);
            Glide.with(context)
                    .load(imageUri)
                    .into(mImageView);
        }
    }
}
