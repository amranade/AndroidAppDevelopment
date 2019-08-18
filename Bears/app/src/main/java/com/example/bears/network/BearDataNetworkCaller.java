package com.example.bears.network;

import android.os.Handler;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BearDataNetworkCaller {

    private static BearDataNetworkCaller bearDataNetworkCallerInst;

    private HashSet<Integer> mOngoingCalls;
    private ExecutorService mExecutorService;
    private Handler mUIHandler;

    public interface ResponseListener {
        void onResponse(String response);
    }

    private BearDataNetworkCaller() {
        mExecutorService = Executors.newCachedThreadPool();
        mOngoingCalls = new HashSet<>();
    }

    public static BearDataNetworkCaller getInstance(Handler uiHandler) {
        if (bearDataNetworkCallerInst == null) {
            bearDataNetworkCallerInst = new BearDataNetworkCaller();
            bearDataNetworkCallerInst.mUIHandler = uiHandler;
        }
        return bearDataNetworkCallerInst;
    }

    @Nullable
    public void makeNetWorkCall(
            final String uri,
            final String callType,
            final String queryStr,
            final ResponseListener responseListener) {
        String callHash = uri + "_" + callType;
        final int hash = callHash.hashCode();
        if (mOngoingCalls.contains(hash)) {
            return;
        }
        mOngoingCalls.add(hash);
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    URL getAllBears = new URL(uri);
                    HttpURLConnection urlConnection = (HttpURLConnection) getAllBears.openConnection();
                    urlConnection.setRequestMethod(callType);
//                    urlConnection.addRequestProperty();
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String out;
                        while ((out = bufferedReader.readLine()) != null) {
                            stringBuilder.append(out);
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mOngoingCalls.remove(hash);
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        responseListener.onResponse(stringBuilder.toString());
                    }
                });
            }
        });
    }
}
