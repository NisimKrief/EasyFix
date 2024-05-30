package com.example.easyfix;

import com.example.easyfix.Classes.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBref {
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refOrganizations = FBDB.getReference("Organizations");
    public static DatabaseReference refUsers = FBDB.getReference("Users");
    public static DatabaseReference refWaitingUsers = FBDB.getReference("WaitingUsers");
    public static DatabaseReference refReports = FBDB.getReference("Reports");
    public static DatabaseReference refReportsDone = FBDB.getReference("ReportsDone");
    public static String orgKeyId;
    public static User currentUser; // I use the userLevel alot so I prefer to save it manually.

    public void foundKeyId(String orgKeyId, User user){
        this.orgKeyId = orgKeyId;
        this.currentUser = user;
        refOrganizations = refOrganizations.child(orgKeyId);
        refUsers = refUsers.child(orgKeyId);
        refWaitingUsers = refWaitingUsers.child(orgKeyId);
        refReports = refReports.child(orgKeyId);
        refReportsDone = refReportsDone.child(orgKeyId);
    }
    public void loggedOut(){ //.When user logs out, update the firebase references so he will not log in to the previous organization.
        refOrganizations = FBDB.getReference("Organizations");
        refUsers = FBDB.getReference("Users");
        refWaitingUsers = FBDB.getReference("WaitingUsers");
        refReports = FBDB.getReference("Reports");
        refReportsDone = FBDB.getReference("ReportsDone");
        orgKeyId = "";
    }

}
