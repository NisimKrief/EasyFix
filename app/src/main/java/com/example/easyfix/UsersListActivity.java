package com.example.easyfix;

import static android.content.ContentValues.TAG;
import static com.example.easyfix.FBref.refOrganizations;
import static com.example.easyfix.FBref.refReports;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersListActivity extends AppCompatActivity {
    RecyclerView UsersRv;
    WaitingUsersListAdapter waitingUsersListAdapter;
    String UserUid;
    private FirebaseAuth mAuth;
    ArrayList<User> Users = new ArrayList<User>();
    String orgKeyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_users_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        System.out.println(FirebaseAuth.getInstance().getCurrentUser());
        UsersRv=findViewById(R.id.usersListRv);
        //waitingUsersListAdapter=new ReportListAdapter(Users);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        UsersRv.setLayoutManager(layoutManager);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser FUser = mAuth.getCurrentUser();
        UserUid = FUser.getUid();
        String path = UserUid + "/uId"; // הגעה ישירות למיקום הuId
        Query query = refWaitingUsers.orderByChild(path).equalTo(UserUid); //  לשנות את זה לrefUsers אחרי שאני מסדר את הרמות
        //מציאת מפתח ארגון
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    System.out.println(snapshot);
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        orgKeyId = snapshot1.getKey();
                        System.out.println(orgKeyId);
                        refUsers = refUsers.child(orgKeyId);
                        refWaitingUsers = refWaitingUsers.child(orgKeyId);
                    }

                }
                else{
                    System.out.println("There's no User like that");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };query.addListenerForSingleValueEvent(valueEventListener);

    }
}