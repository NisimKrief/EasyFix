package com.example.easyfix.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.easyfix.Classes.Building;
import com.example.easyfix.Classes.Organization;
import com.example.easyfix.R;

import java.util.ArrayList;

public class ArrayAdapterBuilding extends BaseAdapter {
    private Context context;
    private ArrayList<Building> Buildings;

    public ArrayAdapterBuilding(Context context, ArrayList<Building> Buildings) {
        this.context = context;
        this.Buildings = Buildings;
    }

    @Override
    public int getCount() {
        return Buildings !=null ? Buildings.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_building, parent, false);

        TextView txtName = rootView.findViewById(R.id.itemBuildingText);
        txtName.setText(Buildings.get(position).getBuildingName());

        return rootView;
    }
    public boolean isEnabled(int position) {
        // Removing the option to press the first choice in the spinner (which is a hint option)
        return position != 0;
    }

}