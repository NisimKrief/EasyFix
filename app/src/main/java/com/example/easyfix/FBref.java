package com.example.easyfix;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBref {
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refOrganizations = FBDB.getReference("Organizations");
    public static DatabaseReference refUsers = FBDB.getReference("Users");
    public static DatabaseReference refWaitingUsers = FBDB.getReference("WaitingUsers");
    public static DatabaseReference refReports = FBDB.getReference("Reports");
    public static DatabaseReference refReportsDone = FBDB.getReference("ReportsDone");

}
