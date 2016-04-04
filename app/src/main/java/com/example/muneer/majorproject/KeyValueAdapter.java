package com.example.muneer.majorproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muneer on 06-03-2016.
 */
public class KeyValueAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public KeyValueAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(KeyValue object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        KeyValueHolder keyValueHolder;
        if(row == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.key_value_layout, parent, false);
            keyValueHolder = new KeyValueHolder();
            keyValueHolder.textViewKey = (TextView) row.findViewById(R.id.textViewKey);
            keyValueHolder.textViewValue = (TextView) row.findViewById(R.id.textViewValue);
            row.setTag(keyValueHolder);
        }
        else
        {
            keyValueHolder = (KeyValueHolder) row.getTag();
        }

        KeyValue keyValue = (KeyValue) this.getItem(position);
        keyValueHolder.textViewKey.setText(keyValue.getKey());
        keyValueHolder.textViewValue.setText(keyValue.getValue());

        return row;
    }

    static class KeyValueHolder
    {
        TextView textViewKey, textViewValue;
    }
}
