package com.example.easyfix;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class WaitingUsersListAdapter extends RecyclerView.Adapter<WaitingUsersListAdapter.ViewHolder> {

    private ArrayList<User> Users;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView waitingUsersTv;
        private final CardView cardView;
        LinearLayout containerll;

        public ViewHolder(View view) {
            super(view);
            waitingUsersTv = view.findViewById(R.id.waitingUsersTv);
            cardView = view.findViewById(R.id.cardId);
            containerll = view.findViewById(R.id.WcontainerLL);
        }

        public TextView getTextView() {
            return waitingUsersTv;
        }
    }

    public WaitingUsersListAdapter(ArrayList<User> taskDataset) {
        this.Users = taskDataset;
    }

    @Override
    public WaitingUsersListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_waitingusers, viewGroup, false);
        return new WaitingUsersListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WaitingUsersListAdapter.ViewHolder viewHolder, final int position) {
        User user = Users.get(position);
        String email = fetchUserEmail(user.getuId());

        viewHolder.waitingUsersTv.setText(email);

        viewHolder.containerll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), viewHolder.containerll);
                popupMenu.inflate(R.menu.waitingusersmenu);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.acceptMenu) {

                            return true;
                        }
                        if (menuItem.getItemId() == R.id.deleteMenu) {

                            return true;
                        }
                        return false;
                    }
                });
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return Users.size();
    }
    private String fetchUserEmail(String userId) {
        FirebaseAuth.getInstance().getUser(userId)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult();
                        if (user != null) {
                            String email = user.getEmail();
                            if (email != null) {
                                return email;
                            }
                        }
                    } else {
                        Log.w(TAG, "Failed to fetch user email", task.getException());
                    }
                });
        return "No Email?";
    }
}