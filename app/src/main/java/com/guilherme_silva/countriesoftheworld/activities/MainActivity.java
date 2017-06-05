package com.guilherme_silva.countriesoftheworld.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;

import com.guilherme_silva.countriesoftheworld.R;
import com.guilherme_silva.countriesoftheworld.network.ClientAsyncTask;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindString(R.string.permission_title) String PERMISSION_TITLE;
    @BindString(R.string.permission_msg) String PERMISSION_MSG;
    @BindString(R.string.button_label_ok) String OK_BUTTON;

    @BindView(R.id.rvCountriesList) RecyclerView recyclerView;

    private LinearLayoutManager linearLayoutManager;
    private final int PERMISSION_REQUEST_INTERNET = 1;
    private ArrayList<String> countriesNames = new ArrayList<>();

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
           new ClientAsyncTask(this, recyclerView, countriesNames).execute();
        }
        handleIntent(getIntent());
    }

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
                    new ClientAsyncTask(this, recyclerView, countriesNames).execute();
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
