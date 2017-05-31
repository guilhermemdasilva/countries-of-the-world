package com.guilherme_silva.countriesoftheworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ENDPOINT = "https://restcountries.eu/rest/v2/" +
        "all?fields=name;nativeName;alpha2Code;alpha3Code;region;" +
        "subregion;capital;demonym;area;population";

    private RequestQueue requestQueue;
    private Gson gson;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.countries_grid);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        requestQueue = Volley.newRequestQueue(this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        fetchCountries();
    }

    private void fetchCountries() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onCountriesLoaded, onCountriesError);
        requestQueue.add(request);
    }

    private final Response.Listener<String> onCountriesLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            List<CountryInfo> countries = Arrays.asList(gson.fromJson(response, CountryInfo[].class));
            CountryAdapter countryAdapter = new CountryAdapter(countries, new CountryAdapter.OnCountryItemClicked() {
                @Override
                public void OnCountryInteraction(CountryInfo countryInfo) {
                    Toast.makeText(getApplicationContext(), countryInfo.capital, Toast.LENGTH_LONG).show();
                }
            });
            recyclerView.setAdapter(countryAdapter);
        }
    };

    private final Response.ErrorListener onCountriesError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("MainActivity", error.toString());
        }
    };
}
