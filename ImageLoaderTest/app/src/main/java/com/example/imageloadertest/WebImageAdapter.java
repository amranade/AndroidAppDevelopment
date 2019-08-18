package com.example.imageloadertest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class WebImageAdapter extends RecyclerView.Adapter<WebImageAdapter.ViewHolder> {

    private int mThumbnailSize;
    private List<String> mfilePaths;
    View.OnClickListener mTileClickListener;
    Context mContext;
    public WebImageAdapter(Context context) {
        mfilePaths = new ArrayList<>();
        mTileClickListener = createTileClickListsner();
        mContext = context;
    }

    private View.OnClickListener createTileClickListsner() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fileIdx = (int) v.getTag();
                Log.d("MyDebug", "URI of file clicked: " + mfilePaths.get(fileIdx));
                Intent intent = new Intent(mContext, PhotoEditorActivity.class);
                intent.putExtra(PhotoEditorActivity.FILE_PATH_INTENT_PARAM, mfilePaths.get(fileIdx));
                mContext.startActivity(intent);
            }
        };
    }

    public void setFilepathList(List<String> filePaths) {
        mfilePaths = filePaths;
    }

    public void setThumbnailSize(int mThumbnailSize) {
        this.mThumbnailSize = mThumbnailSize;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private DraweeView draweeView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            draweeView = (DraweeView) itemView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        DraweeView draweeView = new DraweeView(viewGroup.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                mThumbnailSize,
                mThumbnailSize);
        draweeView.setLayoutParams(layoutParams);
        draweeView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        draweeView.setOnClickListener(mTileClickListener);
        return new ViewHolder(draweeView);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String filepath = mfilePaths.get(i);
        viewHolder.draweeView.setImageURL(filepath);
        viewHolder.draweeView.setTag(i);
    }

    @Override
    public int getItemCount() {
        return mfilePaths.size();
    }
}
