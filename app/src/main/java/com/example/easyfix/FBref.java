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

/**
 * Fbref class
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	21/04/2024
 * helpful class, has almost all the firebase realtime database references, has methods to indicate the references to the user specific organization and user
 * also has methods that get called multiple time like "checkInternetConnection" that help to shorten the existing code, when user logs in, all the
 * references get their child with the organization keyId as well as saving the current user information, when logging out reseting all the references
 * and the user so if user logs to a different organization it will indicate his user and references to the new organization.
 */
public class FBref {
    /**
     * The constant FBDB represents the Firebase Realtime Database instance.
     */
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    /**
     * The constant refOrganizations points to the "Organizations" node in the database.
     */
    public static DatabaseReference refOrganizations = FBDB.getReference("Organizations");
    /**
     * The constant refUsers points to the "Users" node in the database.
     */
    public static DatabaseReference refUsers = FBDB.getReference("Users");
    /**
     * The constant refWaitingUsers points to the "WaitingUsers" node in the database.
     */
    public static DatabaseReference refWaitingUsers = FBDB.getReference("WaitingUsers");
    /**
     * The constant refReports points to the "Reports" node in the database.
     */
    public static DatabaseReference refReports = FBDB.getReference("Reports");
    /**
     * The constant refReportsDone points to the "ReportsDone" node in the database.
     */
    public static DatabaseReference refReportsDone = FBDB.getReference("ReportsDone");
    /**
     * The constant refReportsDeleted points to the "ReportsDeleted" node in the database.
     */
    public static DatabaseReference refReportsDeleted = FBDB.getReference("ReportsDeleted");
    /**
     * The constant currentUser holds the current user's information.
     */
    public static User currentUser; // I use the user a big amount of times so I prefer to save it manually.

    private static AlertDialog alertDialog;

    /**
     * Updates Firebase references to point to the current user's organization.
     * This method is called when a user logs in and sets the Firebase references to the user's organization.
     *
     * @param user the current user
     */
    public void foundKeyId(User user){
        this.currentUser = user;
        refOrganizations = refOrganizations.child(currentUser.getKeyId());
        refUsers = refUsers.child(currentUser.getKeyId());
        refWaitingUsers = refWaitingUsers.child(currentUser.getKeyId());
        refReports = refReports.child(currentUser.getKeyId());
        refReportsDone = refReportsDone.child(currentUser.getKeyId());
        refReportsDeleted = refReportsDeleted.child(currentUser.getKeyId());
    }

    /**
     * Resets Firebase references to their original nodes.
     * This method is called when a user logs out, resetting the Firebase references and user information.
     */
    public void loggedOut(){ //.When user logs out, update the firebase references so he will not log in to the previous organization.
        refOrganizations = FBDB.getReference("Organizations");
        refUsers = FBDB.getReference("Users");
        refWaitingUsers = FBDB.getReference("WaitingUsers");
        refReports = FBDB.getReference("Reports");
        refReportsDone = FBDB.getReference("ReportsDone");
        refReportsDeleted = FBDB.getReference("ReportsDone");
        currentUser = null;
    }

    /**
     * Checks the internet connection and shows an alert dialog if there is no internet connection.
     * This method helps ensure that the user is connected to the internet before proceeding with certain operations.
     * if user doesn't have internet connection, an alertdialog will pop out that will tell him to connect to internet
     * the alertdialog has a success button "Ok" if the user press it and still doesn't have internet connection the method will play again
     * it loops till he has internet connection and can continue using the application.
     * if he has internet the alertdialog will disappear without calling the method again.
     * @param context the context (the activity that the alertdialog will pop in)
     */
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
