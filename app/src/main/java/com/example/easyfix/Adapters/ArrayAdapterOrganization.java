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

/**
 * The Array Adapter for organizations
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	22/04/2024
 * A custom ArrayAdapter for displaying Organization objects in a Spinner
 * This adapter provides a customized interactions for each item in the data set.
 */
public class ArrayAdapterOrganization extends BaseAdapter {
    private Context context;
    private ArrayList<Organization> Organizations;

    /**
     * Constructs a new ArrayAdapterOrganization with the specified context and list of organizations.
     *
     * @param context       The context in which the adapter is being used.
     * @param organizations The list of Organization objects to be displayed.
     */
    public ArrayAdapterOrganization(Context context, ArrayList<Organization> organizations) {
        this.context = context;
        Organizations = organizations;
    }

    /**
     * Returns the number of items in the data set represented by this adapter.
     *
     * @return The total number of items in the data set.
     */
    @Override
    public int getCount() {
        return Organizations !=null ? Organizations.size() : 0;
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
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent ViewGroup that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_organization, parent, false);

        TextView txtName = rootView.findViewById(R.id.itemText);
        txtName.setText(Organizations.get(position).getOrganizationName());

        return rootView;
    }
    /**
     * Returns whether the item at the specified position is enabled.
     * This method is used to control the interactivity of items in a Spinner.
     *only the first position is disabled since its a hint organization to help users
     *
     * @param position The position of the item within the adapter's data set.
     * @return True if the item is enabled, false otherwise.
     */
    public boolean isEnabled(int position) {
        // Removing the option to press the first choice in the spinner (which is a hint option)
        return position != 0;
    }

}