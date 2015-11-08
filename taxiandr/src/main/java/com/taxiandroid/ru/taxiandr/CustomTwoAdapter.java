package com.taxiandroid.ru.taxiandr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by saperov on 01.11.15.
 */
public class CustomTwoAdapter extends ArrayAdapter<Two> {
    public CustomTwoAdapter (Context context,ArrayList<Two> two_items){
        super(context,0,two_items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Two two = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_two, parent, false);
        }
        // Lookup view for data population
        TextView text1 = (TextView) convertView.findViewById(R.id.tvTwo);
        text1.setText(two.two_item);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivTwo);
        if (position == 0) {
            imageView.setImageResource(R.drawable.ic_action_mail);
        }
        if (position == 1) {
            imageView.setImageResource(R.drawable.ic_action_phone_outgoing);
        }
        if (position == 3) {
            imageView.setImageResource(R.drawable.ic_action_undo);
        }

        return convertView;
    }
}
