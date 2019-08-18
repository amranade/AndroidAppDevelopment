package com.example.bears.network;

import android.os.Handler;
import android.support.annotation.Nullable;

import com.example.bears.data.BearData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BearDataProvider {
    private @Nullable
    BearDataListsner mBearDataListsner;
    BearDataNetworkCaller mBearDataNetworkCaller;
    List<BearData> mBearsData;
    Handler mUIHandler;

    // make singleton
    public BearDataProvider(Handler handler) {
        mUIHandler = handler;
        mBearDataNetworkCaller = BearDataNetworkCaller.getInstance(mUIHandler);
        getFullData();
    }

    void getFullData() {
        mBearsData = new ArrayList<>();
        mBearDataNetworkCaller.makeNetWorkCall(
            "http://192.168.29.34:8080/api/bears",
            "GET",
            new BearDataNetworkCaller.ResponseListener() {
                @Override
                public void onResponse(final String response) {
                    mBearsData = getBearsData(response);
                    mBearDataListsner.onBearsDataUpdated();
                }
            });
    }

    private static List<BearData> getBearsData(String jsonStr) {
        List<BearData> bearsData = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                BearData bearData = new BearData();
                bearData.name = jsonObject.getString("name");
                bearData.id = jsonObject.getString("_id");
                bearsData.add(bearData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bearsData;
    }

//    private void fetchBearsData() {
//        fetchBearsData(new ResponseListener() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    mBearsData = new ArrayList<>();
//                    JSONArray jsonArray = new JSONArray(response);
//                    for (int i=0;i<jsonArray.length();i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        BearData bearData = new BearData();
//                        bearData.name = jsonObject.getString("name");
//                        bearData.id = jsonObject.getString("_id");
//                        mBearsData.add(bearData);
//                    }
//                    mBearDataListsner.onBearsDataUpdated();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    private void fetchBearsData(final ResponseListener responseListener) {
//        try {
//            URL getAllBears = new URL("http://192.168.29.34:8080/api/bears");
//            HttpURLConnection urlConnection = (HttpURLConnection) getAllBears.openConnection();
////            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            final StringBuilder stringBuilder = new StringBuilder();
//            String out;
//            while ((out = bufferedReader.readLine()) != null) {
//                stringBuilder.append(out);
//            }
//            mUIHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    responseListener.onResponse(stringBuilder.toString());
//                }
//            });
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    Runnable getDeleteBearRunnable(final String id) {
//        return new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL getBearUrl = new URL("http://192.168.29.34:8080/api/bears/" + id);
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) getBearUrl.openConnection();
//                    httpURLConnection.setRequestMethod("DELETE");
//                    httpURLConnection.connect();
//                    httpURLConnection.getResponseCode();
//                    fetchBearsData();
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//    }

    public BearData getBear(int i) {
        return mBearsData.get(i);
//        return null;
    }

    public int getCount() {
        return mBearsData.size();
    }

    public void deleteBear(String id) {
        mBearDataNetworkCaller.makeNetWorkCall(
                "http://192.168.29.34:8080/api/bears/" + id,
                "DELETE",
                new BearDataNetworkCaller.ResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        getFullData();
                    }
                });
    }

    public void updateBearName(String id, String newName) {

    }

    public void addBear(String name) {

    }

    public void setmBearDataListsner(BearDataListsner bearDataListsner) {
        mBearDataListsner = bearDataListsner;
    }
}
