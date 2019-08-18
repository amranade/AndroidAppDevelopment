package com.example.imageloadertest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.imageloadertest.data.BitmapCacheManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView mDraweeParent;
    WebImageAdapter mWebImageAdapter;
    GridLayoutManager manager;

    private static final int GRID_COLUMN_COUNT = 3;

    public static final String CAMERA_IMAGE_BUCKET_NAME =
            Environment.getExternalStorageDirectory().toString()
                    + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID =
            getBucketId(CAMERA_IMAGE_BUCKET_NAME);
    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BitmapCacheManager.init(this);
        setContentView(R.layout.activity_main);

        mDraweeParent = findViewById(R.id.drawee_view_parent);
        mWebImageAdapter = new WebImageAdapter(this);
        manager = new GridLayoutManager(this, GRID_COLUMN_COUNT);
        mDraweeParent.setAdapter(mWebImageAdapter);
        mWebImageAdapter.notifyDataSetChanged();
        mDraweeParent.setLayoutManager(manager);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        mWebImageAdapter.setThumbnailSize(width / GRID_COLUMN_COUNT);
        if (requestImagePermission(this)) {
            showFileImage(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showFileImage(this);
//            List<String> cameraImages = getCameraImages(this);
//            for (String name : cameraImages) {
//                Log.d("MyDebug", "Image path: " + name);
//            }
        }
    }

    private void showFileImage(Activity activity) {
        List<String> cameraImages = getCameraImages(activity);
        for (String name : cameraImages) {
            Log.d("MyDebug", "Image path: " + name);
        }
        mWebImageAdapter.setFilepathList(cameraImages);
        mWebImageAdapter.notifyDataSetChanged();
    }

    public static boolean requestImagePermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // do something here
            } else {
                ActivityCompat.requestPermissions(
                        context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        2); //
            }
            return false;
        }
        return true;
    }

    public static List<String> getCameraImages(Activity context) {
        final String[] projection = { MediaStore.Images.Media.DATA };
        String orderBy = MediaStore.Audio.Media.DATE_MODIFIED + " DESC";
        final Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                orderBy);
        ArrayList<String> result = new ArrayList<String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }
}
