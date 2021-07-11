package com.example.myapplication.MyPage.Manage.OfferingConfirm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class OfferingConfirmCustomAdapter extends RecyclerView.Adapter<OfferingConfirmCustomAdapter.CustomViewHolder> {
    private ArrayList<OfferingConfirmDictionary> offeringArrayList;
    private android.content.Context Context;
    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView offeringSort;
        protected TextView offeringDate;
        protected TextView offeringMoney;
        protected TextView offeringContents;

        public CustomViewHolder(View view) { //리사이클러뷰에서 보여지는 부분
            super(view);
            this.offeringSort = (TextView) view.findViewById(R.id.offering_sort);
            this.offeringDate = (TextView) view.findViewById(R.id.offering_date);
            this.offeringMoney = (TextView) view.findViewById(R.id.offering_money);
            this.offeringContents = (TextView) view.findViewById(R.id.offering_contents);
        }
    }

    //CustomAdapter 의 생성자
    public OfferingConfirmCustomAdapter(android.content.Context context, ArrayList<OfferingConfirmDictionary> list) {
        offeringArrayList = list;
        Context = context;
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //뷰홀더 객체를 만들어 주는 곳
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.offering_list, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {
        //생성된 뷰홀더에 데이터를 묶어주는(바인딩) 메소드
        //스크롤을 해서 데이터 바인딩이 필요할 때마다 호출되는 함수다.
        //데이터를 추가할 때는 onCreateViewHolder 와 onBindViewHolder 둘다 호출되고,
        //스크롤을 해서 뷰홀더를 재사용할때는 onBindViewHolder 만 호출된다.
        viewholder.offeringDate.setText(offeringArrayList.get(position).getOfferingDate());
        viewholder.offeringSort.setText(offeringArrayList.get(position).getOfferingSort());
        viewholder.offeringMoney.setText(offeringArrayList.get(position).getOfferingMoney());
        viewholder.offeringContents.setText(offeringArrayList.get(position).getOfferingContents());
    }

    //데이터의 길이를 측정하는 메소드
    @Override
    public int getItemCount() {
        return (null != offeringArrayList ? offeringArrayList.size() : 0);
    }

}
