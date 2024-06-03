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

public class WaitingUsersListAdapter extends RecyclerView.Adapter<WaitingUsersListAdapter.ViewHolder> {

    private ArrayList<User> Users;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView waitingUsersTv, lastYearTv;
        private final CardView cardView;
        LinearLayout containerll;

        public ViewHolder(View view) {
            super(view);
            waitingUsersTv = view.findViewById(R.id.waitingUsersTv);
            lastYearTv = view.findViewById(R.id.lastYearTv);
            cardView = view.findViewById(R.id.cardId);
            containerll = view.findViewById(R.id.WcontainerLL);
        }

        public TextView getTextView() {
            return waitingUsersTv;
        }
    }

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