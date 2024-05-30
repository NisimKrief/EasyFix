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

public class LogInActivity extends AppCompatActivity {
    EditText eTUser, eTPass;
    private FirebaseAuth mAuth;
    CheckBox rememberCheckBox;
    boolean rememberChecked;
    SharedPreferences sP;
    String orgKey;
    int userLevel;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void RegisterScreen(View view) {
        startActivity(new Intent(LogInActivity.this, MainActivity.class));
    }

    public void Login(View view) {
        pd = ProgressDialog.show(this, "Trying to log in...", "",true);
        String Email = eTUser.getText().toString();
        String Password = eTPass.getText().toString();
        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            SharedPreferences.Editor editor=sP.edit();
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
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            orgKey = snapshot1.getKey();
                                            User user =snapshot1.child(UserUid).getValue(User.class);
                                            userLevel = user.getUserLevel();
                                            System.out.println(userLevel);
                                            FBref fbref = new FBref();
                                            fbref.foundKeyId(user);  // Adjusting the references to the right organization
                                            pd.dismiss();
                                            if(userLevel == 1 || userLevel == 100){
                                                startActivity(new Intent(LogInActivity.this, ReportsActivity.class));
                                            }
                                            else{
                                                startActivity(new Intent(LogInActivity.this, LobbyActivity.class));
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
                            Toast.makeText(LogInActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "signInWithEmailAndPassword:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            pd.dismiss();
                            Log.w(TAG, "signInWithCEmailAndPassword:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}