package com.example.easyfix;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText eTUser, eTPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Report report = new Report();
        mAuth = FirebaseAuth.getInstance();
        eTUser = (EditText) findViewById(R.id.eTUser);
        eTPass = (EditText) findViewById(R.id.eTPass);

    }
    public void Register(View view) {

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
            Toast.makeText(MainActivity.this, "Password Not Good Enough.", Toast.LENGTH_SHORT).show();

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