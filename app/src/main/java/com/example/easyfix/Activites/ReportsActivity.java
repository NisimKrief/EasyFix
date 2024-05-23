package com.example.easyfix.Activites;

import static android.content.ContentValues.TAG;
import static com.example.easyfix.FBref.orgKeyId;
import static com.example.easyfix.FBref.refOrganizations;
import static com.example.easyfix.FBref.refReports;
import static com.example.easyfix.FBref.refUsers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import com.example.easyfix.Adapters.ArrayAdapterBuilding;
import com.example.easyfix.Adapters.ArrayAdapterOrganization;
import com.example.easyfix.Adapters.ReportListAdapter;
import com.example.easyfix.Classes.Building;
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
    ArrayList<Building> Buildings = new ArrayList<Building>();
    RecyclerView ReportRv;
    ReportListAdapter repListAdapter;
    SharedPreferences sP;
    Spinner spinBuilding;
    String UserUid;
    String reporter;
    ProgressDialog pd;


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
        pd = ProgressDialog.show(this, "Loading Reports...", "",true);
        ReportRv=findViewById(R.id.repListRv);
        repListAdapter=new ReportListAdapter(Reports, Buildings);
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
                    refOrganizations.child("organizationBuildings").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Buildings.clear();
                            ArrayList<String> hintRooms = new ArrayList<>();
                            hintRooms.add("נא לבחור קודם את אזור התקלה");
                            Buildings.add(new Building("נא לבחור את אזור התקלה", hintRooms));
                            hintRooms = null;
                            for(DataSnapshot data : snapshot.getChildren()){
                                Buildings.add(data.getValue(Building.class));
                            }
                            System.out.println(Buildings);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    refUsers.child(UserUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            reporter = snapshot.child("userName").getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    refReports.addValueEventListener(repListener);
                }
            }
            ValueEventListener repListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dS) {
                    pd.dismiss();
                    pd = ProgressDialog.show(ReportsActivity.this, "Updating Reports...", "",true);
                    Reports.clear();
                    for(DataSnapshot data : dS.getChildren()){
                        Report rep = data.getValue(Report.class);
                        Reports.add(rep);
                    }
                    ReportRv.setAdapter(repListAdapter);
                    pd.dismiss();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error fetching reports", error.toException());
                    pd.dismiss();

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
        fbref.loggedOut(); // כאשר משתמש יוצא, לאתחל את מצביעי הפיירבייס כך שאם יתחבר למוסד אחר לא יופיע את המוסד הקודם.
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
        spinBuilding = (Spinner) dialogView.findViewById(R.id.spinnerBuildings);
        Spinner spinRooms = (Spinner) dialogView.findViewById(R.id.spinnerRooms);
        spinBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinRooms.setAdapter(new ArrayAdapter<String>(ReportsActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Buildings.get(position).getRooms()));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinBuilding.setAdapter(new ArrayAdapterBuilding(this, Buildings));
        Button Enter = dialogView.findViewById(R.id.reportEnter);
        EditText reportTitle = (EditText) dialogView.findViewById(R.id.repTitle);
        EditText reportDescription = (EditText) dialogView.findViewById(R.id.repDesc);

        Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // לעשות שאם אין נתונים כגון כותרת התקלה, אזור התקלה וכו, להעיר למשתמש.
                //DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Organizations/" + orgKeyId + "/Reports");
                DatabaseReference reportsRef = refReports;
                Report report = new Report(
                        reporter, // reporter
                        reportTitle.getText().toString(), // reportMainType
                        spinBuilding.getSelectedItemPosition() - 1, // malfunctionArea (you can set this based on your requirements) (מחסירים באחד כי יש את הראשון שהוא דמה)
                        spinRooms.getSelectedItemPosition(), // malfunctionRoom
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