package com.example.easyfix.Activites;

import static android.content.ContentValues.TAG;
import static com.example.easyfix.FBref.currentUser;
import static com.example.easyfix.FBref.refUsers;
import static com.example.easyfix.FBref.refWaitingUsers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WaitingUsersListActivity extends AppCompatActivity {
    RecyclerView waitingUsersRv;
    WaitingUsersListAdapter waitingUsersListAdapter;
    String UserUid;
    private FirebaseAuth mAuth;
    ArrayList<User> Users = new ArrayList<User>();
    String orgKey;
    int lastYear;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waitingusers_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        pd = ProgressDialog.show(this, "Loading WaitingUsers...", "",true);
        waitingUsersRv =findViewById(R.id.usersListRv);
        waitingUsersListAdapter=new WaitingUsersListAdapter(Users);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        waitingUsersRv.setLayoutManager(layoutManager);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser FUser = mAuth.getCurrentUser();
        UserUid = FUser.getUid();
        String path = UserUid + "/uId"; // הגעה ישירות למיקום הuId
        Query query = refWaitingUsers.orderByChild(path).equalTo(UserUid); //  לשנות את זה לrefUsers אחרי שאני מסדר את הרמות

        //מציאת מפתח ארגון
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Query sameLastYear;
                lastYear = currentUser.getLastYear();
                if(currentUser.getUserLevel() >= 1000){
                    sameLastYear = refWaitingUsers.orderByChild("lastYear"); // if you are Manager or admin you should see all users.
                }
                else {
                    sameLastYear = refWaitingUsers.orderByChild("lastYear").equalTo(lastYear); // If you are teacher you should see only students within you same year
                }
                sameLastYear.addValueEventListener(waitingUsersListener);


            }


            ValueEventListener waitingUsersListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dS) {
                    System.out.println(dS);
                    Users.clear();
                    for(DataSnapshot data : dS.getChildren()){
                        User user = data.getValue(User.class);
                        Users.add(user);
                    }
                    waitingUsersRv.setAdapter(waitingUsersListAdapter);
                    pd.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error fetching organizations", error.toException());


                }
            };


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);

    }
}