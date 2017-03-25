package com.example.ai.amann_gohack;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ifirf on 3/26/2017.
 */

public class TheMarker {

    private Context context;
    private String currentStatus;

    public TheMarker(Context context){
        this.context = context;
    }

    public interface VolleyCallback{
        void onSuccessResponse(String result);
    }

    public void getCurrentStatus(final Double latitude, final Double longitude, final VolleyCallback callback){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.api_domain+"getpin",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccessResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", Config.key);
                params.put("lat", Double.toString(latitude));
                params.put("lng", Double.toString(longitude));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        requestQueue.add(stringRequest);
    }
}
