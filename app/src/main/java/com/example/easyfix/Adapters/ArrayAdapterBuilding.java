package com.example.easyfix.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.easyfix.Classes.Building;
import com.example.easyfix.R;

import java.util.ArrayList;

/**
 * Array adapter for Buildings
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	23/05/2024
 * This adapter provides a customized view and interactions for each item in the data set.
 */
public class ArrayAdapterBuilding extends BaseAdapter {
    private Context context;
    private ArrayList<Building> Buildings;

    /**
     * Constructs a new ArrayAdapterBuilding with the specified context and list of buildings.
     *
     * @param context   The context in which the adapter is being used. (ReportsActivity.class)
     * @param Buildings The list of Building objects to be displayed.
     */
    public ArrayAdapterBuilding(Context context, ArrayList<Building> Buildings) {
        this.context = context;
        this.Buildings = Buildings;
    }

    /**
     * Returns the number of items in the data set represented by this adapter.
     *
     * @return The total number of items in the data set.
     */
    @Override
    public int getCount() {
        return Buildings !=null ? Buildings.size() : 0;
    }

    /**
     * Returns the item at the specified position in the data set.
     *
     * @param position The position of the item within the adapter's data set.
     * @return The item at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return position;
    }

    /**
     * Returns the row ID of the item at the specified position in the data set.
     *
     * @param position The position of the item within the adapter's data set.
     * @return The row ID of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns a View corresponding to the data at the specified position.
     * sets the building name for each item.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent ViewGroup that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_building, parent, false);

        TextView txtName = rootView.findViewById(R.id.itemBuildingText);
        txtName.setText(Buildings.get(position).getBuildingName());

        return rootView;
    }
    /**
     * Returns whether the item at the specified position is enabled.
     * This method is used to control the interactivity of items in a Spinner.
     * only the first position is disabled since its a hint building to help users
     *
     * @param position The position of the item within the adapter's data set.
     * @return True if the item is enabled, false otherwise.
     */
    public boolean isEnabled(int position) {
        // Removing the option to press the first choice in the spinner (which is a hint option)
        return position != 0;
    }

}