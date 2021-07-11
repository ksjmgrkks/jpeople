package com.example.myapplication.Test;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Item> myDataList = null;

    MyAdapter(ArrayList<Item> dataList)
    {
        myDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == ViewType.CENTER_JOIN)
        {
            view = inflater.inflate(R.layout.chat_center_item, parent, false);
            return new CenterViewHolder(view);
        }
        else if(viewType == ViewType.LEFT_CHAT)
        {
            view = inflater.inflate(R.layout.chat_left_item, parent, false);
            return new LeftViewHolder(view);
        }
        else if(viewType == ViewType.RIGHT_CHAT)
        {
            view = inflater.inflate(R.layout.chat_right_item, parent, false);
            return new RightViewHolder(view);
        }
        else if(viewType == ViewType.LEFT_IMAGE)
        {
            view = inflater.inflate(R.layout.chat_left_image, parent, false);
            return new LeftImageViewHolder(view);
        } else{
            view = inflater.inflate(R.layout.chat_right_image, parent, false);
            return new RightImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        if(viewHolder instanceof CenterViewHolder)
        {
            ((CenterViewHolder) viewHolder).content.setText(myDataList.get(position).getContent());
        }
        else if(viewHolder instanceof LeftViewHolder)
        {
            ((LeftViewHolder) viewHolder).name.setText(myDataList.get(position).getName());
            ((LeftViewHolder) viewHolder).content.setText(myDataList.get(position).getContent());
            ((LeftViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
            Uri profileUri = Uri.parse(myDataList.get(position).getProfilePath());
            Glide.with(viewHolder.itemView.getContext())
                    .load(profileUri)
                    .circleCrop()
                    .into(((LeftViewHolder) viewHolder).profile);
        }
        else if(viewHolder instanceof RightViewHolder)
        {
            ((RightViewHolder) viewHolder).content.setText(myDataList.get(position).getContent());
            ((RightViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
        }
        else if(viewHolder instanceof LeftImageViewHolder)
        {
            ((LeftImageViewHolder) viewHolder).name.setText(myDataList.get(position).getName());
            Uri imageUri = Uri.parse(myDataList.get(position).getImagePath());
            Glide.with(viewHolder.itemView.getContext())
                    .load(imageUri)
                    .into(((LeftImageViewHolder) viewHolder).content);
            ((LeftImageViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
            Uri profileUri = Uri.parse(myDataList.get(position).getProfilePath());
            Glide.with(viewHolder.itemView.getContext())
                    .load(profileUri)
                    .circleCrop()
                    .into(((LeftImageViewHolder) viewHolder).profile);
        }else{
            Uri imageUri = Uri.parse(myDataList.get(position).getImagePath());
            Glide.with(viewHolder.itemView.getContext())
                    .load(imageUri)
                    .into(((RightImageViewHolder) viewHolder).content);
            ((RightImageViewHolder) viewHolder).time.setText(myDataList.get(position).getTime());
        }
    }

    @Override
    public int getItemCount()
    {
        return myDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return myDataList.get(position).getViewType();
    }

    public class CenterViewHolder extends RecyclerView.ViewHolder{
        TextView content;

        CenterViewHolder(View itemView)
        {
            super(itemView);

            content = itemView.findViewById(R.id.content);
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView name;
        TextView time;
        ImageView profile;

        LeftViewHolder(View itemView)
        {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            profile = itemView.findViewById(R.id.profile);
        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView time;

        RightViewHolder(View itemView)
        {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
        }
    }

    public class LeftImageViewHolder extends RecyclerView.ViewHolder{
        ImageView content;
        TextView name;
        TextView time;
        ImageView profile;

        LeftImageViewHolder(View itemView)
        {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            profile = itemView.findViewById(R.id.profile);
        }
    }

    public class RightImageViewHolder extends RecyclerView.ViewHolder{
        ImageView content;
        TextView time;

        RightImageViewHolder(View itemView)
        {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
        }
    }

}
