package com.guilherme_silva.countriesoftheworld;

import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

    private List<CountryInfo> countryList;
    private OnCountryItemClicked onCountryItemClicked;

    public CountryAdapter(List<CountryInfo> countryList, OnCountryItemClicked onCountryItemClicked) {
        this.countryList = countryList;
        this.onCountryItemClicked = onCountryItemClicked;
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    @Override
    public void onBindViewHolder(final CountryViewHolder countryViewHolder, int index) {
        CountryInfo country = countryList.get(index);
        countryViewHolder.mItem = countryList.get(index);
        countryViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCountryItemClicked.OnCountryInteraction(countryViewHolder.mItem);
            }
        });
        if(CountryCodeHelper.getDrawableResource(country.alpha2Code.toLowerCase()) != 0) {
            countryViewHolder.ivFlag.setImageResource(CountryCodeHelper
                    .getDrawableResource(country.alpha2Code.toLowerCase()));
        } else {
            //if there is no flag, use UN flag
            countryViewHolder.ivFlag.setImageResource(CountryCodeHelper
                    .getDrawableResource("un"));
        }
    }

    @Override
    public CountryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.card_layout, parent, false);
        return new CountryViewHolder(item);
    }

    public static class CountryViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CountryInfo mItem;
        @BindView(R.id.flag_image) protected ImageView ivFlag;
        @BindView(R.id.card_view) public CardView cardView;

        public CountryViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }

    static class BottomDecoration extends RecyclerView.ItemDecoration {
        private int mBottom;

        public BottomDecoration(int bottom) {
            mBottom = bottom;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int itemCount = state.getItemCount();
            int position = parent.getChildAdapterPosition(view);
            if (itemCount > 0 && position == itemCount - 1) {
                outRect.set(0, 0, 0, mBottom);
            } else {
                outRect.set(0, 0, 0, 0);
            }

        }
    }

    public interface OnCountryItemClicked {
        void OnCountryInteraction(CountryInfo countryInfo);
    }
}
