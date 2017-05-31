package com.guilherme_silva.countriesoftheworld;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CountryDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_country_dialog, container, false);
        TextView tvCountryName = (TextView) rootView.findViewById(R.id.tvCountryName);
        tvCountryName.setText(getArguments().getString("name"));
        ImageView ivThumbnailFlag = (ImageView) rootView.findViewById(R.id.ivThumbnailFlag);
        ivThumbnailFlag.setImageResource(CountryCodeHelper
                .getDrawableResource(getArguments().getString("alpha2Code").toLowerCase()));
        TextView tvCapital = (TextView) rootView.findViewById(R.id.tvCapital);
        tvCapital.setText(getArguments().getString("capital"));
        TextView tvSubregion = (TextView) rootView.findViewById(R.id.tvSubregion);
        tvSubregion.setText(getArguments().getString("subregion"));
        TextView tvPopulation = (TextView) rootView.findViewById(R.id.tvPopulation);
        tvPopulation.setText(getArguments().getInt("population") + "");
        TextView tvArea = (TextView) rootView.findViewById(R.id.tvArea);
        tvArea.setText(getArguments().getFloat("area") + "");
        Button btDismiss = (Button) rootView.findViewById(R.id.btDismiss);
        btDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }
}
