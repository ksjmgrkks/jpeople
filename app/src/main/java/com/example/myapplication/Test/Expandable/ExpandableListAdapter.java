package com.example.myapplication.Test.Expandable;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private List<Item> data;

    public ExpandableListAdapter(List<Item> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        switch (type) {
            case HEADER:
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.cc_list, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD:
                LayoutInflater inflaterChild = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflaterChild.inflate(R.layout.cr_list, parent, false);
                ListChildViewHolder child = new ListChildViewHolder(view);
                return child;
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = data.get(position);
        switch (item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                itemController.refferalItem = item;
                itemController.header_title.setText(item.contents);

                if (item.invisibleChildren == null) {
                    itemController.reply_read.setText(" - 답글 숨기기");
                }
                itemController.reply_read.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.invisibleChildren == null) {
                            item.invisibleChildren = new ArrayList<Item>();
                            int count = 0;
                            int pos = data.indexOf(itemController.refferalItem);
                            while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
                                item.invisibleChildren.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.reply_read.setText("답글 보기");

                        } else {
                            int pos = data.indexOf(itemController.refferalItem);
                            int index = pos + 1;
                            for (Item i : item.invisibleChildren) {
                                data.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                                itemController.reply_read.setText(" - 답글 숨기기");
                            item.invisibleChildren = null;
                        }
                    }
                });
                break;
            case CHILD:
                final ListChildViewHolder childItemController = (ListChildViewHolder) holder;
                childItemController.child_contents.setText(item.contents);

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView header_title;
        public TextView reply_read;
        public Item refferalItem;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            header_title = (TextView) itemView.findViewById(R.id.textView_cc_contents);
            reply_read = (TextView) itemView.findViewById(R.id.textView_cc_reply_read);
        }
    }

    private static class ListChildViewHolder extends RecyclerView.ViewHolder {
        public TextView child_contents;

        public ListChildViewHolder(View itemView) {
            super(itemView);
            child_contents = (TextView) itemView.findViewById(R.id.textView_cr_contents);
        }
    }

    public static class Item {
        public int type;
        public String writerProfile;
        public String writerName;
        public String writerGroup;
        public String writeTime;
        public String replyCount;
        public String contents;
        public List<Item> invisibleChildren;

        public Item(int type, String contents) {
            this.type = type;
            this.contents = contents;
        }
    }
}

