package com.example.bears;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.bears.network.BearDataProvider;
import com.example.bears.view.BearsAdapter;

public class MainActivity extends AppCompatActivity {

    RecyclerView mBearView;
    BearsAdapter mBearsAdapter;
    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBearView = findViewById(R.id.bears_view);
        mBearsAdapter = new BearsAdapter(this, new BearDataProvider(new Handler()));
        mLayoutManager = new LinearLayoutManager(this);
        mBearView.setAdapter(mBearsAdapter);
        mBearView.setLayoutManager(mLayoutManager);
        mBearsAdapter.notifyDataSetChanged();
    }
}
