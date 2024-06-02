package com.example.easyfix.Activites;

import static android.content.ContentValues.TAG;
import static com.example.easyfix.FBref.currentUser;
import static com.example.easyfix.FBref.refUsers;
import static com.example.easyfix.FBref.refWaitingUsers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
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

public class WaitingUsersListActivity extends AppCompatActivity {
    RecyclerView waitingUsersRv;
    WaitingUsersListAdapter waitingUsersListAdapter;
    String UserUid;
    ArrayList<User> Users = new ArrayList<User>();
    int lastYear;
    ProgressDialog pd;
    Query sameLastYear;
    SearchView searchWaitingUsers;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
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
                waitingUsersListAdapter = new WaitingUsersListAdapter(Users);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                waitingUsersRv.setLayoutManager(layoutManager);
                UserUid = currentUser.getuId();
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
                searchWaitingUsers.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        sameLastYear.removeEventListener(waitingUsersListener);
                        sameLastYear = null;
                        if (newText.isEmpty()) {
                            sameLastYear = refWaitingUsers.orderByChild("lastYear");
                            sameLastYear.addValueEventListener(waitingUsersListener);
                        } else
                            sameLastYear = refWaitingUsers.orderByChild("userName").startAt(newText).endAt(newText + "\uf8ff");
                        sameLastYear.addValueEventListener(waitingUsersListener);
                        return true;
                    }
                });
                if (searchWaitingUsers.getQuery().toString().isEmpty()) {
                    sameLastYear = refWaitingUsers.orderByChild("lastYear");
                    sameLastYear.addValueEventListener(waitingUsersListener);
                }

            }

   /* public void sendNotification(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.baseline_access_time_24) // Set the small icon for the notification
                .setContentTitle("Test Notification") // Set the title of the notification
                .setContentText("This is a test notification") // Set the content text of the notification
                .setAutoCancel(true) // Dismiss the notification when clicked
                .setContentIntent(pendingIntent); // Set the content intent

        // Get the notification manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if Android version is Oreo or higher, and set notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Notify
        notificationManager.notify(0, notificationBuilder.build());
    }*/
}

