package com.example.easyfix;

import static android.content.ContentValues.TAG;
import static com.example.easyfix.FBref.FBDB;
import static com.example.easyfix.FBref.refOrganizations;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReportsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String orgKeyId;
    ArrayList<Report> Reports = new ArrayList<Report>();
    RecyclerView ReportRv;
    ReportListAdapter repListAdapter;

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
        ReportRv=findViewById(R.id.repListRv);
        repListAdapter=new ReportListAdapter(Reports);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        ReportRv.setLayoutManager(layoutManager);

        // יצירת רפורט רנדומלי

        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Organizations/" + "-Nw5X96KI_P4yGcxoN3L" + "/Reports");
        Report report = new Report(
                null, // reporter
                "There's a big issue in the bathroom of class 5, when I went there I saw lots of water flooding all over the place, please fix", // reportMainType
                1, // malfunctionArea (you can set this based on your requirements)
                String.valueOf(System.currentTimeMillis()), // timeReported (timestamp)
                "Please fix it as fast as possible" // extraInformation
        );
        reportsRef.child(String.valueOf(System.currentTimeMillis())).setValue(report);
        Report rp = new Report();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser FUser = mAuth.getCurrentUser();
        String UserUid = FUser.getUid();
        String path = "Users/" + UserUid + "/uId"; // הגעה ישירות למיקום הuId
        Query query = refOrganizations.orderByChild(path).equalTo(UserUid);
        //מציאת מפתח ארגון
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        orgKeyId = snapshot1.getKey();
                        System.out.println(orgKeyId);
                        DatabaseReference refReports = refOrganizations.child(orgKeyId + "/Reports");
                        refReports.addListenerForSingleValueEvent(repListener);
                    }

                }
                else{
                    System.out.println("There's no User like that");
                }
            }
            ValueEventListener repListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dS) {
                    Reports.clear();
                    for(DataSnapshot data : dS.getChildren()){
                        Report rep = data.getValue(Report.class);
                        Reports.add(rep);
                    }
                    System.out.println(Reports);
                    ReportRv.setAdapter(repListAdapter);
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