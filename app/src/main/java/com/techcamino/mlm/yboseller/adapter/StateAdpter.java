package com.techcamino.mlm.yboseller.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techcamino.mlm.yboseller.R;
import com.techcamino.mlm.yboseller.details.CityDetails;
import com.techcamino.mlm.yboseller.details.StateDetails;

import java.util.ArrayList;
import java.util.List;

public class StateAdpter extends BaseAdapter {

    private List<StateDetails> stateDetailsList;
    private Context context;
    private LayoutInflater inflter;

    public StateAdpter(Context applicationContext, ArrayList<StateDetails> stateDetailsList) {
        this.context = applicationContext;
        this.stateDetailsList = stateDetailsList;
        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return stateDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return stateDetailsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position == 0) {
            // Disable the first item from Spinner
            // First item will be use for hint
            return false;
        } else {
            return true;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.spinner_item, null);
        TextView names = (TextView) convertView.findViewById(R.id.spinner_item);

        if (position == 0) {
            // Set the hint text color gray
            names.setTextColor(Color.GRAY);
        } else {
            names.setTextColor(Color.BLACK);
        }

        names.setText(stateDetailsList.get(position).getName());
        return convertView;
    }
}
