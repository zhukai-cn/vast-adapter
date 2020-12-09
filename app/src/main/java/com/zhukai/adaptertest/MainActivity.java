package com.zhukai.adaptertest;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.llm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayoutManagerActivity.launch(MainActivity.this);
            }
        });
        findViewById(R.id.glm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GridLayoutManagerActivity.launch(MainActivity.this);
            }
        });
        findViewById(R.id.sglm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaggeredGridLayoutManagerActivity.launch(MainActivity.this);
            }
        });
    }

}
