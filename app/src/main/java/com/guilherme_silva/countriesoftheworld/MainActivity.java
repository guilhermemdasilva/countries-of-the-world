package com.guilherme_silva.countriesoftheworld;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ENDPOINT = "https://restcountries.eu/rest/v2/" +
        "all?fields=name;alpha2Code;subregion;capital;area;population";

    private RequestQueue requestQueue;
    private Gson gson;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<String> countriesNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.countries_grid);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        requestQueue = Volley.newRequestQueue(this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        fetchCountries();
        handleIntent(getIntent());
    }

    @Override
    public void startActivity(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            intent.putExtra("countriesNames", countriesNames);
        }
        super.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY).trim();
            countriesNames = intent.getStringArrayListExtra("countriesNames");

            int index = 0;
            for (String countryName : countriesNames) {
                if (countryName.equalsIgnoreCase(query)) {
                    linearLayoutManager.scrollToPosition(index);
                    break;
                }
                index++;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    private void fetchCountries() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onCountriesLoaded, onCountriesError);
        requestQueue.add(request);
    }

    private final Response.Listener<String> onCountriesLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            List<CountryInfo> countries = Arrays.asList(gson.fromJson(response, CountryInfo[].class));
            for(CountryInfo country : countries) {
                countriesNames.add(country.name);
            }
            CountryAdapter countryAdapter = new CountryAdapter(countries, new CountryAdapter.OnCountryItemClicked() {
                @Override
                public void OnCountryInteraction(CountryInfo countryInfo) {
                    FragmentManager fm = getFragmentManager();
                    Bundle args = new Bundle();
                    CountryDialogFragment countryDialogFragment = new CountryDialogFragment ();
                    args.putString("name", countryInfo.name);
                    args.putString("alpha2Code", countryInfo.alpha2Code);
                    args.putString("capital", countryInfo.capital);
                    args.putString("subregion", countryInfo.subregion);
                    args.putInt("population", countryInfo.population);
                    args.putFloat("area", countryInfo.area);
                    countryDialogFragment.setArguments(args);
                    countryDialogFragment.show(fm, "Country Dialog Fragment");
                }
            });
            recyclerView.setAdapter(countryAdapter);
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);

            CountryAdapter.BottomDecoration bottomOffsetDecoration = new CountryAdapter.BottomDecoration(size.y);
            recyclerView.addItemDecoration(bottomOffsetDecoration);
        }
    };

    private final Response.ErrorListener onCountriesError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("MainActivity", error.toString());
        }
    };
}
