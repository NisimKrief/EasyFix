package com.example.easyfix.Activites;

import static android.content.ContentValues.TAG;
import static com.example.easyfix.FBref.orgKeyId;
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

public class UsersListActivity extends AppCompatActivity {
    RecyclerView UsersRv;
    WaitingUsersListAdapter waitingUsersListAdapter;
    String UserUid;
    private FirebaseAuth mAuth;
    ArrayList<User> Users = new ArrayList<User>();
    String orgKey;
    int lastYear;

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
        waitingUsersListAdapter=new WaitingUsersListAdapter(Users);
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
                if(orgKeyId == null) { //במידה ולא null, המשתמש כבר נכנס לעמוד הזה ומצא את הkeyId
                    if (snapshot.exists()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            orgKey = snapshot1.getKey();
                            FBref fbref = new FBref();
                            fbref.foundKeyId(orgKey); // פעולה הממקדת את המצביעים בריל טיים למוסד הנכון למשתמש
                        }


                    } else {
                        System.out.println("There's no User like that");
                    }
                }
                else{
                    refUsers.child(UserUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            lastYear = snapshot.child("lastYear").getValue(Integer.class);
                            Query sameLastYear = refWaitingUsers.orderByChild("lastYear").equalTo(lastYear); // סינון לפי שכבה, יופיעו רק משתמשים באותה השכבה
                            sameLastYear.addValueEventListener(waitingUsersListener);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
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
                    UsersRv.setAdapter(waitingUsersListAdapter);
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