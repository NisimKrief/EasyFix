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
import com.example.easyfix.Classes.Building;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private FirebaseAuth mAuth;
    EditText eTUser, eTPass, eTPass2, eTFullName;
    //String[] Mosad = {"נא לבחור מוסד: ", "הרב תחומי עמ'ל מקיף ז'", "מקיף א'", "מיקרוסופט" };
    ArrayList<Organization> Organizations = new ArrayList<Organization>();

    String[] BeforePicking = {"קודם לבחור מוסד בבקשה"};
    String[] Class = {"נא לבחור כיתה: ", "כיתה א'","כיתה ב'", "כיתה ג'", "כיתה ד'", "כיתה ה'", "כיתה ו'", "כיתה ז'", "כיתה ח'"
    , "כיתה ט'", "כיתה י'", "כיתה יא'", "כיתה יב'", "כיתה יג'", "כיתה יד'" };
    String[] WorkArea = {"נא לבחור אזור עבודה: ", "משרד", "כלכלה", "תכנות"};
    Spinner spinMosad;
    Spinner spinClass;
    String MosadSelected;
    int MosadIdSelected;
    String MosadStringId;
    String ClassSelected;
    int ClassIdSelected;
    ArrayAdapter<String> AdpMosad;
    ArrayAdapter<String> AdpMosadWorkArea;
    Calendar calendar;
    CheckBox rememberCheckBox;
    SharedPreferences sP;
    ProgressDialog pd;


    public void onStart() {
        super.onStart();
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
                            System.out.println(userLevel);
                            FBref fbref = new FBref();
                            fbref.foundKeyId(user); // Adjusting the references to the right organization, saving orgKeyId and user.
                            pd.dismiss();
                            if (userLevel == 1 || userLevel == 100) {
                                startActivity(new Intent(MainActivity.this, ReportsActivity.class));
                            } else {
                                startActivity(new Intent(MainActivity.this, LobbyActivity.class));
                            }
                        }

                    } else {
                        System.out.println("There's no User like that"); // User in waitingUsers
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            query.addListenerForSingleValueEvent(valueEventListener);
            startActivity(new Intent(MainActivity.this, ReportsActivity.class));
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
        AdpMosadWorkArea = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, WorkArea);
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
                Log.e(TAG, "Error fetching organizations", error.toException());


            }
        };
        refOrganizations.addListenerForSingleValueEvent(orgListener);

        // Add organization
        /*
        String keyId = refOrganizations.push().getKey();
        ArrayList<Building> OrganizationBuildings = new ArrayList<>();

        //adding the buildings and areas inside the organization

        ArrayList<String> str1 = new ArrayList<>();
        ArrayList<String> str2 = new ArrayList<>();
        ArrayList<String> str3 = new ArrayList<>();
        ArrayList<String> str4 = new ArrayList<>();
        ArrayList<String> str5 = new ArrayList<>();
        str1.add("שירותים 100");
        str1.add("שירותים 110");
        str1.add("חדר 101");
        str1.add("חדר 102");
        str1.add("חדר 103");
        str1.add("חדר 104");
        str1.add("חדר 105");
        str1.add("חדר 106");
        str1.add("חדר 107");
        str1.add("חדר 108");
        str1.add("חדר 109");
        str1.add("חדר 110");
        str1.add("חדר 111");
        str1.add("חדר 112");
        str1.add("חדר 113");
        str1.add("חדר 114");
        str1.add("חדר 115");
        str1.add("חדר 116");
        str1.add("חדר 117");
        str1.add("חדר 118");
        str1.add("חדר 119");
        str1.add("חדר 120");
        str1.add("חדר 121");
        str1.add("חדר 122");

        str2.add("שירותים 200");
        str2.add("שירותים 210");
        str2.add("חדר 201");
        str2.add("חדר 202");
        str2.add("חדר 203");
        str2.add("חדר 204");
        str2.add("חדר 205");
        str2.add("חדר 206");
        str2.add("חדר 207");
        str2.add("חדר 208");
        str2.add("חדר 209");
        str2.add("חדר 210");
        str2.add("חדר 211");
        str2.add("חדר 212");
        str2.add("חדר 213");
        str2.add("חדר 214");
        str2.add("חדר 215");
        str2.add("חדר 216");
        str2.add("חדר 217");
        str2.add("חדר 218");
        str2.add("חדר 219");
        str2.add("חדר 220");
        str2.add("חדר 221");
        str2.add("חדר 222");

        str3.add("שירותים 300");
        str3.add("שירותים 310");
        str3.add("חדר 301");
        str3.add("חדר 302");
        str3.add("חדר 303");
        str3.add("חדר 304");
        str3.add("חדר 305");
        str3.add("חדר 306");
        str3.add("חדר 307");
        str3.add("חדר 308");
        str3.add("חדר 309");
        str3.add("חדר 310");
        str3.add("חדר 311");
        str3.add("חדר 312");
        str3.add("חדר 313");
        str3.add("חדר 314");
        str3.add("חדר 315");
        str3.add("חדר 316");
        str3.add("חדר 317");
        str3.add("חדר 318");
        str3.add("חדר 319");
        str3.add("חדר 320");
        str3.add("חדר 321");
        str3.add("חדר 322");

        str4.add("שירותים 400");
        str4.add("שירותים 410");
        str4.add("חדר מורים 400");
        str4.add("חדר 401");
        str4.add("חדר 402");
        str4.add("חדר 403");
        str4.add("חדר 404");
        str4.add("חדר 405");
        str4.add("חדר 406");
        str4.add("חדר 407");
        str4.add("חדר 408");
        str4.add("חדר 409");
        str4.add("חדר 410");
        str4.add("חדר 411");
        str4.add("חדר 412");
        str4.add("חדר 413");
        str4.add("חדר 414");
        str4.add("חדר 415");
        str4.add("חדר 416");
        str4.add("חדר 417");
        str4.add("חדר 418");
        str4.add("חדר 419");
        str4.add("חדר 420");
        str4.add("חדר 421");
        str4.add("חדר 422");

        str5.add("שירותים 500");
        str5.add("חדר מורים 500");
        str5.add("לובי 500");
        str5.add("חדר מנהלת");
        str5.add("ניהול כספים ");
        str5.add("יזהר כהן");

        OrganizationBuildings.add(new Building("בניין 100", str1));
        OrganizationBuildings.add(new Building("בניין 200", str2));
        OrganizationBuildings.add(new Building("בניין 300", str3));
        OrganizationBuildings.add(new Building("בניין 400", str4));
        OrganizationBuildings.add(new Building("בניין 500", str5));
        System.out.println(OrganizationBuildings);

        // adding the classes or the work areas

        ArrayList<String> classOrWorkArea = new ArrayList<>();
        classOrWorkArea.add("כיתה ז'");
        classOrWorkArea.add("כיתה ח'");
        classOrWorkArea.add("כיתה ט'");
        classOrWorkArea.add("כיתה י'");
        classOrWorkArea.add("כיתה יא'");
        classOrWorkArea.add("כיתה יב'");

        Organization test = new Organization("עמל רמות באר שבע מקיף ז'", "keyId", OrganizationBuildings, classOrWorkArea);
        FBref.refOrganizations.child("keyId").setValue(test).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Organization added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to add organization", Toast.LENGTH_SHORT).show();
                }
            }
        });
        */

        // Add Report
        /*
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Organizations/" + "-Nw5X96KI_P4yGcxoN3L" + "/Reports");
        Report report = new Report(
                null, // reporter
                "Default Report", // reportMainType
                1, // malfunctionArea (you can set this based on your requirements)
                String.valueOf(System.currentTimeMillis()), // timeReported (timestamp)
                "No additional information" // extraInformation
        );
        reportsRef.child(String.valueOf(System.currentTimeMillis())).setValue(report); */

    }
    public void Register(View view) {
        pd = ProgressDialog.show(this, "Trying to register...", "", true);
        String Email = eTUser.getText().toString();
        String Password = eTPass.getText().toString();
        int checkFields = checkFields(); // the count is how many fields are missing, will only proceed if all fields are filled.
        // if spinClass.getSelectemItem == קודם לבחור מוסד בבקשה it means its a not updated organization. (organization without classOrWorkArea
        if (checkFields == 0 &&Password.equals(eTPass2.getText().toString()) && spinMosad.getSelectedItemPosition() != 0 && !spinClass.getSelectedItem().equals("קודם לבחור מוסד בבקשה")) {
            //Toast.makeText(MainActivity.this, "Password Not Good Enough.", Toast.LENGTH_SHORT).show();
            mAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser fUser = mAuth.getCurrentUser();
                                String uId = fUser.getUid();
                                User user = new User(MosadStringId, uId, eTFullName.getText().toString(), calculateLastYear((String) spinClass.getSelectedItem()), ClassIdSelected);
                                if (user.getUserName().equals("AdminNisimDoronKrief")) {
                                    user.setUserLevel(10000);
                                    refUsers.child(user.getKeyId()).child(uId).setValue(user);
                                } else {
                                    refWaitingUsers.child(user.getKeyId()).child(uId).setValue(user);
                                }
                                SharedPreferences.Editor editor = sP.edit();
                                editor.putBoolean("doRemember", rememberCheckBox.isChecked());
                                editor.apply();
                                pd.dismiss();
                                startActivity(new Intent(MainActivity.this, LobbyActivity.class)); // להעביר אותם למסך/לבקש מהם לחכות שיאשרו אותם.
                                Log.d(TAG, "RegisterWithEmailAndPassword:success");
                                Toast.makeText(MainActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();

                            } else {
                                pd.dismiss();
                                Log.w(TAG, "RegisterWithCEmailAndPassword:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Email Already Used", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner) { // אם משתמשים בספינר של מוסד
            if (position != 0) {
                Organization selectedOrg = Organizations.get(position);
                MosadSelected = selectedOrg.getOrganizationName();
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

    public void LogInScreen(View view) {
        startActivity(new Intent(MainActivity.this, LogInActivity.class));
    }
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
    // Method to get the last year based on the current class
    public int calculateLastYear(String currentClass) {
        // Map classes to their corresponding grade levels
        Map<String, Integer> classToGradeMap = new HashMap<>();
        classToGradeMap.put("כיתה א'", 1);
        classToGradeMap.put("כיתה ב'", 2);
        classToGradeMap.put("כיתה ג'", 3);
        classToGradeMap.put("כיתה ד'", 4);
        classToGradeMap.put("כיתה ה'", 5);
        classToGradeMap.put("כיתה ו'", 6);
        classToGradeMap.put("כיתה ז'", 7);
        classToGradeMap.put("כיתה ח'", 8);
        classToGradeMap.put("כיתה ט'", 9);
        classToGradeMap.put("כיתה י'", 10);
        classToGradeMap.put("כיתה יא'", 11);
        classToGradeMap.put("כיתה יב'", 12);

        // Get the current year
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        // Get the grade level of the current class
        Integer gradeLevel = classToGradeMap.get(currentClass);

        if (gradeLevel != null) {
            // Calculate the remaining years to complete 12 years of schooling
            int remainingYears = 12 - gradeLevel;
            // Calculate the last year
            return currentYear + remainingYears;
        } else {
            //the case where the class is not found in the map
            throw new IllegalArgumentException("Invalid class name: " + currentClass);
        }
    }


}

