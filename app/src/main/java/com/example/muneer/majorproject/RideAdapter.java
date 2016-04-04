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
public class RideAdapter extends ArrayAdapter
{
    List list = new ArrayList();

    public RideAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(SharedRide object) {
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
        RideHolder rideHolder;
        if(row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.row_layout, parent, false);
            rideHolder = new RideHolder();

            rideHolder.tv_from = (TextView) row.findViewById(R.id.tv_from);
            rideHolder.tv_to = (TextView) row.findViewById(R.id.tv_to);
            rideHolder.tv_date = (TextView) row.findViewById(R.id.tv_date);
            rideHolder.tv_time = (TextView) row.findViewById(R.id.tv_time);

            row.setTag(rideHolder);
        }
        else {
            rideHolder = (RideHolder) row.getTag();
        }

        SharedRide sharedRide = (SharedRide) this.getItem(position);
        rideHolder.tv_from.setText(sharedRide.getFrom());
        rideHolder.tv_to.setText(sharedRide.getTo());
        rideHolder.tv_date.setText(sharedRide.getDate());
        rideHolder.tv_time.setText(sharedRide.getTime());

        return row;
    }

    static class RideHolder
    {
        TextView tv_from, tv_to, tv_date, tv_time;
    }
}
