package com.guilherme_silva.countriesoftheworld.network;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.guilherme_silva.countriesoftheworld.R;
import com.guilherme_silva.countriesoftheworld.models.Country;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

class Client {
    private static final String TAG = Client.class.getSimpleName();
    private RequestQueue requestQueue;
    private Gson gson;
    private Context context;
    private List<Country> countries;

    Client(Context context) {
        this.context = context;
        initializeClient();
        initializeGson();
        fetchCountries();
    }

    private void initializeClient() {
        requestQueue = Volley.newRequestQueue(context);
    }

    private void initializeGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    List<Country> getCountries() {
        return countries;
    }

    private void fetchCountries() {
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(
                Request.Method.GET, context.getString(R.string.url_countries_endpoint),
                future, onCountriesError);
        requestQueue.add(request);
        try {
            countries = Arrays.asList(gson.fromJson(future.get(), Country[].class));
        } catch(InterruptedException | ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private final Response.ErrorListener onCountriesError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(context.getString(R.string.error_title));
            alertDialog.setMessage(context.getString(R.string.error_msg));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
                    context.getString(R.string.button_label_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    };
}
