package com.example.imageloadertest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.example.imageloadertest.data.BitmapCacheManager;

public class DraweeView extends android.support.v7.widget.AppCompatImageView {
    public final Drawable mDummyDrawable;
    public final BitmapCacheManager mBitmapCacheManager;

    public DraweeView(Context context) {
        this(context, null);
    }

    @SuppressLint("NewApi")
    public DraweeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDummyDrawable = context.getDrawable(android.R.drawable.spinner_background);
        mBitmapCacheManager = BitmapCacheManager.getInstance();
        showDummyImage();
    }

//    @SuppressLint("NewApi")
//    public DraweeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }

    @SuppressLint("NewApi")
    public void setImageURL(final String imageUrl) {
//        int urlHash = imageUrl.toString().hashCode();
//        RecyclerView parent = (RecyclerView) getParent();
//        BitmapDrawable drawable;
        boolean requested = mBitmapCacheManager.requestFileBitmap(
                imageUrl,
                getLayoutParams().width,
                new BitmapCacheManager.BitmapCacheListener() {
                    @Override
                    public void onBitmapDownloaded(BitmapDrawable bitmapDrawable) {
                        onBitmapUpdated(bitmapDrawable);
                    }
                });
        if (requested) {
            // Wait for actual bitmap
            showDummyImage();
        }
//        if ((drawable = mCachedDrawables.get(urlHash)) != null) {
//            onBitmapUpdated(drawable);
//            return;
//        }
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    post(new Runnable() {
//                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                        @Override
//                        public void run() {
//                            showDummyImage();
//                        }
//                    });
//                    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
//                    connection.setDoInput(true);
//                    connection.connect();
//                    InputStream input = connection.getInputStream();
//                    final Bitmap bmp = BitmapFactory.decodeStream(input);
//                    final int urlHash = imageUrl.toString().hashCode();
//                    Log.e("MyDebug", "downloaded image, hash: " + urlHash +
//                            " width: " + bmp.getWidth() + " height: " + bmp.getHeight());
//                    int w = getLayoutParams().width;
//                    final int h = w * bmp.getHeight() / bmp.getWidth();
//                    final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, w, h, false);
//                    post(new Runnable() {
//                        @Override
//                        public void run() {
//                            getLayoutParams().height = h;
//                            BitmapDrawable bitmapDrawable = new BitmapDrawable(scaledBitmap);
//                            mCachedDrawables.put(urlHash, bitmapDrawable);
//                            onBitmapUpdated(bitmapDrawable);
//                        }
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showDummyImage() {
        setImageDrawable(mDummyDrawable);
    }

    private void onBitmapUpdated(BitmapDrawable drawable) {
        if (getDrawable() == drawable) return;
//        getLayoutParams().height = 300; // drawable.getBitmap().getHeight();
        setImageDrawable(drawable);
    }
}
