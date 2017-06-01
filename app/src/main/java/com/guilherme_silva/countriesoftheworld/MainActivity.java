package com.guilherme_silva.countriesoftheworld;

import android.Manifest;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
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

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindString(R.string.endpoint_url) String ENDPOINT;
    @BindString(R.string.permission_title) String PERMISSION_TITLE;
    @BindString(R.string.permission_msg) String PERMISSION_MSG;
    @BindString(R.string.ok_button) String OK_BUTTON;
    @BindString(R.string.error_title) String ERROR_TITLE;
    @BindString(R.string.error_msg) String ERROR_MSG;

    private RequestQueue requestQueue;
    private Gson gson;
    @BindView(R.id.countries_list) RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<String> countriesNames = new ArrayList<>();
    private final int PERMISSION_REQUEST_INTERNET = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_INTERNET);

        } else {
            requestQueue = Volley.newRequestQueue(this);
            fetchCountries();
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        handleIntent(getIntent());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_REQUEST_INTERNET:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestQueue = Volley.newRequestQueue(this);
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
            String query = intent.getStringExtra(SearchManager.QUERY).trim();
            countriesNames = intent.getStringArrayListExtra("countriesNames");

            int index = 0;
            for (String countryName : countriesNames) {
                if (countryName.toLowerCase().contains(query.toLowerCase())) {
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
}
