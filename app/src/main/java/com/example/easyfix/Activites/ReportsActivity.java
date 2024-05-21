package com.example.easyfix.Activites;

import static android.content.ContentValues.TAG;
import static com.example.easyfix.FBref.orgKeyId;
import static com.example.easyfix.FBref.refReports;
import static com.example.easyfix.FBref.refUsers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfix.Adapters.ReportListAdapter;
import com.example.easyfix.FBref;
import com.example.easyfix.R;
import com.example.easyfix.Classes.Report;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReportsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String orgKey;
    ArrayList<Report> Reports = new ArrayList<Report>();
    RecyclerView ReportRv;
    ReportListAdapter repListAdapter;
    SharedPreferences sP;
    String UserUid;


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
        sP=getSharedPreferences("Remember",MODE_PRIVATE);

        // יצירת רפורט רנדומלי

       /* DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Organizations/" + "-Nw5X96KI_P4yGcxoN3L" + "/Reports");
        Report report = new Report(
                null, // reporter
                "There's a big issue in the bathroom of class 5, when I went there I saw lots of water flooding all over the place, please fix", // reportMainType
                1, // malfunctionArea (you can set this based on your requirements)
                String.valueOf(System.currentTimeMillis()), // timeReported (timestamp)
                "Please fix it as fast as possible" // extraInformation
        );
        reportsRef.child(String.valueOf(System.currentTimeMillis())).setValue(report); */
        Report rp = new Report();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser FUser = mAuth.getCurrentUser();
        UserUid = FUser.getUid();
        String path = UserUid + "/uId"; // הגעה ישירות למיקום הuId
        Query query = refUsers.orderByChild(path).equalTo(UserUid);
        //מציאת מפתח ארגון
        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(orgKeyId == null) { //במידה ולא null, המשתמש כבר נכנס לעמוד הזה ומצא את הkeyId
                    if (snapshot.exists()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            orgKey = snapshot1.getKey();
                            //DatabaseReference refReports = refOrganizations.child(orgKeyId + "/Reports");
                            FBref fbref = new FBref();
                            fbref.foundKeyId(orgKey);  // פעולה הממקדת את המצביעים בריל טיים למוסד הנכון למשתמש
                            refReports.addValueEventListener(repListener);
                        }

                    } else {
                        System.out.println("There's no User like that");
                    }
                }
                else{
                    refReports.addValueEventListener(repListener);
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


    public void Logout(View view) {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences.Editor editor=sP.edit();
        editor.putBoolean("doRemember", false); // לעדכן שהמשתמש יצא וצריך לשמור כניסה מחדש
        editor.apply();
        FBref fbref = new FBref();
        fbref.loggedOut();
        Intent intent = new Intent(ReportsActivity.this, LogInActivity.class);
        Toast.makeText(ReportsActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void addReport(View view) {
        ConstraintLayout successConstraintLayout = findViewById(R.id.reportDialogConstraintLayout);
        View dialogView = LayoutInflater.from(ReportsActivity.this).inflate(R.layout.report_dialog, successConstraintLayout);
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportsActivity.this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();

        Button Enter = dialogView.findViewById(R.id.reportEnter);
        EditText reportTitle = (EditText) dialogView.findViewById(R.id.repTitle);
        EditText reportDescription = (EditText) dialogView.findViewById(R.id.repDesc);

        Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Organizations/" + orgKeyId + "/Reports");
                DatabaseReference reportsRef = refReports;
                Report report = new Report(
                        UserUid, // reporter
                        reportTitle.getText().toString(), // reportMainType
                        1, // malfunctionArea (you can set this based on your requirements)
                        String.valueOf(System.currentTimeMillis()), // timeReported (timestamp)
                        reportDescription.getText().toString() // extraInformation
                );
                reportsRef.child(String.valueOf(System.currentTimeMillis())).setValue(report);
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}