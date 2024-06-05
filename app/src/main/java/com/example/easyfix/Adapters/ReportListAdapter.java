package com.example.easyfix.Adapters;

import static com.example.easyfix.FBref.currentUser;
import static com.example.easyfix.FBref.refReports;
import static com.example.easyfix.FBref.refReportsDeleted;
import static com.example.easyfix.FBref.refReportsDone;
import static java.lang.Long.parseLong;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfix.Classes.Building;
import com.example.easyfix.FBref;
import com.example.easyfix.R;
import com.example.easyfix.Classes.Report;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * The Array Adapter for Reports
 * @author  Nisim Doron Krief
 * @version	1.1
 * @since	23/04/2024
 * Responsible for displaying and managing the list of reports in a RecyclerView.
 * Provides functionalities to update, delete, work on reports, add fixed image, and finish reports.
 * only a user level 100 and higher (construction worker, manager and admin) can update delete work and finish reports,
 * normal users will see the report without all that options
 * if a report has timeFixed(), it means its a fixed report and shouldn't be changed, deleted, updated etc
 * even for a level 100+ user.
 * when clicking on report image or fixed report image it will open it full screen
 * if a worker chose to work on a report no one else can join him
 * once a worker started working on report the "reportCurrentWorker" is set to his name
 * a worker can't start working on a report before setting an urgency
 * a worker can't finish a report before adding a fixed image.
 * Handles image uploading and permissions for camera and gallery access.
 */
public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ViewHolder> {

    /**
     * The list of reports.
     */
    private ArrayList<Report> Reports;
    /**
     * The list of buildings.
     */
    ArrayList<Building> Buildings;
    /**
     * The context of ReportsActivity. (need that in order to show alertdialogs and toasts).
     */
    private Context context;
    /**
     * The spinner for urgency levels, can only interact with that if userLevel >= 100
     */
    Spinner urgencySpinner;
    /**
     * Buttons for various actions, only a user level >= 100 can see them.
     */
    Button updateButton, deleteButton, takeJobButton, finishReportButton;
    /**
     * Image views for displaying report image, and setting fixed report image.
     */
    ImageView reportImageView, fixedImageView;
    /**
     * Text views for displaying report details.
     */
    TextView reportTitleTV, reporterTV, extraInformationTV, urgencyTV, buildingTV, areaTV, reportDateTV, workingOnTheFixTV, fixedImageTV, reportFixDateTV;
    /**
     * An array of urgency levels.
     */
    private String[] urgencyLevels = {"Not set yet", "Low", "Medium", "High"};
    /**
     * The urgency level of the current report.
     */
    String urgency;
    /**
     * The Bitmap value of the photos, used to save the bitmap of the small photos in order to
     * open alertDialog with big image (full screen) of the same image.
     */
    Bitmap photo;
    /**
     * A progress dialog for displaying loading status.
     */
    private ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> openCameraForFixedReportLauncher;
    private ActivityResultLauncher<Intent> openGalleryForFixedReportLauncher;
    /**
     * Request code for opening camera to capture fixed report images.
     */
    public static final int OPEN_CAMERA_FOR_FIXED_REPORT_REQUEST = 201;
    /**
     * Request code for opening gallery to select fixed report images.
     */
    public static final int OPEN_GALLERY_FOR_FIXED_REPORT_REQUEST = 300;

    /**
     * Represents a view holder for managing report details.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ReportNameTv, ReportExInfoTv, ReportAreaTv;
        private final CardView cardView;
        private final ImageView urgencyIcon;

        /**
         * The container layout to interact when an item is pressed.
         */
        LinearLayout containerll;

        /**
         * Instantiates a new View holder.
         *
         * @param view the view
         */
        public ViewHolder(View view) {
            super(view);

            ReportNameTv = view.findViewById(R.id.reportsNameTv);
            ReportExInfoTv = view.findViewById(R.id.reportExInfoTv);
            urgencyIcon = view.findViewById(R.id.urgencyIcon);
            ReportAreaTv = view.findViewById(R.id.reportAreaTv);
            cardView = view.findViewById(R.id.cardId);
            containerll = view.findViewById(R.id.containerLL);
        }

        /**
         * Gets the report name text view.
         *
         * @return the text view for the report name
         */
        public TextView getTextView() {
            return ReportNameTv;
        }
    }

    /**
     * Instantiates a new Report list adapter.
     *
     * @param taskDataset the list of reports
     * @param buildings   the list of buildings
     * @param context     the context of the activity (ReportsActivity)
     * @param openCameraForFixedReportLauncher The ActivityResultLauncher for opening the camera.
     * @param openGalleryForFixedReportLauncher The ActivityResultLauncher for opening the gallery.
     */
    public ReportListAdapter(ArrayList<Report> taskDataset, ArrayList<Building> buildings, Context context, ActivityResultLauncher<Intent> openCameraForFixedReportLauncher, ActivityResultLauncher<Intent> openGalleryForFixedReportLauncher)
    {
        this.Reports = taskDataset;
        Buildings = buildings;
        this.context = context;
        this.openCameraForFixedReportLauncher = openCameraForFixedReportLauncher;
        this.openGalleryForFixedReportLauncher = openGalleryForFixedReportLauncher;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_report, viewGroup, false);

        return new ViewHolder(view);
    }

    /**
     * Binds the report data to the views within the ViewHolder.
     *
     * @param viewHolder the ViewHolder to bind data to
     * @param position   the position of the item within the RecyclerView
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Report currentReport = Reports.get(position);

        viewHolder.ReportNameTv.setText(currentReport.getReportMainType());
        viewHolder.ReportExInfoTv.setText(currentReport.getExtraInformation());
        viewHolder.ReportAreaTv.setText(Buildings.get(currentReport.getMalfunctionArea() + 1).getBuildingName());

        urgency = Reports.get(position).getUrgencyLevel();
        //base report background color
        viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#B3E5FC"));

        if (!currentReport.getMalfunctionFixer().equals("Null")) {
            // if there's a worker currently working on the report
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#FFC107"));
        }
        if(!currentReport.getTimeFixed().equals("Null")) // if a report is finished it should be green
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#7ED98F"));

        //firebase saves urgency as Low, JMeduim and Hard in order to show it the right way in query
        if (urgency.equals("JMedium"))
            urgency = "Medium";

        switch (urgency.toLowerCase()) {
            case "low":
                viewHolder.urgencyIcon.setColorFilter(Color.parseColor("#28a745"));
                break;
            case "medium":
                viewHolder.urgencyIcon.setColorFilter(Color.parseColor("#ff9800"));
                break;
            case "high":
                viewHolder.urgencyIcon.setColorFilter(Color.parseColor("#f44336"));
                break;
            default:
                viewHolder.urgencyIcon.setColorFilter(Color.parseColor("#000000"));
                break;
        }

        viewHolder.containerll.setOnLongClickListener(new View.OnLongClickListener() {
            /**
             * Handles long click events on the container view. This method inflates a dialog layout,
             * initializes UI components, and sets up various actions based on the state of the current report.
             *
             * @param view the view that was clicked and held
             * @return true to indicate that the long click event has been handled
             */
            @Override
            public boolean onLongClick(View view) {
                View dialogView = LayoutInflater.from(context).inflate(R.layout.openedreport_dialog, null);
                ConstraintLayout adapterConstraintLayout = dialogView.findViewById(R.id.openedReportDialogConstraintLayout);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                if(alertDialog.getWindow() != null){
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
                progressDialog = ProgressDialog.show(context, "Loading " + currentReport.getReportMainType() + " Report...", "",true);

                urgencySpinner = dialogView.findViewById(R.id.urgencySpinner);

                updateButton = dialogView.findViewById(R.id.updateButton);
                deleteButton = dialogView.findViewById(R.id.deleteButton);
                takeJobButton = dialogView.findViewById(R.id.takeJobButton);
                finishReportButton = dialogView.findViewById(R.id.reportFixedButton);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, urgencyLevels);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                urgencySpinner.setAdapter(adapter);
                urgency = Reports.get(position).getUrgencyLevel();

                if (urgency.equals("JMedium"))
                    urgency = "Medium";
                setSpinnerSelection(urgency);

                if (currentUser.getUserLevel() >= 100 && currentReport.getTimeFixed().equals("Null")) {
                    // if user is construction worker/manager/admin he should be able to press those buttons, but if there's time fixed
                    // which means the report is done, he shouldn't see those buttons and it should stay there.
                    urgencySpinner.setEnabled(true);
                    updateButton.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.VISIBLE);
                    takeJobButton.setVisibility(View.VISIBLE);
                } else {
                    urgencySpinner.setEnabled(false);
                }

                if (!currentReport.getMalfunctionFixer().equals("Null") && currentReport.getTimeFixed().equals("Null")) {
                    //if there's someone working on the report and the report isn't fixed yet another worker shouldn't see the takeJob button
                    if (currentReport.getMalfunctionFixer().equals(currentUser.getUserName())) {
                        //if the worker is the current user he should see the take job button but change it to leave job
                        takeJobButton.setText("Leave Job");
                    } else
                        takeJobButton.setVisibility(View.GONE);
                }

                reportImageView = dialogView.findViewById(R.id.reportImageView);
                fixedImageView = dialogView.findViewById(R.id.fixImageView);

                reportTitleTV = dialogView.findViewById(R.id.openedReportTitle);
                reporterTV = dialogView.findViewById(R.id.reporterLabel);
                extraInformationTV = dialogView.findViewById(R.id.extraInfoLabel);
                urgencyTV = dialogView.findViewById(R.id.urgencyLabel);
                buildingTV = dialogView.findViewById(R.id.buildingLabel);
                areaTV = dialogView.findViewById(R.id.areaLabel);
                reportDateTV = dialogView.findViewById(R.id.dateTextView);
                workingOnTheFixTV = dialogView.findViewById(R.id.workingOnTheFixLabel);
                fixedImageTV = dialogView.findViewById(R.id.fixImageLabel);
                reportFixDateTV = dialogView.findViewById(R.id.reportFixDateLabel);

                reportTitleTV.setText(Reports.get(position).getReportMainType());
                reporterTV.setText("Reporter: " + currentReport.getReporter());
                extraInformationTV.setText("Extra Information: " + currentReport.getExtraInformation());
                buildingTV.setText("Building: " + Buildings.get(currentReport.getMalfunctionArea() + 1).getBuildingName());
                areaTV.setText("Room: " + Buildings.get(currentReport.getMalfunctionArea() + 1).getRooms().get(Reports.get(position).getMalfunctionRoom()));
                reportDateTV.setText("Report Date: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date(parseLong(currentReport.getTimeReported()))));

                if (currentReport.getMalfunctionFixer().equals("Null")) {
                    //there's no worker currently working on the fix.
                    workingOnTheFixTV.setText("Working On The Fix: No one yet");
                } else {
                    //there's worker currently working on the fix.
                    workingOnTheFixTV.setText("Working On The Fix: " + currentReport.getMalfunctionFixer());
                }
                if(!currentReport.getTimeFixed().equals("Null")){
                    // the report is fixed already, which means it should be "worked" instead of "working" and also add the fixed image and date fixed.
                    workingOnTheFixTV.setText("Worked On The Fix: " + currentReport.getMalfunctionFixer());
                    reportFixDateTV.setVisibility(View.VISIBLE);
                    getImageFromFireBase(currentReport.getFixedPhoto(), context, 1);
                    reportFixDateTV.setText("Report Fix Date: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date(parseLong(currentReport.getTimeFixed()))));
                }

                if (!currentReport.getReportPhoto().equals("Null")) {
                    //if there's report photo the imageview should be visible and the application will fetch the image.
                    getImageFromFireBase(currentReport.getReportPhoto(), context, 0);
                    reportImageView.setVisibility(View.VISIBLE);
                }
                else if(currentReport.getTimeFixed().equals("Null"))
                    progressDialog.dismiss();


                if (currentReport.getMalfunctionFixer().equals(currentUser.getUserName()) || !currentReport.getFixedPhoto().equals("Null")) {
                    //if the worker on the report or the report is fixed, then the fixedImageView should be visible
                    fixedImageTV.setVisibility(View.VISIBLE);
                    fixedImageView.setVisibility(View.VISIBLE);

                    if (currentReport.getMalfunctionFixer().equals(currentUser.getUserName()) &&  currentReport.getTimeFixed().equals("Null")) // if the report is taken by that worker he should see the button, but if the report is fixed already he shouldn't
                        finishReportButton.setVisibility(View.VISIBLE);
                }


                fixedImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currentReport.getTimeFixed().equals("Null")) { // a worker can add fixed photo only if the report isn't fixed yet.
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Choose the option to add an image")
                                    .setItems(new CharSequence[]{"Open Camera", "Open Gallery"}, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0: // Open Camera
                                                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                                        ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CAMERA}, OPEN_CAMERA_FOR_FIXED_REPORT_REQUEST);
                                                    } else {
                                                        openCamera();
                                                    }
                                                    break;
                                                case 1:
                                                    if ((ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != -1) {
                                                        //if ContextCompat.checkSelfPermission(ReportsActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) = -1, it means it doesn't ask the user for permissions.
                                                    } else {
                                                        openGallery();
                                                    }
                                                    break;
                                            }
                                        }
                                    });
                            builder.create().show();
                        }
                        else{
                            // if the report is fixed, by pressing the image it should open it full screen.
                            Bitmap bitmap = ((BitmapDrawable) fixedImageView.getDrawable()).getBitmap();
                            showImageDialog(bitmap);
                        }
                    }
                });

                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newUrgency = (String) urgencySpinner.getSelectedItem();
                        String previousUrgency = currentReport.getUrgencyLevel();
                        if (previousUrgency.equals("JMedium"))
                            previousUrgency = "Medium";
                        if (newUrgency.equals("Medium"))
                            newUrgency = "JMedium";
                        if (!currentReport.getUrgencyLevel().equals(newUrgency) && urgencySpinner.getSelectedItemPosition() != 0) {
                            //updating the urgency only if its a different urgency than was set before and its not the hint "not set yet" urgency.
                            String finalPreviousUrgency = previousUrgency;
                            refReports.child(currentReport.getTimeReported()).child("urgencyLevel").setValue(newUrgency).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    if (currentReport.getUrgencyLevel().equals("Null")) //if the current urgency is "Null" should toast for the first change as "Set" and not changed
                                        Toast.makeText(context, "Urgency for '" + currentReport.getReportMainType() + "' Was set to " + urgencySpinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
                                    else //the current urgency should toast as changed
                                        Toast.makeText(context, "Urgency for '" + currentReport.getReportMainType() + "' changed from " + finalPreviousUrgency + " to " + urgencySpinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                            });
                        } else if (urgencySpinner.getSelectedItemPosition() == 0) {
                            Toast.makeText(context, "Report must have urgency level", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                takeJobButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!currentReport.getMalfunctionFixer().equals("Null")) {
                            //what happens to the button if user is the worker and took the job
                            //only the worker will see the job and by pressing "leave job" he will leave the report.
                            refReports.child(currentReport.getTimeReported()).child("malfunctionFixer").setValue("Null").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Successfully left '" + currentReport.getReportMainType() + "' job.", Toast.LENGTH_LONG).show();
                                    alertDialog.dismiss();
                                }
                            });
                        } else { //if there's no one working on the report
                            if (currentReport.getUrgencyLevel().equals("Null")) { //if there's no urgency, worker must set urgency before taking the job.
                                Toast.makeText(context, "Please set urgency level first and update it.", Toast.LENGTH_SHORT).show();
                            } else {//if there's urgency, worker claimed the job successfully.
                                refReports.child(currentReport.getTimeReported()).child("malfunctionFixer").setValue(currentUser.getUserName()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Report '" + currentReport.getReportMainType() + "' has been claimed by " + currentUser.getUserName() + ". Good Luck!", Toast.LENGTH_LONG).show();
                                        alertDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // check for internet connection
                                        FBref.checkInternetConnection(context);
                                    }
                                });
                            }
                        }
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refReports.child(currentReport.getTimeReported()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Report '" + currentReport.getReportMainType() + "' removed successfully", Toast.LENGTH_SHORT).show();
                                //report getting deleted from the Reports in firebase realtime database and then moved to the "ReportsDeleted" in firebase realtime database.
                                refReportsDeleted.child(currentReport.getTimeReported()).setValue(currentReport);
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                FBref.checkInternetConnection(context);
                            }
                        });
                    }
                });
                finishReportButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(photo != null) { //if worker added a photo already
                            //compress photo
                            Bitmap resizedPhoto = resizeBitmap(photo, 800); // Resize to 800x800
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            resizedPhoto.compress(Bitmap.CompressFormat.JPEG, 80, baos);

                            byte[] imageData = baos.toByteArray();
                            long PhotoTime = System.currentTimeMillis();
                            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("imagesfromcamera/" + PhotoTime + ".jpg");
                            String stringPhotoTime = String.valueOf(PhotoTime);

                            // Upload the image data to Firebase Storage
                            UploadTask uploadTask = imageRef.putBytes(imageData);
                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                                // Image uploaded successfully
                                Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                // Handle unsuccessful uploads
                                FBref.checkInternetConnection(context);
                                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            });
                            currentReport.setFixedPhoto(stringPhotoTime);
                            currentReport.setTimeFixed(String.valueOf(System.currentTimeMillis()));

                            refReports.child(currentReport.getTimeReported()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    refReportsDone.child(currentReport.getTimeReported()).setValue(currentReport).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Successfully done the report and moved it to "reports done"
                                            Toast.makeText(context, "Report '" + currentReport.getReportMainType() + "' Successfully done", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            FBref.checkInternetConnection(context);
                                        }
                                    });
                                    photo = null;
                                    alertDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    FBref.checkInternetConnection(context);
                                }
                            });
                        }
                        else{
                            //if worker didn't add a photo yet.
                            Toast.makeText(context, "Must add fixed report photo.", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
                if(!currentReport.getTimeFixed().equals("Null")){

                }

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return Reports.size();
    }

    /**
     * Sets the selection of a spinner based on the urgency that the worker set.
     *
     * @param value the value to select in the spinner
     */
    private void setSpinnerSelection(String value) {
        if (value != null) {
            int spinnerPosition = -1;
            for (int i = 0; i < urgencyLevels.length; i++) {
                if (urgencyLevels[i].equals(value)) {
                    spinnerPosition = i;
                    break;
                }
            }
            if (spinnerPosition >= 0) {
                urgencySpinner.setSelection(spinnerPosition);
            }
        }
    }

    /**
     * Gets an image from Firebase and sets it to the specified ImageView.
     * imageViewChoice =0 for reportImageView
     * else for fixedImageView
     * also made reportImageView onclick and by clicking it will open the image full screen.
     *
     * @param PhotoTime       the photo time identifier
     * @param context         the context
     * @param imageViewChoice the choice of ImageView to set the image
     */
    public void getImageFromFireBase(String PhotoTime, Context context, Integer imageViewChoice) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("imagesfromcamera/" + PhotoTime + ".jpg");
        imageRef.getBytes(1920 * 1080).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (imageViewChoice == 0) {
                reportImageView.setImageBitmap(bitmap);
                reportImageView.setVisibility(View.VISIBLE);
                reportImageView.setOnClickListener(view -> showImageDialog(bitmap));
            }
            else {
                fixedImageView.setImageBitmap(bitmap);
            }
            progressDialog.dismiss();

        }).addOnFailureListener(e -> {
            FBref.checkInternetConnection(context);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }

    /**
     * Opens the camera for capturing an image.
     */
    public void openCamera() {
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraForFixedReportLauncher.launch(openCamera);
    }

    /**
     * Opens the gallery for selecting an image.
     */
    public void openGallery() {
        Intent openGallery = new Intent(Intent.ACTION_PICK);
        openGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openGalleryForFixedReportLauncher.launch(openGallery);
    }

    /**
     * the activityForResult gets to "ReportsActivity" and by adressing the "Fixed_Report_Request" code
     * it gets back to here by doing repListAdapter.handleActivityResult() with the desired parameters
     * like that I can get activityForResult for my adapter.
     * Handles the result from an activity for capturing or selecting an image.
     *
     * @param photo       the captured photo
     * @param data        the intent data
     */
// Handle the result in your Activity
    public void handleActivityResult(Bitmap photo, @Nullable Intent data) {
        if (photo != null) {
            this.photo = photo;
            fixedImageView.setImageBitmap(photo);
        }
        else{
            System.out.println("There's no photo");
        }
    }
    /**
     * Resizes the given bitmap to the specified maximum dimension.
     * in order to take less space in firebase storage to reduce image limits.
     * @param original     the original bitmap
     * @param maxDimension the maximum dimension for resizing
     * @return the resized bitmap
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
     * Shows a dialog with the given bitmap image.
     * opens a full screen image of the fixedImageView and reportImageView by clicking on them.
     * @param bitmap the bitmap image to display
     */
    private void showImageDialog(Bitmap bitmap) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.big_image_dialog);

        ImageView dialogImageView = dialog.findViewById(R.id.dialogImageView);
        dialogImageView.setImageBitmap(bitmap);

        dialog.show();
    }
}
