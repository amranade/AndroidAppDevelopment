package com.example.notes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.notes.R;
import com.example.notes.view.NotesBasicViewAdapter;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mNotesView;
    private NotesBasicViewAdapter mNotesBasicViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.create_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(RichEditorActivity.launchIntentForNewNote(MainActivity.this));
                startActivityForResult(
                        RichEditorActivity.launchIntentForNewNote(MainActivity.this),
                        RichEditorActivity.NOTES_UPDATE_REQUEST_CODE);
            }
        });
        setNotesView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    private void refreshView() {
        mNotesBasicViewAdapter.notifyDataSetChanged();
        mNotesView.requestLayout();
    }

    private void setNotesView() {
        mNotesView = findViewById(R.id.basic_notes_view);
        mNotesBasicViewAdapter = new NotesBasicViewAdapter(this);
        mNotesBasicViewAdapter.attachToView(this, mNotesView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RichEditorActivity.NOTES_UPDATE_REQUEST_CODE) {
            mNotesBasicViewAdapter.refreshAllNotesData();
            refreshView();
        }
    }
}
