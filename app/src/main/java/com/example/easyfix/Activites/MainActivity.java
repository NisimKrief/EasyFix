package com.example.easyfix.Activites;

import static android.content.ContentValues.TAG;

import static com.example.easyfix.FBref.refOrganizations;
import static com.example.easyfix.FBref.refUsers;
import static com.example.easyfix.FBref.refWaitingUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.example.easyfix.Adapters.ArrayAdapterOrganization;
import com.example.easyfix.Classes.Organization;
import com.example.easyfix.FBref;
import com.example.easyfix.R;
import com.example.easyfix.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * The Main activity (The first activity).
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	08/02/2024
 * Users can register by providing necessary details and selecting an organization and class.
 * The registration process checks for existing usernames to avoid duplicates.
 * If the "Remember Me" checkbox is checked, the user remains logged in across sessions.
 * Organizations and classes are populated from a Firebase Realtime Database.
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private FirebaseAuth mAuth;
    /**
     * The EditText for the Email.
     */
    EditText eTUser,
    /**
     * The EditText for the password.
     */
    eTPass,
    /**
     * The EditText for confirming the password.
     */
    eTPass2,
    /**
     * The EditText for the full name.
     */
    eTFullName;
    /**
     * List of available organizations.
     */
    ArrayList<Organization> Organizations = new ArrayList<Organization>();

    /**
     * Default message shown before picking an organization.
     */
    String[] BeforePicking = {"קודם לבחור מוסד בבקשה"};
    /**
     * Spinner for selecting an organization.
     */
    Spinner spinMosad;
    /**
     * Spinner for selecting a class.
     */
    Spinner spinClass;
    /**
     * The Mosad id selected.
     */
    int MosadIdSelected;
    /**
     * Selected organization keyID.
     */
    String MosadStringId;
    /**
     * Array adapter for organizations.
     */
    ArrayAdapter<String> AdpMosad;
    /**
     * Calendar instance for date-related operations.
     */
    Calendar calendar;
    /**
     * Checkbox for remembering the user.
     */
    CheckBox rememberCheckBox;
    /**
     * SharedPreferences for storing the checkbox.
     */
    SharedPreferences sP;
    /**
     * ProgressDialog for showing loading indicators.
     */
    ProgressDialog pd;

    /**
     * Called when the app is opened.
     * Checks if the user is already logged in and proceeds accordingly.
     * if the user is logged in but still in waiting users (still not accepted)
     * he will not proceed and be asked to wait till he get accepted.
     */
    public void onStart() {
        super.onStart();
        FBref.checkInternetConnection(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean isChecked = sP.getBoolean("doRemember", false);
        if(currentUser != null && isChecked) {
            pd = ProgressDialog.show(this, "Logging In...", "",true);
            String UserUid = currentUser.getUid();
            String path = UserUid + "/uId"; // Path to the uId
            Query query = refUsers.orderByChild(path).equalTo(UserUid); // Finding the organization the user is in.
            ValueEventListener valueEventListener = new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String orgKey = snapshot1.getKey();
                            User user = snapshot1.child(UserUid).getValue(User.class);
                            int userLevel = user.getUserLevel();
                            FBref fbref = new FBref();
                            fbref.foundKeyId(user); // Adjusting the references to the right organization, saving user.
                            pd.dismiss();
                            startActivity(new Intent(MainActivity.this, ReportsActivity.class));
                        }

                    } else {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "Still waiting, please ask your teacher to accept you.", Toast.LENGTH_LONG).show(); // User in waitingUsers
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            query.addListenerForSingleValueEvent(valueEventListener);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Report report = new Report();
        mAuth = FirebaseAuth.getInstance();
        eTUser = (EditText) findViewById(R.id.eTUser);
        eTPass = (EditText) findViewById(R.id.eTPass);
        eTPass2 = (EditText) findViewById(R.id.eTCnfPass);
        eTFullName = (EditText) findViewById(R.id.eTFullName);
        spinMosad = (Spinner) findViewById(R.id.spinner);
        spinClass = (Spinner) findViewById(R.id.spinnerRooms);
        rememberCheckBox = (CheckBox)findViewById(R.id.checkBox2);
        ArrayAdapter<String> adpBeforePicking = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, BeforePicking);
        spinMosad.setOnItemSelectedListener(this);
        spinClass.setAdapter(adpBeforePicking);
        spinClass.setOnItemSelectedListener(this);
        calendar = Calendar.getInstance();
        pd = ProgressDialog.show(this, "Loading Organizations...", "", true);
        sP =getSharedPreferences("Remember",MODE_PRIVATE);

        // Adding all the available organizations to the spinner.
        ValueEventListener orgListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                Organizations.clear();
                Organizations.add(new Organization("נא לבחור מוסד")); // Hint organization, will tell the users to pick an organization, unpickable.
                for(DataSnapshot data : dS.getChildren()){
                    Organization Org = data.getValue(Organization.class);
                    Organizations.add(Org);
                }
                spinMosad.setAdapter(new ArrayAdapterOrganization(MainActivity.this, Organizations));
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FBref.checkInternetConnection(MainActivity.this);
                Log.e(TAG, "Error fetching organizations", error.toException());


            }
        };
        refOrganizations.addListenerForSingleValueEvent(orgListener);

    }

    /**
     * Attempts to register a new user.
     * Validates user input and checks for existing usernames.
     * if user input is correct, should call "checkIfUserNameExists" method to check if there's a user with the same username
     *
     * @param view The view that was clicked.
     */
    public void Register(View view) {
        pd = ProgressDialog.show(this, "Trying to register...", "", true);
        int checkFields = checkFields(); // the count is how many fields are missing, will only proceed if all fields are filled.
        // if spinClass.getSelectemItem == "קודם לבחור מוסד בבקשה" it means its a not updated organization. (organization without classOrWorkArea
        if (checkFields == 0 &&eTPass.getText().toString().equals(eTPass2.getText().toString()) && spinMosad.getSelectedItemPosition() != 0 && !spinClass.getSelectedItem().equals("קודם לבחור מוסד בבקשה")) {
            //if all fields are filled, it should check if there's a user registered with this name already, if there's he should change his username
            //but if there isn't let him proceed to the registering.
            checkIfUserNameExists();
        } else {
            pd.dismiss();
            if(spinMosad.getSelectedItemPosition() == 0 || spinClass.getSelectedItem().equals("קודם לבחור מוסד בבקשה")){
                Toast.makeText(MainActivity.this, "Pick an Organization" +"\n" + "חובה לבחור מוסד וכיתה", Toast.LENGTH_SHORT).show();
            }
            if(checkFields != 0)
                Toast.makeText(MainActivity.this, "Must Fill All Fields First", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "Passwords Not Equal", Toast.LENGTH_SHORT).show();
    }

    }

    /**
     * Proceeds with the user registration process after validating user input.
     * Creates a new user account with the provided email and password using Firebase Authentication.
     * If the registration is successful, adds the user to 'WaitingUsers' node in the Firebase Realtime Database
     * Displays appropriate messages and handles errors accordingly.
     */
    public void registerProceed(){
        String Email = eTUser.getText().toString();
        String Password = eTPass.getText().toString();
        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser fUser = mAuth.getCurrentUser();
                            String uId = fUser.getUid();
                            User user = new User(MosadStringId, uId, eTFullName.getText().toString().trim(), calculateLastYear((String) spinClass.getSelectedItem()));
                            if (user.getUserName().equals("AdminNisimDoronKrief")) {
                                //adding an admin properties for faster registering and accepting users when new organization is created.
                                user.setUserLevel(10000);
                                refUsers.child(user.getKeyId()).child(uId).setValue(user);
                            } else {
                                refWaitingUsers.child(user.getKeyId()).child(uId).setValue(user);
                            }
                            SharedPreferences.Editor editor = sP.edit();
                            editor.putBoolean("doRemember", rememberCheckBox.isChecked());
                            editor.apply();
                            pd.dismiss();
                            Log.d(TAG, "RegisterWithEmailAndPassword:success");
                            Toast.makeText(MainActivity.this, "Registered successfully! ", Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Wait till you get accepted in order to continue ", Toast.LENGTH_LONG).show();

                        } else {
                            pd.dismiss();
                            Log.w(TAG, "RegisterWithCEmailAndPassword:failure", task.getException());
                            FBref.checkInternetConnection(MainActivity.this);
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    /**
     * Handles the item selection event for spinners.
     * Updates the available classes based on the selected organization.
     *
     * @param parent The AdapterView where the selection happened.
     * @param view The view within the AdapterView that was clicked.
     * @param position The position of the view in the adapter.
     * @param id The row id of the item that is selected.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner) { // אם משתמשים בספינר של מוסד
            if (position != 0) {
                Organization selectedOrg = Organizations.get(position);
                MosadStringId = String.valueOf(selectedOrg.getKeyId());
                if(selectedOrg.getClassesOrWorkArea() != null) {
                    AdpMosad = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, selectedOrg.getClassesOrWorkArea());
                    spinClass.setAdapter(AdpMosad);
                }
                else{
                    Toast.makeText(MainActivity.this, "This Organization isn't updated and can't be used", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Log in screen.
     *
     * @param view the view
     */
    public void LogInScreen(View view) {
        startActivity(new Intent(MainActivity.this, LogInActivity.class));
    }

    /**
     * Validates user input fields to ensure they are not empty.
     *
     * @return The count of empty fields.
     */
    public int checkFields(){
        int count = 0;
        if(eTUser.getText().toString().isEmpty()){
            eTUser.setError("This field is required");
            count++;
        }
        if(eTPass.getText().toString().isEmpty()){
            eTPass.setError("This field is required");
            count++;
        }
        if(eTPass2.getText().toString().isEmpty()){
            eTPass2.setError("This field is required");
            count++;
        }
        if(eTFullName.getText().toString().isEmpty()){
            eTFullName.setError("This field is required");
            count++;
        }
        return count;
    }

    /**
     * Calculates the last year of schooling based on the current class.
     * that the user chose from the spinner
     *
     * @param currentClass The current class of the user.
     * @return The last year of schooling.
     */
// Method to get the last year based on the current class
    public int calculateLastYear(String currentClass) {
        int gradeLevel;

        switch (currentClass) {
            case "כיתה א'":
                gradeLevel = 1;
                break;
            case "כיתה ב'":
                gradeLevel = 2;
                break;
            case "כיתה ג'":
                gradeLevel = 3;
                break;
            case "כיתה ד'":
                gradeLevel = 4;
                break;
            case "כיתה ה'":
                gradeLevel = 5;
                break;
            case "כיתה ו'":
                gradeLevel = 6;
                break;
            case "כיתה ז'":
                gradeLevel = 7;
                break;
            case "כיתה ח'":
                gradeLevel = 8;
                break;
            case "כיתה ט'":
                gradeLevel = 9;
                break;
            case "כיתה י'":
                gradeLevel = 10;
                break;
            case "כיתה יא'":
                gradeLevel = 11;
                break;
            case "כיתה יב'":
                gradeLevel = 12;
                break;
            default:
        }
                // Handle cases where the class name is not in cases
                // can add more cases based on the organizations.
                gradeLevel = 12; // Or any value to indicate an invalid class
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        // Calculate the remaining years to complete 12 years of schooling
            int remainingYears = 12 - gradeLevel;
            // Calculate the last year
            return currentYear + remainingYears;

    }
    /**
     * Called when the back button is pressed.
     * Overrides the default behavior to prevent navigating back after logging out.
     */
    public void onBackPressed() {
        // So you can't go back after logging out, which may crash the application
        //super.onBackPressed(); // Comment this out to disable the back button
    }

    /**
     * checks for existing usernames in the organization, if there's user with the same username (in waiting users and current users), will make a toast and set an error
     * if there isn't a user with the same username, will continue and call "registerProceed" method to continue the registration
     */
    public void checkIfUserNameExists(){
        Query queryUsers = refUsers.child(MosadStringId).orderByChild("userName").equalTo(eTFullName.getText().toString().trim());
        Query queryWaitingUsers = refWaitingUsers.child(MosadStringId).orderByChild("userName").equalTo(eTFullName.getText().toString().trim());
        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    //there's no user in the organization with that name, let him proceed.
                    ValueEventListener valueEventListener2 = new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                //there's no waiting user in the organization with that name, let him proceed.
                                System.out.println("No user with the name ");
                                registerProceed();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "User with the same username exists, Please change your username", Toast.LENGTH_SHORT).show();
                                eTFullName.setError("User with the same username exists, Please change your username");
                                pd.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    queryWaitingUsers.addListenerForSingleValueEvent(valueEventListener2);
                    System.out.println("No user with the name ");
                }
                else{
                    Toast.makeText(MainActivity.this, "User with the same username exists, Please change your username", Toast.LENGTH_SHORT).show();
                    eTFullName.setError("User with the same username exists, Please change your username");
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        queryUsers.addListenerForSingleValueEvent(valueEventListener);
    }
}

