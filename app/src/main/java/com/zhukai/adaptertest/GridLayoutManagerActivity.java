package com.zhukai.adaptertest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhukai.adapter.VastAdapter;
import com.zhukai.adapter.VastHolder;

import java.util.ArrayList;
import java.util.List;

public class GridLayoutManagerActivity extends AppCompatActivity {

    private RecyclerView mListRv;

    private List<String> datas = new ArrayList<>();
    private View headerOrFooterView;

    public static final void launch(Activity activity) {
        Intent intent = new Intent(activity, GridLayoutManagerActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout_manager);
        mListRv = findViewById(R.id.list_rv);


        for (int i = 0; i < 100; i++) {
            datas.add("GradItem" + i);
        }

        mListRv.setLayoutManager(new GridLayoutManager(this, 5));
        mListRv.setAdapter(vastAdapter);
        headerOrFooterView = new View(this);
        headerOrFooterView.setBackgroundColor(Color.parseColor("#eeee34"));
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        headerOrFooterView.setLayoutParams(lp);
        vastAdapter.addHeaderView(headerOrFooterView);
        vastAdapter.addFooterView(headerOrFooterView);
        vastAdapter.setOnItemClickListener(new VastAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {

            }
        });
        vastAdapter.setOnItemLongClickListener(new VastAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });
        vastAdapter.setOnPreloadListener(new VastAdapter.OnPreloadListener() {
            @Override
            public void onPreload() {
                
            }
        });
    }

    private VastAdapter vastAdapter = new VastAdapter<String>(datas, R.layout.item_list) {
        @Override
        public void onCreateHolder(VastHolder holder) {

        }

        @Override
        public void bindHolder(VastHolder holder, String data, int position) {
            holder.setText(R.id.content_text_tv, data);
        }

        @Override
        public int getItemViewIndex(int position) {
            return 0;
        }
    };
}
