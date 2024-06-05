package com.example.easyfix.Activites;

import static android.content.ContentValues.TAG;

import static com.example.easyfix.FBref.refUsers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.easyfix.Classes.User;
import com.example.easyfix.FBref;
import com.example.easyfix.R;
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
 * The log in activity
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	23/04/2024
 * an activity where user enters their email and password, if the user exists, the user logs in to his organization
 * if the user information are correct but he is not in "users" reference that means he is in waiting users and should wait till a teacher or
 * the manager accepts him.
 */
public class LogInActivity extends AppCompatActivity {
    /**
     * The E t user email.
     */
    EditText eTUser, /**
     * The E t password.
     */
    eTPass;
    private FirebaseAuth mAuth;
    /**
     * The Remember check box.
     */
    CheckBox rememberCheckBox;
    /**
     * remember checked - checkbox.isChecked().
     */
    boolean rememberChecked;
    /**
     * The Shared Preferences.
     */
    SharedPreferences sP;
    /**
     * The Organization key.
     */
    String orgKey;
    /**
     * The User level.
     */
    int userLevel;

    /**
     * The Progress Dialog.
     */
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FBref.checkInternetConnection(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rememberChecked = false;
        eTUser = (EditText) findViewById(R.id.eTUser2);
        eTPass = (EditText) findViewById(R.id.eTPass2);
        rememberCheckBox = (CheckBox)findViewById(R.id.checkBox);
        mAuth = FirebaseAuth.getInstance();
        sP=getSharedPreferences("Remember",MODE_PRIVATE);

    }

    /**
     * Register screen.
     *
     * @param view the view
     */
    public void RegisterScreen(View view) {
        startActivity(new Intent(LogInActivity.this, MainActivity.class));
        finish();
    }

    /**
     * Attempts to log in the user using the provided email and password.
     * <p>
     * This method checks if the email and password fields are not empty. If any field is empty,
     * an error message is set on the respective field. If both fields are filled, a login attempt
     * is made using Firebase Authentication.
     *
     * @param view The view that triggers this method.
     */
    public void Login(View view) {
        int count = 0;

        // Check if the email field is empty
        if (eTUser.getText().toString().isEmpty()) {
            count++;
            eTUser.setError("This field is required");
        }

        // Check if the password field is empty
        if (eTPass.getText().toString().isEmpty()) {
            count++;
            eTPass.setError("This field is required");
        }

        // If both fields are filled
        if (count == 0) {
            pd = ProgressDialog.show(this, "Trying to log in...", "", true);

            String Email = eTUser.getText().toString();
            String Password = eTPass.getText().toString();

            // Attempt to sign in with email and password
            mAuth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Save login state, if the checkbox is checked it will save and login the user once the app is opened, unless he log outs.
                                SharedPreferences.Editor editor = sP.edit();
                                editor.putBoolean("doRemember", rememberCheckBox.isChecked());
                                editor.apply();
                                mAuth = FirebaseAuth.getInstance();
                                FirebaseUser FUser = mAuth.getCurrentUser();
                                String UserUid = FUser.getUid();
                                String path = UserUid + "/uId"; // Path to the uId
                                Query query = refUsers.orderByChild(path).equalTo(UserUid); // Finding the organization the user is in.
                                ValueEventListener valueEventListener = new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // the user is in Users and should get redirected to his organization.
                                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                orgKey = snapshot1.getKey();
                                                User user = snapshot1.child(UserUid).getValue(User.class);
                                                userLevel = user.getUserLevel();
                                                FBref fbref = new FBref();
                                                fbref.foundKeyId(user);  // Adjusting the references to the right organization, saving user
                                                pd.dismiss();
                                                Toast.makeText(LogInActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LogInActivity.this, ReportsActivity.class));
                                            }

                                        } else {
                                            // user still in waiting users, should wait to get accepted.
                                            pd.dismiss();
                                            Toast.makeText(LogInActivity.this, "Still waiting, please ask your teacher to accept you.", Toast.LENGTH_LONG).show(); // User in waitingUsers
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                };
                                query.addListenerForSingleValueEvent(valueEventListener);
                                Log.d(TAG, "signInWithEmailAndPassword:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                            } else {
                                pd.dismiss();
                                Log.w(TAG, "signInWithCEmailAndPassword:failure", task.getException());
                                FBref.checkInternetConnection(LogInActivity.this);
                                Toast.makeText(LogInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                Toast.makeText(LogInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }
    public void onBackPressed() {
        // So you can't go back after logging out, which may crash the application
        //super.onBackPressed(); // Comment this out to disable the back button
    }

}