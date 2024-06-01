package com.example.easyfix.Activites;

import static android.content.ContentValues.TAG;
import static com.example.easyfix.Adapters.ReportListAdapter.OPEN_CAMERA_FOR_FIXED_REPORT_REQUEST;
import static com.example.easyfix.Adapters.ReportListAdapter.OPEN_GALLERY_FOR_FIXED_REPORT_REQUEST;
import static com.example.easyfix.FBref.refOrganizations;
import static com.example.easyfix.FBref.refReports;
import static com.example.easyfix.FBref.refReportsDone;
import static com.example.easyfix.FBref.refUsers;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfix.Adapters.ArrayAdapterBuilding;
import com.example.easyfix.Adapters.ArrayAdapterOrganization;
import com.example.easyfix.Adapters.ReportListAdapter;
import com.example.easyfix.Classes.Building;
import com.example.easyfix.FBref;
import com.example.easyfix.R;
import com.example.easyfix.Classes.Report;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ReportsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String orgKey;
    ArrayList<Report> Reports = new ArrayList<Report>();
    ArrayList<Building> Buildings = new ArrayList<Building>();
    RecyclerView ReportRv;
    ReportListAdapter repListAdapter;
    SharedPreferences sP;
    Spinner spinBuilding;
    String UserUid;
    String reporter;
    ProgressDialog pd;
    ValueEventListener valueEventListenerBuilding;
    ImageView image;
    String stringPhotoTime;
    private StorageReference storageRef;
    private StorageReference imageRef;
    Bitmap photo;
    ConstraintLayout adapterConstraintLayout;
    Switch switchReportsShown;
    Query queryUrgency;
    FloatingActionButton addReportButton;
    public static final int OPEN_CAMERA_REQUEST = 10;
    public static final int OPEN_GALLERY_REQUEST = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reports);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if(!isFinishing())
            pd = ProgressDialog.show(this, "Loading Reports...", "",true);
        switchReportsShown = (Switch) findViewById(R.id.switchReportType);
        ReportRv=findViewById(R.id.repListRv);
        addReportButton=findViewById(R.id.fab_add);
        repListAdapter=new ReportListAdapter(Reports, Buildings, this);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        ReportRv.setLayoutManager(layoutManager);
        sP=getSharedPreferences("Remember",MODE_PRIVATE);

        storageRef = FirebaseStorage.getInstance().getReference();

        // יצירת רפורט רנדומלי

       /* DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Organizations/" + "-Nw5X96KI_P4yGcxoN3L" + "/Reports");
        Report report = new Report(
                null, // reporter
                "There's a big issue in the bathroom of class 5, when I went there I saw lots of water flooding all over the place, please fix", // reportMainType
                1, // malfunctionArea (you can set this based on your requirements)
                String.valueOf(System.currentTimeMillis()), // timeReported (timestamp)
                "Please fix it as fast as possible" // extraInformation
        );
        reportsRef.child(String.valueOf(System.currentTimeMillis())).setValue(report); */
        Report rp = new Report();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser FUser = mAuth.getCurrentUser();
        setupDataFetch();
        UserUid = FUser.getUid();
        String path = UserUid + "/uId"; // הגעה ישירות למיקום הuId
        Query query = refUsers.orderByChild(path).equalTo(UserUid);
        //מציאת מפתח ארגון
        ValueEventListener valueEventListener = new ValueEventListener() { // I should delete this line if I have extra time.

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    refOrganizations.child("organizationBuildings").addListenerForSingleValueEvent(valueEventListenerBuilding);
                    refUsers.child(UserUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            reporter = snapshot.child("userName").getValue(String.class);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //add switch to choose between reports and reportDone
                switchReportsShown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // Switch is ON, show finished reports
                            if(!isFinishing())
                                pd = ProgressDialog.show(ReportsActivity.this, "Switching to Finished Reports...", "",true);
                            queryUrgency.removeEventListener(repListener);
                            queryUrgency = null;
                            queryUrgency = refReportsDone.orderByChild("urgencyLevel");
                            queryUrgency.addValueEventListener(repListener);
                            switchReportsShown.setText("Switch To Show Available Reports");
                            addReportButton.setVisibility(View.GONE);
                        } else {
                            // Switch is OFF, show available reports
                            if(!isFinishing())
                                pd = ProgressDialog.show(ReportsActivity.this, "Switching to available Reports...", "",true);
                            queryUrgency.removeEventListener(repListener);
                            queryUrgency = null;
                            queryUrgency = refReports.orderByChild("urgencyLevel");
                            queryUrgency.addValueEventListener(repListener);
                            switchReportsShown.setText("Switch To Show Finished Reports");
                            addReportButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
                if(switchReportsShown.isChecked()) {
                    queryUrgency = refReportsDone.orderByChild("urgencyLevel"); //if we go back to the screen and the switch is activated
                    switchReportsShown.setText("Switch To Show Available Reports");
                }
                else {
                    queryUrgency = refReports.orderByChild("urgencyLevel");
                    switchReportsShown.setText("Switch To Show Finished Reports");
                }
                queryUrgency.addValueEventListener(repListener);
            }
            ValueEventListener repListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dS) {
                    if(!isFinishing())
                        pd.dismiss();
                    if(!isFinishing())
                        pd = ProgressDialog.show(ReportsActivity.this, "Updating Reports...", "",true);
                    Reports.clear();
                    for(DataSnapshot data : dS.getChildren()){
                        Report rep = data.getValue(Report.class);
                        Reports.add(rep);
                    }
                    ReportRv.setAdapter(repListAdapter);
                    if(!isFinishing())
                        pd.dismiss();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error fetching reports", error.toException());
                    if(!isFinishing())
                        pd.dismiss();

                }
            };


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);

    }


    public void Logout(View view) {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences.Editor editor=sP.edit();
        editor.putBoolean("doRemember", false); //  Update that the user logged out and he needs to press the checkbox again.
        editor.apply();
        FBref fbref = new FBref();
        fbref.loggedOut(); //.When user logs out, update the firebase references so he will not log in to the previous organization
        Intent intent = new Intent(ReportsActivity.this, LogInActivity.class);
        Toast.makeText(ReportsActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void addReport(View view) {
        ConstraintLayout successConstraintLayout = findViewById(R.id.reportDialogConstraintLayout);
        View dialogView = LayoutInflater.from(ReportsActivity.this).inflate(R.layout.report_dialog, successConstraintLayout);
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportsActivity.this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        spinBuilding = (Spinner) dialogView.findViewById(R.id.spinnerBuildings);
        Spinner spinRooms = (Spinner) dialogView.findViewById(R.id.spinnerRooms);
        spinBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinRooms.setAdapter(new ArrayAdapter<String>(ReportsActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, Buildings.get(position).getRooms()));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinBuilding.setAdapter(new ArrayAdapterBuilding(this, Buildings));
        Button Enter = dialogView.findViewById(R.id.reportEnter);
        image = dialogView.findViewById(R.id.reportImageView);
        EditText reportTitle = (EditText) dialogView.findViewById(R.id.repTitle);
        EditText reportDescription = (EditText) dialogView.findViewById(R.id.repDesc);



        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReportsActivity.this);
                builder.setTitle("Choose the option to add an image")
                        .setItems(new CharSequence[]{"Open Camera", "Open Gallery"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: // Open Camera
                                        if (ContextCompat.checkSelfPermission(ReportsActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(ReportsActivity.this, new String[]{android.Manifest.permission.CAMERA}, OPEN_CAMERA_REQUEST);
                                        } else {
                                            openCamera();
                                        }
                                        break;
                                    case 1: // Open Gallery
                                        /*if (ContextCompat.checkSelfPermission(ReportsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            System.out.println("Requesting Gallery Permissionns....");
                                            ActivityCompat.requestPermissions(ReportsActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, OPEN_GALLERY_REQUEST);
                                        } else { */
                                            openGallery();
                                        //}
                                        break;
                                }
                            }
                        });
                builder.create().show();
            }
        });
        Enter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stringPhotoTime = "Null";
                if(!isFinishing())
                    pd = ProgressDialog.show(ReportsActivity.this, "Uploading Report...", "",true);
                if (photo != null) {
                    Bitmap resizedPhoto = resizeBitmap(photo, 800); // Resize to 800x800
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    resizedPhoto.compress(CompressFormat.JPEG, 80, baos);

                    byte[] imageData = baos.toByteArray();
                    long PhotoTime = System.currentTimeMillis();
                    imageRef = storageRef.child("imagesfromcamera/" + PhotoTime + ".jpg");
                    stringPhotoTime = String.valueOf(PhotoTime);

                    // Upload the image data to Firebase Storage
                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        Toast.makeText(ReportsActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                        Toast.makeText(ReportsActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });

                }
                if(!isFinishing())
                    pd.dismiss();
                // לעשות שאם אין נתונים כגון כותרת התקלה, אזור התקלה וכו, להעיר למשתמש.

                Report report = new Report(
                        reporter, // reporter
                        reportTitle.getText().toString(), // reportMainType
                        spinBuilding.getSelectedItemPosition() - 1, // malfunctionArea the minus 1 is because there's the hint building
                        spinRooms.getSelectedItemPosition(), // malfunctionRoom
                        String.valueOf(System.currentTimeMillis()), // timeReported (timestamp)
                        reportDescription.getText().toString(), // extraInformation
                        stringPhotoTime // reportPhoto, use that string to find the image
                );
                refReports.child(String.valueOf(System.currentTimeMillis())).setValue(report);
                if(!isFinishing())
                    pd.dismiss();
                photo = null;
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == OPEN_CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                openCamera();
            } else {
                // Permission denied
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA);
                if (!showRationale) {
                    // User selected "Don't ask again"
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Denied")
                            .setMessage("Camera permission is necessary to take photos. Please enable it in app settings.")
                            .setPositiveButton("Go to Settings", (dialog, which) -> {
                                // Open app settings
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (requestCode == OPEN_GALLERY_REQUEST) {
            System.out.println(ContextCompat.checkSelfPermission(ReportsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE));
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                openGallery();
            } else {
                // Permission denied
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                if (!showRationale) {
                    // User selected "Don't ask again"
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Denied")
                            .setMessage("Storage permission is necessary to access the gallery. Please enable it in app settings.")
                            .setPositiveButton("Go to Settings", (dialog, which) -> {
                                // Open app settings
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if(requestCode == OPEN_CAMERA_FOR_FIXED_REPORT_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                repListAdapter.openCamera();
            } else {
                // Permission denied
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                if (!showRationale) {
                    // User selected "Don't ask again"
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Denied")
                            .setMessage("Storage permission is necessary to access the gallery. Please enable it in app settings.")
                            .setPositiveButton("Go to Settings", (dialog, which) -> {
                                // Open app settings
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if(requestCode == OPEN_GALLERY_FOR_FIXED_REPORT_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                repListAdapter.openCamera();
            } else {
                // Permission denied
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                if (!showRationale) {
                    // User selected "Don't ask again"
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Denied")
                            .setMessage("Storage permission is necessary to access the gallery. Please enable it in app settings.")
                            .setPositiveButton("Go to Settings", (dialog, which) -> {
                                // Open app settings
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public void openCamera(){
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCamera, OPEN_CAMERA_REQUEST);

    }
    public void openGallery(){
        Intent openGallery = new Intent(Intent.ACTION_PICK);
        openGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGallery, OPEN_GALLERY_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode + " " + data);
        if ((requestCode == OPEN_CAMERA_REQUEST)&& resultCode == RESULT_OK && data != null) {
            photo = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(photo);

        }
        else if (requestCode == OPEN_GALLERY_REQUEST && data != null) {
            Uri selectedImage = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                image.setImageBitmap(photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(requestCode == OPEN_CAMERA_FOR_FIXED_REPORT_REQUEST && resultCode == RESULT_OK && data != null){
            photo = (Bitmap) data.getExtras().get("data");
            repListAdapter.handleActivityResult(requestCode, photo, data);
        }
        else if(requestCode ==OPEN_GALLERY_FOR_FIXED_REPORT_REQUEST && data != null){
            Uri selectedImage = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            repListAdapter.handleActivityResult(requestCode, photo, data);
        }
        else {
            Toast.makeText(ReportsActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }
    private Bitmap resizeBitmap(Bitmap original, int maxDimension) { // Resizing the bitmap so it will take less space (kb)
        int width = original.getWidth();
        int height = original.getHeight();
        float scale = ((float) maxDimension) / Math.max(width, height);
        int newWidth = Math.round(scale * width);
        int newHeight = Math.round(scale * height);
        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
    }
    @Override
    public void onBackPressed() {
        // So you can't go back to register/log in without logging out
         super.onBackPressed(); // Comment this out to disable the back button
    }
    private void setupDataFetch() {
        refOrganizations.child("organizationBuildings").addListenerForSingleValueEvent(valueEventListenerBuilding = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Buildings.clear();
                ArrayList<String> hintRooms = new ArrayList<>();
                hintRooms.add("נא לבחור קודם את אזור התקלה");
                Buildings.add(new Building("נא לבחור את אזור התקלה", hintRooms));
                hintRooms = null;
                for (DataSnapshot data : snapshot.getChildren()) {
                    Buildings.add(data.getValue(Building.class));
                }
                // Notify adapter about the data change
                repListAdapter.notifyDataSetChanged();
                if(!isFinishing())
                    pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching buildings", error.toException());
                pd.dismiss();
                Toast.makeText(ReportsActivity.this, "Failed to load buildings", Toast.LENGTH_SHORT).show();
            }
        });
    }

}