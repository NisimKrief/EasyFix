package com.example.easyfix.Activites;

import static android.content.ContentValues.TAG;
import static com.example.easyfix.FBref.currentUser;
import static com.example.easyfix.FBref.refWaitingUsers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfix.Adapters.WaitingUsersListAdapter;
import com.example.easyfix.FBref;
import com.example.easyfix.R;
import com.example.easyfix.Classes.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * The Waiting Users activity
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	20/05/2024
 * Allows users to view and manage a list of waiting users by accepting or deleting them,
 * only teachers, managers and the admin can get here and interact with the items.
 * Users can search for specific users by name.
 * teachers can only see student with the same year as them while managers and the admin can see everyone.
 */
public class WaitingUsersListActivity extends AppCompatActivity {
    /**
     * The RecyclerView for displaying waiting users.
     */
    RecyclerView waitingUsersRv;
    /**
     * The Adapter for the waiting users RecyclerView.
     */
    WaitingUsersListAdapter waitingUsersListAdapter;
    /**
     * The List of waiting users.
     */
    ArrayList<User> Users = new ArrayList<User>();
    /**
     * The last year of the current user.
     */
    int lastYear;
    /**
     * The ProgressDialog for displaying loading indication.
     */
    ProgressDialog pd;
    /**
     * The Query to fetch waiting users sorted by the same last year or userName.
     */
    Query sameLastYear;
    /**
     * The SearchView for searching waiting users by name.
     */
    SearchView searchWaitingUsers;

    /**
     * setting up the user interface (UI), loading data into the activity, etc.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                FBref.checkInternetConnection(this);
                EdgeToEdge.enable(this);
                setContentView(R.layout.activity_waitingusers_list);
                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
                pd = ProgressDialog.show(this, "Loading WaitingUsers...", "", true);
                waitingUsersRv = findViewById(R.id.usersListRv);
                searchWaitingUsers = findViewById(R.id.waitingUsersSearchView);
                waitingUsersListAdapter = new WaitingUsersListAdapter(Users, this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                waitingUsersRv.setLayoutManager(layoutManager);
                lastYear = currentUser.getLastYear();

                ValueEventListener waitingUsersListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        System.out.println(dS);
                        //if (dS.exists())
                           // sendNotification();
                        Users.clear();
                        for (DataSnapshot data : dS.getChildren()) {
                            User user = data.getValue(User.class);
                            if (currentUser.getUserLevel() >= 1000) // if you are Manager or admin you should see all waiting users.
                                Users.add(user);
                            else if (currentUser.getLastYear() == user.getLastYear()) // If you are teacher you should see only students within the same year as yours
                                Users.add(user);

                        }
                        waitingUsersRv.setAdapter(waitingUsersListAdapter);
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error fetching organizations", error.toException());
                        FBref.checkInternetConnection(WaitingUsersListActivity.this);

                    }
                };
                // Set a listener for the SearchView to filter waiting users by name
                searchWaitingUsers.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        // If the search query is empty, fetch all waiting users ordered by the same last year
                        sameLastYear.removeEventListener(waitingUsersListener);
                        sameLastYear = null;
                        if (newText.isEmpty()) {
                            sameLastYear = refWaitingUsers.orderByChild("lastYear");
                            sameLastYear.addValueEventListener(waitingUsersListener);
                        } else // If there is a search query, filter waiting users by username
                            sameLastYear = refWaitingUsers.orderByChild("userName").startAt(newText).endAt(newText + "\uf8ff");
                        sameLastYear.addValueEventListener(waitingUsersListener);
                        return true;
                    }
                });
                // If the SearchView query is empty (when activity first created), fetch all waiting users with the same last year
                if (searchWaitingUsers.getQuery().toString().isEmpty()) {
                    sameLastYear = refWaitingUsers.orderByChild("lastYear");
                    sameLastYear.addValueEventListener(waitingUsersListener);
                }

            }
    /**
     * The default implementation simply finishes the current activity, to
     * go back to "ReportsActivity" and save resources
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

