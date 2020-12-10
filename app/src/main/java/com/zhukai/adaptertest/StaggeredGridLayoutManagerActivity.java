package com.zhukai.adaptertest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.zhukai.adapter.VastAdapter;
import com.zhukai.adapter.VastHolder;

import java.util.ArrayList;
import java.util.List;

public class StaggeredGridLayoutManagerActivity extends AppCompatActivity {

    private RecyclerView mListRv;

    private List<String> datas = new ArrayList<>();

    public static final void launch(Activity activity) {
        Intent intent = new Intent(activity, StaggeredGridLayoutManagerActivity.class);
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

        mListRv.setLayoutManager(new StaggeredGridLayoutManager(5, RecyclerView.HORIZONTAL));
        mListRv.setAdapter(vastAdapter);
        View view = new View(this);
        view.setBackgroundColor(Color.parseColor("#eeee34"));
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(100, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
        vastAdapter.addHeaderView(view);
        vastAdapter.addFooterView(view);
    }

    private VastAdapter vastAdapter = new VastAdapter<String>(datas, R.layout.item_list) {
        @Override
        public void onCreateHolder(VastHolder holder) {

        }

        @Override
        public void bindHolder(VastHolder holder, String data, int position) {
            holder.setText(R.id.content_text_tv, data);
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            lp.width = 100 * (position % 2 == 0 ? 1 : 2);
        }
    };
}
