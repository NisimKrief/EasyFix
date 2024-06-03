package com.example.easyfix.Adapters;

import static com.example.easyfix.FBref.refUsers;
import static com.example.easyfix.FBref.refWaitingUsers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfix.FBref;
import com.example.easyfix.R;
import com.example.easyfix.Classes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

/**
 * The adapter for displaying a list of waiting users.
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	19/05/2024
 * This adapter binds the data of waiting users to the views in the RecyclerView.
 * It also handles the users with higher user level (10+) interactions for accepting or deleting users from the waiting list.
 */
public class WaitingUsersListAdapter extends RecyclerView.Adapter<WaitingUsersListAdapter.ViewHolder> {

    private ArrayList<User> Users;
    private Context context;

    /**
     * ViewHolder for the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView waitingUsersTv, lastYearTv;
        private final CardView cardView;
        /**
         * The Containerll.
         */
        LinearLayout containerll;

        /**
         * Constructs a new ViewHolder.
         *
         * @param view the view to hold
         */
        public ViewHolder(View view) {
            super(view);
            waitingUsersTv = view.findViewById(R.id.waitingUsersTv);
            lastYearTv = view.findViewById(R.id.lastYearTv);
            cardView = view.findViewById(R.id.cardId);
            containerll = view.findViewById(R.id.WcontainerLL);
        }

        /**
         * Gets the waiting users TextView.
         *
         * @return the waiting users TextView
         */
        public TextView getTextView() {
            return waitingUsersTv;
        }
    }

    /**
     * Constructs a new WaitingUsersListAdapter.
     *
     * @param taskDataset the dataset of waiting users
     * @param context     the context (WaitingUsersListActivity)
     */
    public WaitingUsersListAdapter(ArrayList<User> taskDataset, Context context) {
        this.Users = taskDataset;
        this.context = context;
    }

    @Override
    public WaitingUsersListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_waitingusers, viewGroup, false);
        return new WaitingUsersListAdapter.ViewHolder(view);
    }

    /**
     * Binds the data of a waiting user to the view and sets up interaction handling.
     * <p>
     * This method arranges the waiting users list, displaying each user's name and grade level.
     * When an item is clicked, it opens a popup menu with two options: accept or delete the user.
     * <ul>
     *     <li>If the user is accepted, they are moved from the "WaitingUsers" node to the "Users" node in the Firebase Realtime Database, allowing them to log in.</li>
     *     <li>If the user is deleted, they are removed from the "WaitingUsers" node in the Firebase Realtime Database.</li>
     * </ul>
     * </p>
     *
     * @param viewHolder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position   The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(WaitingUsersListAdapter.ViewHolder viewHolder, final int position) {
        User user = Users.get(position);
        String name = user.getUserName();

        viewHolder.waitingUsersTv.setText(name);
        viewHolder.lastYearTv.setText(switchLastYear(Users.get(position).getLastYear()));
        viewHolder.containerll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), viewHolder.containerll);
                popupMenu.inflate(R.menu.waitingusersmenu);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.acceptMenu) {
                            user.setUserLevel(1);
                            refUsers.child(user.getuId()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    refWaitingUsers.child(user.getuId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Successfully accepted " + user.getUserName() + " to the organization", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Failed to add " + user.getUserName() + " to the organization", Toast.LENGTH_SHORT).show();
                                    FBref.checkInternetConnection(context);
                                }
                            });
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.deleteMenu) {
                            refWaitingUsers.child(user.getuId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Successfully deleted " + user.getUserName(), Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Failed to delete " + user.getUserName(), Toast.LENGTH_SHORT).show();
                                    FBref.checkInternetConnection(context);
                                }
                            });
                            //להוסיף טוסט הצלחה
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return Users.size();
    }

    /**
     * Converts the last year integer to a string representation of the class.
     *
     * @param lastYear the last year of the user (The year he finished school)
     * @return the string representation of the class
     */
    public String switchLastYear(int lastYear) {
        String className;
        switch (lastYear) {
            case 2024:
                className = "כיתה יב";
                break;
            case 2025:
                className = "כיתה יא";
                break;
            case 2026:
                className = "כיתה י";
                break;
            case 2027:
                className = "כיתה ט";
                break;
            case 2028:
                className = "כיתה ח";
                break;
            case 2029:
                className = "כיתה ז'";
                break;
            case 2030:
                className = "כיתה ו'";
                break;
            case 2031:
                className = "כיתה ה'";
                break;
            case 2032:
                className = "כיתה ד'";
                break;
            case 2033:
                className = "כיתה ג'";
                break;
            case 2034:
                className = "כיתה ב'";
                break;
            case 2035:
                className = "כיתה א'";
                break;
            default:
                className = null; // Return null if no class has found
                break;
        }
        return className;
    }

}