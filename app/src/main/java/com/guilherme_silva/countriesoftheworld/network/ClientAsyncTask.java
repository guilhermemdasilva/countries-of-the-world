package com.guilherme_silva.countriesoftheworld.network;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.guilherme_silva.countriesoftheworld.fragments.CountryDialogFragment;
import com.guilherme_silva.countriesoftheworld.models.Country;
import com.guilherme_silva.countriesoftheworld.views.adapters.CountryAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientAsyncTask extends AsyncTask<String, String, List<Country>> {
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<String> countriesNames;
    private List<List<Double>> countriesLatLng;

    public ClientAsyncTask(Context context, RecyclerView recyclerView,
                           ArrayList<String> countriesNames, List<List<Double>> countriesLatLng) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.countriesNames = countriesNames;
        this.countriesLatLng = countriesLatLng;
    }

    @Override
    protected List<Country> doInBackground(String... params) {
        Client client = new Client(context);
        return client.getCountries();
    }

    @Override
    protected void onPostExecute(List<Country> countries) {
        for(Country country : countries) {
            countriesNames.add(country.getName());
            if(country.getLatlng() != null && country.getLatlng().size() == 2) {
                countriesLatLng.add(country.getLatlng());
            } else {
                countriesLatLng.add(new ArrayList<>(Arrays.asList(0.0, 0.0)));
            }
        }
        recyclerView.setAdapter(new CountryAdapter(countries, new CountryAdapter.OnCountryItemClicked() {
            @Override
            public void OnCountryInteraction(Country countryInfo) {
                makeCountryFragment(countryInfo);
            }
        }));
        addEmptyItemBottom();
    }

    private void makeCountryFragment(Country countryInfo) {
        FragmentManager fm = ((Activity) context).getFragmentManager();
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

    private void addEmptyItemBottom() {
        Point size = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getSize(size);
        CountryAdapter.BottomDecoration bottomOffsetDecoration = new CountryAdapter.BottomDecoration(size.y);
        recyclerView.addItemDecoration(bottomOffsetDecoration);
    }
}
