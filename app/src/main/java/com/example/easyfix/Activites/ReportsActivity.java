package com.example.easyfix.Activites;

import static android.content.ContentValues.TAG;
import static com.example.easyfix.Adapters.ReportListAdapter.OPEN_CAMERA_FOR_FIXED_REPORT_REQUEST;
import static com.example.easyfix.Adapters.ReportListAdapter.OPEN_GALLERY_FOR_FIXED_REPORT_REQUEST;
import static com.example.easyfix.FBref.currentUser;
import static com.example.easyfix.FBref.refOrganizations;
import static com.example.easyfix.FBref.refReports;
import static com.example.easyfix.FBref.refReportsDone;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfix.Adapters.ArrayAdapterBuilding;
import com.example.easyfix.Adapters.ReportListAdapter;
import com.example.easyfix.Classes.Building;
import com.example.easyfix.FBref;
import com.example.easyfix.R;
import com.example.easyfix.Classes.Report;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The Reports activity
 * @author  Nisim Doron Krief
 * @version	1.0
 * @since	21/04/2024
 * The Reports activity, the actual main activity of the application, it handles the display, addition, and management of reports.
 * This activity fetches reports from the Firebase database, displays them in a RecyclerView,
 * and allows users to add new reports with optional images from the camera or gallery.
 * User can interact with each item based on his user level, will explain more in the ReportListAdapter.class
 * Users can switch between viewing available and finished reports.
 */
public class ReportsActivity extends AppCompatActivity {
    /** The list of reports. (fetching from firebase) */
    ArrayList<Report> Reports = new ArrayList<Report>();
    /** The list of buildings. (fetching from firebase) */
    ArrayList<Building> Buildings = new ArrayList<Building>();
    /** The RecyclerView for displaying reports. */
    RecyclerView ReportRv;
    /** The adapter for the RecyclerView. */
    ReportListAdapter repListAdapter;
    /** SharedPreferences for editing "remember me" option when user logs out.. */
    SharedPreferences sP;
    /** The Spinner for selecting buildings. */
    Spinner spinBuilding;
    /** The user uID. */
    String UserUid;
    /** ProgressDialog for interactive loading progress. */
    ProgressDialog pd;
    /** ValueEventListener for fetching building data. */
    ValueEventListener valueEventListenerBuilding;
    /** ImageView for displaying selected image. */
    ImageView image;
    /** String to store photo timestamp, thats the name of the photo in firebase storage. */
    String stringPhotoTime;
    /** Firebase Storage reference. */
    private StorageReference storageRef;
    /** Firebase Storage reference for image. */
    private StorageReference imageRef;
    /** Bitmap to store the photo. */
    Bitmap photo;
    /** Switch for toggling between available and finished reports. */
    Switch switchReportsShown;
    /** Query for fetching reports, sorting based on urgency. */
    Query queryUrgency;
    /** FloatingActionButton for adding new reports. */
    FloatingActionButton addReportButton;

    private ActivityResultLauncher<Intent> openCameraLauncher;
    private ActivityResultLauncher<Intent> openGalleryLauncher;
    private ActivityResultLauncher<Intent> openCameraForFixedReportLauncher;
    private ActivityResultLauncher<Intent> openGalleryForFixedReportLauncher;
    /** Request code for opening camera. */
    public static final int OPEN_CAMERA_REQUEST = 10;
    /** Request code for opening gallery. */
    public static final int OPEN_GALLERY_REQUEST = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FBref.checkInternetConnection(this);
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

        // Register activity result launchers
        openCameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), openCameraResultCallback);
        openGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), openGalleryResultCallback);
        openCameraForFixedReportLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), openCameraForFixedReportResultCallback);
        openGalleryForFixedReportLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), openGalleryForFixedReportResultCallback);

        repListAdapter=new ReportListAdapter(Reports, Buildings, this, openCameraForFixedReportLauncher, openGalleryForFixedReportLauncher);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        ReportRv.setLayoutManager(layoutManager);
        sP=getSharedPreferences("Remember",MODE_PRIVATE);
        storageRef = FirebaseStorage.getInstance().getReference();
        UserUid = currentUser.getuId();
        ValueEventListener repListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                if(!isFinishing()){
                    pd.dismiss();
                    pd = ProgressDialog.show(ReportsActivity.this, "Updating Reports...", "",true);
                }
                Reports.clear();
                for(DataSnapshot data : dS.getChildren()){
                    Report rep = data.getValue(Report.class);
                    Reports.add(rep);
                }
                if(Buildings.isEmpty()){ // if the activity didn't fetch the buildings yet, he should fetch them and then set the adapter
                    setupDataFetch();
                }
                else //if he did, there's no reason to fetch the buildings again, just update the adapter.
                    ReportRv.setAdapter(repListAdapter);
                if(!isFinishing())
                    pd.dismiss();
                //updating the add report button after the reports are loaded
                if(switchReportsShown.isChecked()) {
                    addReportButton.setVisibility(View.GONE);
                }
                else {
                    addReportButton.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FBref.checkInternetConnection(ReportsActivity.this);
                Log.e(TAG, "Error fetching reports", error.toException());
                if(!isFinishing())
                    pd.dismiss();
            }
        };
        switchReportsShown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FBref.checkInternetConnection(ReportsActivity.this);
                if (isChecked) {
                    // Switch is ON, show finished reports
                    if(!isFinishing())
                        pd = ProgressDialog.show(ReportsActivity.this, "Switching to Finished Reports...", "",true);
                    queryUrgency.removeEventListener(repListener);
                    queryUrgency = null;
                    queryUrgency = refReportsDone.orderByChild("urgencyLevel");
                    queryUrgency.addValueEventListener(repListener);
                    switchReportsShown.setText("Switch To Show Available Reports");
                } else {
                    // Switch is OFF, show available reports
                    if(!isFinishing())
                        pd = ProgressDialog.show(ReportsActivity.this, "Switching to available Reports...", "",true);
                    queryUrgency.removeEventListener(repListener);
                    queryUrgency = null;
                    queryUrgency = refReports.orderByChild("urgencyLevel");
                    queryUrgency.addValueEventListener(repListener);
                    switchReportsShown.setText("Switch To Show Finished Reports");
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


    /**
     * Logs out the user and redirects to the login activity.
     */
    public void Logout() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences.Editor editor=sP.edit();
        editor.putBoolean("doRemember", false); //  Update that the user logged out and he needs to press the checkbox again.
        editor.apply();
        FBref fbref = new FBref();
        fbref.loggedOut(); //.When user logs out, update the firebase references so he will not log in to the previous organization
        Intent intent = new Intent(ReportsActivity.this, LogInActivity.class);
        Toast.makeText(ReportsActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }

    /**
     * Opens the add report dialog.
     * it contains 2 edit texts, and 2 spinners, and 1 imageView
     * the first edittext is for the report Title which is a must for the report
     * the second edit text is for the description which is only if you want to add extra information
     * the first spinner is to choose a building, it first show the hint building "Choose a building", its a must to choose
     * the second spinner is to choose areas based on the building that has been chosen.
     * the imageview is not necessary, you can add image to the report, when pressing the imageview it will prompt
     * if you want camera or gallery, based on the user choice it will ask for permissions and get the desired image,
     * when pressing enter it will publish the report to firebase realtime database.
     * @param view The view that triggers this method.
     */
    public void addReport(View view) {
        photo = null; // if user inserted photo before but chose to leave the report.
        if (!isFinishing()) {
            pd = ProgressDialog.show(ReportsActivity.this, "Opening add report dialog...", "", true);
        }

        ConstraintLayout successConstraintLayout = findViewById(R.id.reportDialogConstraintLayout);
        View dialogView = LayoutInflater.from(ReportsActivity.this).inflate(R.layout.report_dialog, successConstraintLayout);
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportsActivity.this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();

        // Show the dialog and dismiss the progress dialog after it is shown
        alertDialog.setOnShowListener(dialogInterface -> {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        });

        alertDialog.show();

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
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

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
                                    if ((ContextCompat.checkSelfPermission(ReportsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(ReportsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != -1) {
                                        //if ContextCompat.checkSelfPermission(ReportsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) = -1, it means it doesn't ask the user for permissions.
                                        ActivityCompat.requestPermissions(ReportsActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, OPEN_GALLERY_REQUEST);
                                    } else {
                                        openGallery();
                                        }
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
                int count = 0;
                if(reportTitle.getText().toString().isEmpty()){
                    reportTitle.setError("Report must have a title");
                    count++;
                }
                if(spinBuilding.getSelectedItemPosition() == 0){
                    //its the hint building
                    count++;
                    Toast.makeText(ReportsActivity.this, "Must Choose Building", Toast.LENGTH_SHORT).show();
                }
                if(count ==0) {
                    stringPhotoTime = "Null";
                    if (!isFinishing())
                        pd = ProgressDialog.show(ReportsActivity.this, "Uploading Report...", "", true);
                    if (photo != null) {
                        Bitmap resizedPhoto = resizeBitmap(photo, 800); // Resize to 800x800
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resizedPhoto.compress(Bitmap.CompressFormat.JPEG, 80, baos);

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
                            FBref.checkInternetConnection(ReportsActivity.this);
                            Toast.makeText(ReportsActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        });

                    }
                    String timeReported = String.valueOf(System.currentTimeMillis());
                    Report report = new Report(
                            currentUser.getUserName(), // reporter
                            reportTitle.getText().toString(), // reportMainType
                            spinBuilding.getSelectedItemPosition() - 1, // malfunctionArea the minus 1 is because there's the hint building
                            spinRooms.getSelectedItemPosition(), // malfunctionRoom
                            timeReported, // timeReported (timestamp)
                            reportDescription.getText().toString(), // extraInformation
                            stringPhotoTime // reportPhoto, using that string to find the image
                    );
                    refReports.child(timeReported).setValue(report).addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void unused) {
                            Toast.makeText(ReportsActivity.this, "Report uploaded successfully", Toast.LENGTH_SHORT).show();
                            if (!isFinishing())
                                pd.dismiss();
                        }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (!isFinishing())
                                    pd.dismiss();
                                FBref.checkInternetConnection(ReportsActivity.this);
                            }
                        });
                    if (!isFinishing())
                        pd.dismiss();
                    photo = null;
                    alertDialog.dismiss();
                }
            }
        });
    }

    /**
     * @param requestCode The request code
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * if permissions get accepted it will continue, if permissions get rejected twice, it will
     * open a dialog asking you to add the permissions manually for the app.
     *
     */
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
                repListAdapter.openGallery();
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

    /**
     * Opens the camera application to capture an image.
     */
    public void openCamera() {
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraLauncher.launch(openCamera);
    }

    /**
     * Opens the gallery application to select an image.
     */
    public void openGallery() {
        Intent openGallery = new Intent(Intent.ACTION_PICK);
        openGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGalleryLauncher.launch(openGallery);
    }
    /**
     * Callback for handling the result from the camera.
     */
    private final ActivityResultCallback<ActivityResult> openCameraResultCallback = result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            photo = (Bitmap) result.getData().getExtras().get("data");
            image.setImageBitmap(photo);
        } else {
            Toast.makeText(ReportsActivity.this, "No Image Captured", Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * Callback for handling the result from the gallery.
     */
    private final ActivityResultCallback<ActivityResult> openGalleryResultCallback = result -> {
        if (result.getData() != null) {
            Uri selectedImage = result.getData().getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                image.setImageBitmap(photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(ReportsActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * Callback for handling the result from the camera for a fixed report.
     * and sending the results back to the adapter
     */
    private final ActivityResultCallback<ActivityResult> openCameraForFixedReportResultCallback = result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            photo = (Bitmap) result.getData().getExtras().get("data");
            repListAdapter.handleActivityResult(photo, result.getData());
            photo = null;
        } else {
            Toast.makeText(ReportsActivity.this, "No Image Captured", Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * Callback for handling the result from the gallery for a fixed report.
     * and sending the results back to the adapter
     */
    private final ActivityResultCallback<ActivityResult> openGalleryForFixedReportResultCallback = result -> {
        if (result.getData() != null) {
            Uri selectedImage = result.getData().getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                repListAdapter.handleActivityResult(photo, result.getData());
                photo = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Toast.makeText(ReportsActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * Resizes a bitmap to fit within a specified maximum dimension and also reduce image size.
     * @param original The original bitmap.
     * @param maxDimension The maximum dimension for the resized bitmap.
     * @return The resized bitmap.
     */
    private Bitmap resizeBitmap(Bitmap original, int maxDimension) { // Resizing the bitmap so it will take less space (kb)
        int width = original.getWidth();
        int height = original.getHeight();
        float scale = ((float) maxDimension) / Math.max(width, height);
        int newWidth = Math.round(scale * width);
        int newHeight = Math.round(scale * height);
        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
    }
    /**
     * Disables the back button to prevent navigation without logging out.
     */
    @Override
    public void onBackPressed() {
        // So you can't go back to register/log in without logging out
        //super.onBackPressed(); // Comment this out to disable the back button
    }
    /**
     * Sets up data fetching for the buildings.
     * Fetches the list of buildings from the database and updates the UI.
     */
    private void setupDataFetch() {
        refOrganizations.child("organizationBuildings").addListenerForSingleValueEvent(valueEventListenerBuilding = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Buildings.clear();
                ArrayList<String> hintRooms = new ArrayList<>();
                hintRooms.add("נא לבחור קודם את אזור התקלה");
                Buildings.add(new Building("נא לבחור את אזור התקלה", hintRooms));
                for (DataSnapshot data : snapshot.getChildren()) {
                    Buildings.add(data.getValue(Building.class));
                }
                repListAdapter.notifyDataSetChanged();
                if (!isFinishing())
                    pd.dismiss();
                ReportRv.setAdapter(repListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching buildings", error.toException());
                FBref.checkInternetConnection(ReportsActivity.this);
                if (!isFinishing())
                    pd.dismiss();
                Toast.makeText(ReportsActivity.this, "Failed to load buildings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles the options menu click events.
     * only teachers, managers and admins can see the full options menu
     * students and construction workers will only see "credits" and "logout"
     * @param view The view that triggers this method.
     */
    public void optionsMenuClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        if(currentUser.getUserLevel() < 10000){
            //only admin should see that option
            popupMenu.getMenu().findItem(R.id.addOrganizationOption).setVisible(false);
        }
        if (currentUser.getUserLevel() < 10 || currentUser.getUserLevel() == 100) {
            //if its a regular user or a construction worker he shouldn't see all the screens.
            popupMenu.getMenu().findItem(R.id.manageUsersOption).setVisible(false);
            popupMenu.getMenu().findItem(R.id.waitingUsersOption).setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.manageUsersOption) {
                    startActivity(new Intent(ReportsActivity.this, ManageUsersListActivity.class));
                    return true;
                }
                if (id == R.id.waitingUsersOption) {
                    startActivity(new Intent(ReportsActivity.this, WaitingUsersListActivity.class));
                    return true;
                }
                if(id == R.id.addOrganizationOption){
                    startActivity(new Intent(ReportsActivity.this, addOrganizationForAdminActivity.class));
                    return true;
                }
                if (id == R.id.creditsOption) {
                    startActivity(new Intent(ReportsActivity.this, creditsActivity.class));
                    return true;
                }
                if (id == R.id.logOutOption) {
                    Logout();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

}