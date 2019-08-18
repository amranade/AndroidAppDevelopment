package com.example.bearswithvolleygson;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        String url = "http://192.168.29.34:8080/api/bears";
        JsonArrayRequest req = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        Bear[] bears = gson.fromJson(response.toString(), Bear[].class);
                        String res = response.toString();
                        res = res + res;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String res = "foo";
                        res = res + res;
                    }
                });


        JSONObject params = null;
        try {
            params = new JSONObject().put("name", "Ameya Ranade");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String putUrl = "http://192.168.29.34:8080/api/bears/5c799acb223726f146ebc14c";
        JsonObjectRequest reqDel = new JsonObjectRequest(
                Request.Method.PUT,
                putUrl,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String s = response.toString();
                        s = s + s;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        mQueue.add(reqDel);
    }
}
