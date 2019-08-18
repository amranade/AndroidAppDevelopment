package com.example.imageloadertest.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.LruCache;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BitmapCacheManager {
    private final int BITMAP_MAX_SIZE = 10;
    private static BitmapCacheManager mBitmapCacheManagerInst;
    private LruCache<Integer, BitmapDrawable> mCachedDrawables = new LruCache<>(BITMAP_MAX_SIZE);
    private HashSet<Integer> mBitmapFutures;
    final ExecutorService mExecutorService;
    Context mContext;

    public interface BitmapCacheListener {
        void onBitmapDownloaded(BitmapDrawable bitmapDrawable);
    }

    private BitmapCacheManager() {
        mExecutorService = Executors.newFixedThreadPool(BITMAP_MAX_SIZE);
        mBitmapFutures = new HashSet<>();
    }

    public static void init(Context context) {
        if (mBitmapCacheManagerInst != null) return;
        mBitmapCacheManagerInst = new BitmapCacheManager();
        mBitmapCacheManagerInst.mContext = context;
    }

    public static BitmapCacheManager getInstance() {
        assert (mBitmapCacheManagerInst != null);
        return mBitmapCacheManagerInst;
    }

    public boolean requestFileBitmap(
            final String filepath,
            final int forWidth,
            final BitmapCacheListener bitmapCacheListener) {
        String pathForHash = filepath + '_' + forWidth;
        final int urlHash = pathForHash.hashCode();
        if (mCachedDrawables.get(urlHash) != null) {
            bitmapCacheListener.onBitmapDownloaded(mCachedDrawables.get(urlHash));
            return false;
        }
        if (mBitmapFutures.contains(urlHash)) {
            return true;
        }
        mExecutorService.execute(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                final Bitmap bmp = BitmapFactory.decodeFile(filepath, options);
                final int h = forWidth * bmp.getHeight() / bmp.getWidth();
                final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, forWidth, h, false);
                mContext.getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(scaledBitmap);
                        bitmapCacheListener.onBitmapDownloaded(bitmapDrawable);
                        mBitmapFutures.remove(urlHash);
                    }
                });
            }
        });
        mBitmapFutures.add(urlHash);
        return true;
    }

//    private class ProgressDownload extends AsyncTask<String, Integer, BitmapDrawable> {
//
//        @Override
//        protected BitmapDrawable doInBackground(String... strings) {
//            publishProgress();
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//        }
//    }

//    /**
//     * Returns if an image is in fact requested and will receive a callback
//     */
//    public boolean requestNetworkBitmap(
//            String urlStr,
//            final int forWidth,
//            final BitmapCacheListener bitmapCacheListener) {
//        try {
//            final URL imageUrl = new URL(urlStr);
//            final int urlHash = urlStr.hashCode();
//            if (mCachedDrawables.get(urlHash) != null) {
//                bitmapCacheListener.onBitmapDownloaded(mCachedDrawables.get(urlHash));
//                return false;
//            }
//            if (mBitmapFutures.contains(urlHash)) {
//                return true;
//            }
//
////            new ProgressDownload().execute("hello");
//
//            mExecutorService.execute(new Runnable() {
//                @RequiresApi(api = Build.VERSION_CODES.P)
//                @Override
//                public void run() {
//                    try {
//                        HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
//                        connection.setDoInput(true);
//                        connection.connect();
//                        InputStream input = connection.getInputStream();
//                        final Bitmap bmp = BitmapFactory.decodeStream(input);
//                        Log.e("MyDebug", "downloaded image, hash: " + urlHash +
//                                " width: " + bmp.getWidth() + " height: " + bmp.getHeight());
//                        final int h = forWidth * bmp.getHeight() / bmp.getWidth();
//                        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, forWidth, h, false);
//                        mContext.getMainExecutor().execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                BitmapDrawable bitmapDrawable = new BitmapDrawable(scaledBitmap);
//                                bitmapCacheListener.onBitmapDownloaded(bitmapDrawable);
//                                mBitmapFutures.add(urlHash);
//                            }
//                        });
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//            mBitmapFutures.add(urlHash);

//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                }
//            });
//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
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
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//            });
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
}
