package com.example.easyfix.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.easyfix.R;

public class LobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lobby);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void moveToReports(View view) {
        startActivity(new Intent(LobbyActivity.this, ReportsActivity.class));
    }

    public void moveToWaitingUsers(View view) {
        startActivity(new Intent(LobbyActivity.this, WaitingUsersListActivity.class));
    }

    public void moveToManageUsers(View view) {
        startActivity(new Intent(LobbyActivity.this, ManageUsersListActivity.class));
    }
}