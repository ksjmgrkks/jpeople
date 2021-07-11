package com.example.myapplication.MyPage.Note;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class NoteCustomAdapter extends RecyclerView.Adapter<NoteCustomAdapter.CustomViewHolder> {
    private ArrayList<NoteDictionary> noteArrayList;
    private android.content.Context Context;
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView noteTitle;
        protected TextView noteDate;
        protected TextView noteContents;
        protected TextView noteNumber;
        protected ImageView imageviewOption;

        public CustomViewHolder(View itemView) { //리사이클러뷰에서 보여지는 부분
            super(itemView);
            this.noteTitle = (TextView) itemView.findViewById(R.id.note_title);
            this.noteDate = (TextView) itemView.findViewById(R.id.note_date);
            this.noteContents = (TextView) itemView.findViewById(R.id.note_contents);
            this.noteNumber = (TextView) itemView.findViewById(R.id.noteNumber);
            this.imageviewOption = (ImageView) itemView.findViewById(R.id.imageview_option);

            // 아이템 클릭 이벤트 처리.
            imageviewOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition() ;
                    if (position != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, position) ;
                        }
                    }
                }
            });
        }
    }

    //CustomAdapter 의 생성자
    public NoteCustomAdapter(android.content.Context context, ArrayList<NoteDictionary> list) {
        noteArrayList = list;
        Context = context;
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //뷰홀더 객체를 만들어 주는 곳
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_list, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {
        //생성된 뷰홀더에 데이터를 묶어주는(바인딩) 메소드
        //스크롤을 해서 데이터 바인딩이 필요할 때마다 호출되는 함수다.
        //데이터를 추가할 때는 onCreateViewHolder 와 onBindViewHolder 둘다 호출되고,
        //스크롤을 해서 뷰홀더를 재사용할때는 onBindViewHolder 만 호출된다.

        viewholder.noteTitle.setText(noteArrayList.get(position).getNoteTitle());
        viewholder.noteDate.setText(noteArrayList.get(position).getNoteDate());
        viewholder.noteContents.setText(noteArrayList.get(position).getNoteContents());
        viewholder.noteNumber.setText(noteArrayList.get(position).getNoteNumber());
    }

    //데이터의 길이를 측정하는 메소드
    @Override
    public int getItemCount() {
        return (null != noteArrayList ? noteArrayList.size() : 0);
    }

}