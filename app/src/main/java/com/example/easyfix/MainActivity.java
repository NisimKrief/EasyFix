package com.example.easyfix;

import static android.content.ContentValues.TAG;

import static com.example.easyfix.FBref.refOrganizations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private FirebaseAuth mAuth;
    EditText eTUser, eTPass;
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
    ArrayAdapter<String> AdpMosadClass;
    ArrayAdapter<String> AdpMosadWorkArea;
    Calendar calendar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Report report = new Report();
        mAuth = FirebaseAuth.getInstance();
        eTUser = (EditText) findViewById(R.id.eTUser);
        eTPass = (EditText) findViewById(R.id.eTPass);
        spinMosad = (Spinner) findViewById(R.id.spinner);
        spinClass = (Spinner) findViewById(R.id.spinner2);
        //ArrayAdapter<String> adpMosad = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Mosad);
        ArrayAdapter<String> adpBeforePicking = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, BeforePicking);
        AdpMosadClass = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Class);
        AdpMosadWorkArea = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, WorkArea);
        spinMosad.setOnItemSelectedListener(this);
        spinClass.setAdapter(adpBeforePicking);
        spinClass.setOnItemSelectedListener(this);
        calendar = Calendar.getInstance();

        // הוספת כל המוסדים הקיימים לספינר של בחירת מוסדות
        ValueEventListener orgListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                Organizations.clear();
                for(DataSnapshot data : dS.getChildren()){
                    Organization Org = data.getValue(Organization.class);
                    Organizations.add(Org);
                }
                spinMosad.setAdapter(new ArrayAdapterOrganization(MainActivity.this, Organizations));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching organizations", error.toException());


            }
        };
        refOrganizations.addListenerForSingleValueEvent(orgListener);

        /* Add organization
        String keyId = refOrganizations.push().getKey();
        ArrayList<Building> OrganizationBuildings = new ArrayList<>();
        Organization test = new Organization("TestMosad", keyId, OrganizationBuildings);
        FBref.refOrganizations.child(keyId).setValue(test).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Organization added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to add organization", Toast.LENGTH_SHORT).show();
                }
            }
        }); */

    }
    public void Register(View view) {

            String Email = eTUser.getText().toString();
            String Password = eTPass.getText().toString();
            System.out.println(Email + " " + Password + " " + MosadSelected + " " + ClassSelected);
        Toast.makeText(MainActivity.this, "Password Not Good Enough.", Toast.LENGTH_SHORT).show();
            mAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser fUser = mAuth.getCurrentUser();
                                String uId = fUser.getUid();
                                User user = new User(MosadStringId, uId, (calendar.get(Calendar.YEAR) + (12-ClassIdSelected)),ClassIdSelected);
                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Organizations/" + user.getKeyID() + "/Users");
                                usersRef.push().setValue(user);
                                startActivity(new Intent(MainActivity.this, ReportsActivity.class));
                                Log.d(TAG, "RegisterWithEmailAndPassword:success");
                                Toast.makeText(MainActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();

                            } else {
                                Log.w(TAG, "RegisterWithCEmailAndPassword:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Email Already Used", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinner) { // אם משתמשים בספינר של מוסד
            if (position != -1) {
                Organization selectedOrg = Organizations.get(position);
                MosadSelected = selectedOrg.getOrganizationName();
                MosadIdSelected = position;
                MosadStringId = String.valueOf(selectedOrg.getKeyId());  // Assuming you have a method to get the organization ID
                if (position <= 2) { // If its Mosad of School
                    spinClass.setAdapter(AdpMosadClass);
                } else {
                    spinClass.setAdapter(AdpMosadWorkArea);
                }
            }
        }

        else{ // אם משתמשים בספניר של כיתה/אזור עבודה
            if (position != 0){
                if(MosadIdSelected <=2){ // אם נבחרה כיתה
                    ClassSelected = Class[position];
                }
                else{
                    ClassSelected = WorkArea[position]; // אם נבחר אזור עבודה
                }
                ClassIdSelected = position;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

/*
package com.example.alphaversion;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    EditText eTUser, eTPass;
    TextView passStrength;
    boolean PasswordGoodEnough = false;
    private FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
            startActivity(new Intent(MainActivity.this, SecondActivity.class));

    }
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("trying to read: ");



        eTUser = (EditText) findViewById(R.id.eTUser);
        eTPass = (EditText) findViewById(R.id.eTPass);
        passStrength = (TextView) findViewById(R.id.tVStrength);
        mAuth = FirebaseAuth.getInstance();

        eTPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean CP = false;
                try {
                    CP = Read(eTPass.getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(CP == true){
                    passStrength.setText("Common Password");
                    passStrength.setTextColor(getResources().getColor(R.color.Red));
                    PasswordGoodEnough = false;
                }

                else if(eTPass.getText().length() < 6) {
                    passStrength.setText("Low");
                    passStrength.setTextColor(getResources().getColor(R.color.Red));
                    PasswordGoodEnough = false;
                }
                    else if(eTPass.getText().length() >= 6 && eTPass.getText().length() < 8){
                        passStrength.setText("Medium");
                        passStrength.setTextColor(getResources().getColor(R.color.yellow));
                        PasswordGoodEnough = true;

                    }
                    else{
                        passStrength.setText("High");
                        passStrength.setTextColor(getResources().getColor(R.color.green));
                        PasswordGoodEnough = true;
                    }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }



    public void Register(View view) {
        if(PasswordGoodEnough) {
            String Email = eTUser.getText().toString();
            String Password = eTPass.getText().toString();
            System.out.println(Email + " " + Password);
            mAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                                Log.d(TAG, "RegisterWithEmailAndPassword:success");
                                Toast.makeText(MainActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w(TAG, "RegisterWithCEmailAndPassword:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Email Already Used", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(MainActivity.this, "Password Not Good Enough.", Toast.LENGTH_SHORT).show();

        }
    }

    public void Login(View view) {
        String Email = eTUser.getText().toString();
        String Password = eTPass.getText().toString();
        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(MainActivity.this, SecondActivity.class));
                            Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "signInWithEmailAndPassword:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCEmailAndPassword:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public boolean Read(String Password) throws IOException {
        InputStream is = this.getResources().openRawResource(R.raw.commonpasswords);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String nextLine = "";

        while (true) {
            try {
                nextLine = reader.readLine();
                if (nextLine == null) break;
                else{
                    nextLine = nextLine.trim();
                    if(nextLine.equals(Password)) {
                        is.close();
                        return true;
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
        is.close();

        return false;
    }

}
 */