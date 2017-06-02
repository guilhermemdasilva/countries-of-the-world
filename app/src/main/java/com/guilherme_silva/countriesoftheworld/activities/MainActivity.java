package com.guilherme_silva.countriesoftheworld.activities;

import android.Manifest;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.SearchView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.guilherme_silva.countriesoftheworld.R;
import com.guilherme_silva.countriesoftheworld.fragments.CountryDialogFragment;
import com.guilherme_silva.countriesoftheworld.models.Country;
import com.guilherme_silva.countriesoftheworld.views.adapters.CountryAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindString(R.string.url_countries_endpoint) String ENDPOINT;
    @BindString(R.string.permission_title) String PERMISSION_TITLE;
    @BindString(R.string.permission_msg) String PERMISSION_MSG;
    @BindString(R.string.button_label_ok) String OK_BUTTON;
    @BindString(R.string.error_title) String ERROR_TITLE;
    @BindString(R.string.error_msg) String ERROR_MSG;

    @BindView(R.id.rvCountriesList) RecyclerView recyclerView;

    private ArrayList<String> countriesNames = new ArrayList<>();
    private RequestQueue requestQueue;
    private Gson gson;
    private LinearLayoutManager linearLayoutManager;
    private final int PERMISSION_REQUEST_INTERNET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeLinearLayoutManager();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        handlePermissions();
        if (arePermissionsGranted()) {
            initializeClient();
            initializeGson();
            fetchCountries();
        }
        handleIntent(getIntent());
    }

    private void initializeClient() {
        requestQueue = Volley.newRequestQueue(this);
    }

    private void initializeGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    public void fetchCountries() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onCountriesLoaded, onCountriesError);
        requestQueue.add(request);
    }

    private final Response.Listener<String> onCountriesLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            List<Country> countries = Arrays.asList(gson.fromJson(response, Country[].class));
            for(Country country : countries) {
                countriesNames.add(country.getName());
            }
            CountryAdapter countryAdapter = new CountryAdapter(countries, new CountryAdapter.OnCountryItemClicked() {
                @Override
                public void OnCountryInteraction(Country countryInfo) {
                    FragmentManager fm = getFragmentManager();
                    Bundle args = new Bundle();
                    CountryDialogFragment countryDialogFragment = new CountryDialogFragment ();
                    args.putString("name", countryInfo.getName());
                    args.putString("alpha2Code", countryInfo.getAlpha2Code());
                    args.putString("capital", countryInfo.getCapital());
                    args.putString("subregion", countryInfo.getSubregion());
                    args.putInt("population", countryInfo.getPopulation());
                    args.putFloat("area", countryInfo.getArea());
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
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(ERROR_TITLE);
            alertDialog.setMessage(ERROR_MSG);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, OK_BUTTON,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    };

    private void handlePermissions() {
        if(!arePermissionsGranted()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_INTERNET);
        }
    }

    private boolean arePermissionsGranted() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
    }

    private void initializeLinearLayoutManager() {
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_REQUEST_INTERNET:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeClient();
                    initializeGson();
                    fetchCountries();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle(PERMISSION_TITLE);
                    alertDialog.setMessage(PERMISSION_MSG);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, OK_BUTTON,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                break;
        }
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
            final String query = intent.getStringExtra(SearchManager.QUERY).trim();
            ArrayList<String> countriesNames = intent.getStringArrayListExtra("countriesNames");
            int index;
            for (index = 0;
                 index < countriesNames.size() &&
                    !countriesNames.get(index).toLowerCase().contains(query.toLowerCase());
                 index++);
            if (index == countriesNames.size()) index = 0;
            linearLayoutManager.scrollToPosition(index);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
}
