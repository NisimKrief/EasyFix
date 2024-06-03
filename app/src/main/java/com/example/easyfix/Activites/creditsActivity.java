package com.example.easyfix.Activites;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.easyfix.R;
/**
 * The Credits activity
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	02/06/2024
 * an activity where user can see the application creator and his teacher.
 */
public class creditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_credits);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Called when the activity has detected the user's press of the back key.
     * <p>
     * This method finishes the activity when the back button is pressed when we go back to the ReportsActivity.
     * </p>
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}