package com.taxiandroid.ru.taxiandr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by saperov on 29.11.15.
 */
public class CustomEigthAdapter extends ArrayAdapter<StayArea> {
    public CustomEigthAdapter (Context context,ArrayList<StayArea> stayAreas){
        super(context,0,stayAreas);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
    StayArea stayArea = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_eight, parent, false);
        }
        // Lookup view for data population
        TextView tvEight = (TextView) convertView.findViewById(R.id.tvEight);
        tvEight.setText((CharSequence) stayArea.stay);

        return convertView;
    }
}
