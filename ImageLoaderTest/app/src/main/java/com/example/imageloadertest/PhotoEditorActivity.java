package com.example.imageloadertest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

public class PhotoEditorActivity extends AppCompatActivity {
    public static final String FILE_PATH_INTENT_PARAM = "photo_file_path";
    ZoomableDraweeView mPhotoEditorView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        mPhotoEditorView = findViewById(R.id.editor_photo_view);
        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) mPhotoEditorView.getLayoutParams();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.width = displayMetrics.widthPixels;
        Intent intent = getIntent();
        String filePath = intent.getStringExtra(FILE_PATH_INTENT_PARAM);
        mPhotoEditorView.setImageURL(filePath);
    }
}
