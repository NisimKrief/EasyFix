package com.example.easyfix.Adapters;

import static com.example.easyfix.FBref.currentUser;
import static com.example.easyfix.FBref.refUsers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfix.Activites.ManageUsersListActivity;
import com.example.easyfix.FBref;
import com.example.easyfix.R;
import com.example.easyfix.Classes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {

    private ArrayList<User> Users;
    private Context context;
    private int userLevel;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView UsersTv, UsersJobTv;
        private final ImageView userIcon;
        private final CardView cardView;

        LinearLayout containerll;

        public ViewHolder(View view) {
            super(view);
            UsersTv = view.findViewById(R.id.UsersTv);
            UsersJobTv = view.findViewById(R.id.userJobTv);
            userIcon = view.findViewById(R.id.userIcon);
            cardView = view.findViewById(R.id.cardId);
            containerll = view.findViewById(R.id.UcontainerLL);
        }

        public TextView getTextView() {
            return UsersTv;
        }
    }

    public UsersListAdapter(ArrayList<User> taskDataset, Context context) {
        this.Users = taskDataset;
        this.context = context;
    }

    @Override
    public UsersListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_users, viewGroup, false);
        return new UsersListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersListAdapter.ViewHolder viewHolder, final int position) {
        User user = Users.get(position);
        String name = user.getUserName();
        viewHolder.UsersTv.setText(name);
        switch(user.getUserLevel()){
            case 1:
                viewHolder.UsersJobTv.setText("תלמיד");
                viewHolder.UsersJobTv.setTextColor(Color.parseColor("#000000")); // Set text color for student
                viewHolder.userIcon.setColorFilter(Color.parseColor("#000000")); // Set icon color for student
                break;
            case 10:
                viewHolder.UsersJobTv.setText("מורה");
                viewHolder.UsersJobTv.setTextColor(Color.parseColor("#4CAF50")); // Set text color for teacher
                viewHolder.userIcon.setColorFilter(Color.parseColor("#4CAF50")); // Set icon color for teacher
                break;
            case 100:
                viewHolder.UsersJobTv.setText("עובד תחזוקה");
                viewHolder.UsersJobTv.setTextColor(Color.parseColor("#2196F3")); // Set text color for construction worker
                viewHolder.userIcon.setColorFilter(Color.parseColor("#2196F3")); // Set icon color for construction worker
                break;
            case 1000:
                viewHolder.UsersJobTv.setText("מנהל");
                viewHolder.UsersJobTv.setTextColor(Color.parseColor("#FF9800")); // Set text color for manager
                viewHolder.userIcon.setColorFilter(Color.parseColor("#FF9800")); // Set icon color for manager
                break;
            case 10000:
                viewHolder.UsersJobTv.setText("אדמין");
                viewHolder.UsersJobTv.setTextColor(Color.parseColor("#9E9E9E")); // Set text color for admin
                viewHolder.userIcon.setColorFilter(Color.parseColor("#9E9E9E")); // Set icon color for admin
                break;
        }



        viewHolder.containerll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), viewHolder.containerll);
                popupMenu.inflate(R.menu.manageusersmenu);
                if(currentUser.getUserLevel() <10000){
                    popupMenu.getMenu().findItem(R.id.managerMenu).setVisible(false); // Disables the option to make it a manager
                    if (currentUser.getUserLevel() < 1000) {
                        popupMenu.getMenu().findItem(R.id.teacherMenu).setVisible(false); // Disables the option to make it a teacher
                        popupMenu.getMenu().findItem(R.id.constructionWorkerMenu).setVisible(false); // DDisables the option to make it a construction worker
                    }
                }
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.studentMenu) {
                            refUsers.child(user.getuId()).child("userLevel").setValue(1);
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.teacherMenu) {
                            refUsers.child(user.getuId()).child("userLevel").setValue(10);
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.constructionWorkerMenu) {
                            refUsers.child(user.getuId()).child("userLevel").setValue(100);
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.managerMenu) {
                            refUsers.child(user.getuId()).child("userLevel").setValue(1000);
                            return true;
                        }
                        if (menuItem.getItemId() == R.id.deleteUserMenu) {
                            refUsers.child(user.getuId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, user.getUserLevel() + "was removed successfully", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "failed to remove user", Toast.LENGTH_SHORT).show();
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

}