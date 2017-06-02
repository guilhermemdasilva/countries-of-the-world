package com.guilherme_silva.countriesoftheworld.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.guilherme_silva.countriesoftheworld.R;
import com.guilherme_silva.countriesoftheworld.utils.CountryCodeHelper;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CountryDialogFragment extends DialogFragment {
    @BindView(R.id.tvCountryName) TextView tvCountryName;
    @BindView(R.id.ivThumbnailFlag) ImageView ivThumbnailFlag;
    @BindView(R.id.tvCapital) TextView tvCapital;
    @BindView(R.id.tvSubregion) TextView tvSubregion;
    @BindView(R.id.tvPopulation) TextView tvPopulation;
    @BindView(R.id.tvArea) TextView tvArea;
    @BindView(R.id.btDismiss) Button btDismiss;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_country_dialog, container, false);
        ButterKnife.bind(this, view);
        tvCountryName.setText(getArguments().getString("name"));
        final String alpha2Code = getAlpha2CodeArgument().toLowerCase();
        ivThumbnailFlag.setImageResource(CountryCodeHelper.getFlagImageResource(alpha2Code));
        tvCapital.setText(getArguments().getString("capital"));
        tvSubregion.setText(getArguments().getString("subregion"));
        tvPopulation.setText(String.valueOf(getArguments().getInt("population")));
        tvArea.setText(String.valueOf(getArguments().getFloat("area")));
        btDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private String getAlpha2CodeArgument() {
        final Bundle arguments = getArguments();
        if (null != arguments) {
            final String alpha2Code = arguments.getString("alpha2Code");
            if (null != alpha2Code) {
                return alpha2Code;
            } else {
                return "";
            }
        }
        return "";
    }
}
