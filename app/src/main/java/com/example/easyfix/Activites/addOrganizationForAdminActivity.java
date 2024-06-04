package com.example.easyfix.Activites;

import static com.example.easyfix.FBref.FBDB;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.easyfix.Classes.Building;
import com.example.easyfix.Classes.Organization;
import com.example.easyfix.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

/**
 * Adding Organizations activity
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	04/06/2024
 * This class represents the activity for adding a new organization by an admin.
 * Only an admin can get to this activity.
 * It allows admins to enter organization details, buildings with areas, and classes or work areas.
 * The information is then saved to Firebase Realtime Database.
 */
public class addOrganizationForAdminActivity extends AppCompatActivity {
    /**
     * EditTexts to capture user input for organization name, key ID, classes or work areas,
     * building name, and areas.
     */
    EditText orgName, orgKeyId, classOrWorkArea, buildName, area;
    /**
     * ArrayLists to store buildings (with their areas) and class/work areas.
     */
    ArrayList<Building> buildings = new ArrayList<>();
    ArrayList<String> areas = new ArrayList<>(), classOrWorkAreas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_organization_for_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        orgName = findViewById(R.id.etOrganizationName);
        orgKeyId = findViewById(R.id.etKeyId);
        classOrWorkArea = findViewById(R.id.etClassesOrWorkArea);
        buildName = findViewById(R.id.etBuildingName);
        area = findViewById(R.id.etAreas);
    }

    /**
     * Handles clicking the "Next Building" button.
     * - Checks if there are areas in the `areas` list.
     * - If areas exist and building name is not empty, creates a new Building object
     *   with the building name and areas, adds it to the `buildings` list, clears the building
     *   name and areas list.
     * - Displays a Toast message if there are no areas or building name is empty.
     *
     * @param view The button that was clicked.
     */
    public void nextBuilding(View view) {
        if(!areas.isEmpty()) {
            if(!buildName.getText().toString().isEmpty()) {
                buildings.add(new Building(buildName.getText().toString(), areas));
                buildName.setText("");
                areas.clear();
            }
            else{
                Toast.makeText(this, "Add Building Name First", Toast.LENGTH_SHORT).show();
                buildName.setError("Add Building Name First");
            }
        }
        else{
            Toast.makeText(this, "Add Areas first", Toast.LENGTH_SHORT).show();
            area.setError("Add Areas first");
        }
    }
    /**
     * Handles clicking the "Next Area" button.
     * - Checks if the area EditText is not empty.
     * - If not empty, adds the area text to the `areas` list and clears the EditText.
     * - Displays a Toast message if the area EditText is empty.
     *
     * @param view The button that was clicked.
     */
    public void nextArea(View view) {
        if(!area.getText().toString().isEmpty()) {
            areas.add(area.getText().toString());
            area.setText("");
        }
        else{
            Toast.makeText(this, "Add area first", Toast.LENGTH_SHORT).show();
            area.setError("Add Area first");
        }
    }

    /**
     * Handles clicking the "Add Class or Work Area" button.
     * - Checks if the class or work area EditText is not empty.
     * - If not empty, adds the text to the `classOrWorkAreas` list and clears the EditText.
     * - Displays a Toast message if the EditText is empty.
     *
     * @param view The button that was clicked.
     */
    public void addClassOrWorkArea(View view) {
        if(!classOrWorkArea.getText().toString().isEmpty()) {
            classOrWorkAreas.add(classOrWorkArea.getText().toString());
            classOrWorkArea.setText("");
        }
        else{
            Toast.makeText(this, "Add class or work are first", Toast.LENGTH_SHORT).show();
            classOrWorkArea.setError("Add class or work are first");
        }
    }

    /**
     * Handles clicking the "Create Organization" button.
     * - Performs validation on user input:
     *   - Checks if organization name is empty and sets error if so.
     *   - Checks if there are any buildings and displays a Toast message if not.
     *   - Checks if there are any class or work areas and displays a Toast message if not.
     * - If all fields are filled, it does the following:
     *   - Gets the organization key ID from the EditText (or generates a new one if empty).
     *   - Creates a new Organization object with the entered details.
     *   - Saves the organization object to Firebase Realtime Database under the "Organizations"
     *     reference using the key ID as the child node.
     *   - Attaches a completion listener to the Firebase write operation.
     *     - On success, displays a success message and resets all fields and ArrayLists.
     *     - On failure, displays a failure message.
     *
     * @param view The button that was clicked.
     */
    public void createOrganization(View view) {
        if(orgName.getText().toString().isEmpty())
            orgName.setError("Organization must have name");
        else if(buildings.isEmpty())
            Toast.makeText(this, "Add Buildings first", Toast.LENGTH_SHORT).show();
        else if(classOrWorkAreas.isEmpty())
            Toast.makeText(this, "Must add class or work areas first", Toast.LENGTH_SHORT).show();
        else{
            // all fields are filled
            String orgKeyIdString = orgKeyId.getText().toString();
            if(orgKeyIdString.isEmpty()){
                orgKeyIdString = FBDB.getReference("Organizations").push().getKey();
            }
            Organization newOrg = new Organization(orgName.getText().toString(), orgKeyIdString, buildings, classOrWorkAreas);
            FBDB.getReference("Organizations").child(orgKeyIdString).setValue(newOrg).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(addOrganizationForAdminActivity.this, "Organization added successfully", Toast.LENGTH_SHORT).show();
                        orgName.setText(""); orgKeyId.setText(""); classOrWorkArea.setText(""); buildName.setText(""); area.setText(""); // reseting all fields
                        buildings.clear(); areas.clear(); classOrWorkAreas.clear(); // reseting all information

                    } else {
                        Toast.makeText(addOrganizationForAdminActivity.this, "Failed to add organization", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
    /**
     * Handles the back button press.
     * - Calls the superclass's onBackPressed() method.
     * - Finishes the activity to go back to "ReportsActivity" and save resources.
     *
     * @Override This method overrides the onBackPressed() method of the Activity class.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
