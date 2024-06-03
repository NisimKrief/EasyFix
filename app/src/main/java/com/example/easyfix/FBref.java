package com.example.easyfix;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

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
    public static DatabaseReference refReportsDeleted = FBDB.getReference("ReportsDeleted");
    public static User currentUser; // I use the user a big amount of times so I prefer to save it manually.

    private static AlertDialog alertDialog;
    public void foundKeyId(User user){
        this.currentUser = user;
        refOrganizations = refOrganizations.child(currentUser.getKeyId());
        refUsers = refUsers.child(currentUser.getKeyId());
        refWaitingUsers = refWaitingUsers.child(currentUser.getKeyId());
        refReports = refReports.child(currentUser.getKeyId());
        refReportsDone = refReportsDone.child(currentUser.getKeyId());
        refReportsDeleted = refReportsDeleted.child(currentUser.getKeyId());
    }
    public void loggedOut(){ //.When user logs out, update the firebase references so he will not log in to the previous organization.
        refOrganizations = FBDB.getReference("Organizations");
        refUsers = FBDB.getReference("Users");
        refWaitingUsers = FBDB.getReference("WaitingUsers");
        refReports = FBDB.getReference("Reports");
        refReportsDone = FBDB.getReference("ReportsDone");
        refReportsDeleted = FBDB.getReference("ReportsDone");
        currentUser = null;
    }
    public static void checkInternetConnection(Context context) {
        //Checking if the user has Internet, if not, should show alertDialog.
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork  != null && activeNetwork.isConnected()) {
                // there's internet connection, dismiss any existing dialog
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
            } else {
                // there's no internet connection, show alertdialog
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("No Internet Connection");
                builder.setMessage("Please connect to the internet before proceeding.");
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Dismiss the dialog and check internet connection again
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
                            alertDialog = null;
                        }
                        checkInternetConnection(context);
                    }
                });


                alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

}
