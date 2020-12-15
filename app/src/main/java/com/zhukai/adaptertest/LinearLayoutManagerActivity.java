package com.zhukai.adaptertest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.zhukai.adapter.VastAdapter;
import com.zhukai.adapter.VastHolder;

import java.util.ArrayList;
import java.util.List;

public class LinearLayoutManagerActivity extends AppCompatActivity {

    private RecyclerView mListRv;

    private List<String> datas = new ArrayList<>();

    public static final void launch(Activity activity) {
        Intent intent = new Intent(activity, LinearLayoutManagerActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_layout_manager);
        mListRv = findViewById(R.id.list_rv);


//        for (int i = 0; i < 100; i++) {
//            datas.add("linear" + i);
//        }

        mListRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        VastAdapter vastAdapter = new VastAdapter<String>(datas, R.layout.item_list) {
            @Override
            public void onCreateHolder(VastHolder holder) {

            }

            @Override
            public void bindHolder(VastHolder holder, String data, int position) {
                holder.setText(R.id.content_text_tv, data);
                holder.itemView.getLayoutParams().width = 100;
            }

            @Override
            public int getItemViewIndex(int position) {
                return super.getItemViewIndex(position);
            }
        };
        mListRv.setAdapter(vastAdapter);
        View view = new View(this);
        view.setBackgroundColor(Color.parseColor("#eeee34"));
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(100, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
//        vastAdapter.addHeaderView(view);
//        vastAdapter.addFooterView(view);

        vastAdapter.setVacancyView(new VacancyHintView.Builder(this).setText("没有内容").setTextColorRes(R.color.colorPrimary).setTextSize(20).build());
    }

}
