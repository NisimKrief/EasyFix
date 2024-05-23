package com.example.easyfix.Activites;

import static android.content.ContentValues.TAG;
import static com.example.easyfix.FBref.refUsers;
import static com.example.easyfix.FBref.refWaitingUsers;

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

import com.example.easyfix.Adapters.UsersListAdapter;
import com.example.easyfix.Classes.User;
import com.example.easyfix.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageUsersListActivity extends AppCompatActivity {
    RecyclerView UsersRv;
    UsersListAdapter UsersListAdapter;
    String UserUid;
    private FirebaseAuth mAuth;
    ArrayList<User> Users = new ArrayList<User>();
    String orgKey;
    int userLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_users_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UsersRv =findViewById(R.id.usersListRv);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        UsersRv.setLayoutManager(layoutManager);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser FUser = mAuth.getCurrentUser();
        UserUid = FUser.getUid();
        refUsers.child(UserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userLevel = snapshot.child("userLevel").getValue(Integer.class);
                UsersListAdapter=new UsersListAdapter(Users, userLevel);
                Query higherUserLevel = refUsers.orderByChild("userLevel").endAt(userLevel -1); // סינון לפי דרגה, יופיעו רק משתמשים בדרגה נמוכה יותר, מורים יראו רק תלמידים, מנהלים יראו את כולם.
                higherUserLevel.addValueEventListener(ManageUsersListener);
            }
            ValueEventListener ManageUsersListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dS) {
                    System.out.println(dS);
                    Users.clear();
                    for(DataSnapshot data : dS.getChildren()){
                        User user = data.getValue(User.class);
                        Users.add(user);
                    }
                    UsersRv.setAdapter(UsersListAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error fetching organizations", error.toException());


                }
            };

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}