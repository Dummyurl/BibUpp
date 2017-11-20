package com.bibupp.androidvantage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.bibupp.androidvantage.CountryHolder;
import com.bibupp.androidvantage.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by ADMIN on 16-Jan-17.
 */

public class CountryAdapter extends ArrayAdapter<CountryHolder> {

    Context context;
    int resource, textViewResourceId;
    List<CountryHolder> items, tempItems, suggestions;

    public CountryAdapter(Context context, int resource, int textViewResourceId, List<CountryHolder> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<CountryHolder>(items); // this makes the difference.
        suggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.country_listitems, parent, false);
        }
        CountryHolder people = items.get(position);
        if (people != null) {
            TextView lblName = (TextView) view.findViewById(R.id.textView1);
            if (lblName != null)
                lblName.setText(people.getName());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((CountryHolder) resultValue).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (CountryHolder people : tempItems) {
                    if (people.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<CountryHolder> filterList = (ArrayList<CountryHolder>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (CountryHolder people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            }
        }
    };
}