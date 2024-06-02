package com.example.easyfix.Activites;

import static android.content.ContentValues.TAG;
import static com.example.easyfix.FBref.currentUser;
import static com.example.easyfix.FBref.refReportsDone;
import static com.example.easyfix.FBref.refUsers;

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

import com.example.easyfix.Adapters.UsersListAdapter;
import com.example.easyfix.Classes.User;
import com.example.easyfix.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ManageUsersListActivity extends AppCompatActivity {
    RecyclerView UsersRv;
    UsersListAdapter UsersListAdapter;
    String UserUid;
    ArrayList<User> Users = new ArrayList<User>();
    int userLevel;
    ProgressDialog pd;
    Query higherUserLevel;
    SearchView searchUsers;

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
        pd = ProgressDialog.show(this, "Finding Users...", "",true);
        UsersRv =findViewById(R.id.usersListRv);
        searchUsers = (SearchView) findViewById(R.id.usersSearchView);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        UsersRv.setLayoutManager(layoutManager);
        UserUid = currentUser.getuId();
        UsersListAdapter=new UsersListAdapter(Users);
        ValueEventListener ManageUsersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                System.out.println(dS);
                Users.clear();
                for(DataSnapshot data : dS.getChildren()){
                    User user = data.getValue(User.class);
                    if(currentUser.getUserLevel() > user.getUserLevel())
                        Users.add(user);
                }
                Collections.reverse(Users); // so that the higher user levels will show first
                UsersRv.setAdapter(UsersListAdapter);
                pd.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error fetching users", error.toException());
                    pd.dismiss();
            }
        };
        searchUsers.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                higherUserLevel.removeEventListener(ManageUsersListener);
                higherUserLevel = null;
                if(newText.isEmpty()){
                    higherUserLevel = refUsers.orderByChild("userLevel").endAt(currentUser.getUserLevel() -1); // Query so a user will see only the users lower leveled than them.
                    higherUserLevel.addValueEventListener(ManageUsersListener);
                }
                else
                    higherUserLevel = refUsers.orderByChild("userName").startAt(newText).endAt(newText + "\uf8ff");
                System.out.println(newText);
                higherUserLevel.addValueEventListener(ManageUsersListener);
                return true;
            }
        });
        if(searchUsers.getQuery().toString().isEmpty()){
            higherUserLevel = refUsers.orderByChild("userLevel").endAt(currentUser.getUserLevel() -1); // Query so a user will see only the users lower leveled than them.
            higherUserLevel.addValueEventListener(ManageUsersListener);
        }


    }

}