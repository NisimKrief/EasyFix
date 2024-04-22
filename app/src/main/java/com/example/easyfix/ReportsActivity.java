package com.example.easyfix;

import static com.example.easyfix.FBref.FBDB;
import static com.example.easyfix.FBref.refOrganizations;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ReportsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reports);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // יצירת רפורט רנדומלי
        Report rp = new Report();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser FUser = mAuth.getCurrentUser();
        String UserUid = FUser.getUid();
        String path = "Users/" + UserUid + "/uId"; // הגעה ישירות למיקום הuId
        Query query = refOrganizations.orderByChild(path).equalTo(UserUid);
        query.addListenerForSingleValueEvent(valueEventListener);
    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists()){
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    System.out.println(snapshot1);
                    System.out.println(snapshot1.getValue());
                    Organization org = snapshot1.getValue(Organization.class);
                    System.out.println(org.toString());
                }
            }
            else{
                System.out.println("nah");
                System.out.println(snapshot.getValue());
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

}