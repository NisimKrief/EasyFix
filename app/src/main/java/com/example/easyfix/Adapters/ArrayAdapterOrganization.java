package com.example.easyfix.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.easyfix.Classes.Organization;
import com.example.easyfix.R;

import java.util.ArrayList;

public class ArrayAdapterOrganization extends BaseAdapter {
    private Context context;
    private ArrayList<Organization> Organizations;

    public ArrayAdapterOrganization(Context context, ArrayList<Organization> organizations) {
        this.context = context;
        Organizations = organizations;
    }

    @Override
    public int getCount() {
        return Organizations !=null ? Organizations.size() : 0;
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
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_organization, parent, false);

        TextView txtName = rootView.findViewById(R.id.itemText);
        txtName.setText(Organizations.get(position).getOrganizationName());

        return rootView;
    }
    public boolean isEnabled(int position) {
        // Removing the option to press the first choice in the spinner (which is a hint option)
        return position != 0;
    }

}