package com.example.easyfix.Adapters;

import static android.app.Activity.RESULT_OK;
import static com.example.easyfix.FBref.currentUser;
import static com.example.easyfix.FBref.refReports;
import static com.example.easyfix.FBref.refReportsDone;
import static com.example.easyfix.FBref.refUsers;
import static java.lang.Long.parseLong;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfix.Activites.ReportsActivity;
import com.example.easyfix.Classes.Building;
import com.example.easyfix.R;
import com.example.easyfix.Classes.Report;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * The type Task list adapter.
 */
public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ViewHolder> {

    private ArrayList<Report> Reports;
    ArrayList<Building> Buildings;
    private Context context;
    Spinner urgencySpinner;
    Button updateButton, deleteButton, takeJobButton, finishReportButton;
    ImageView reportImageView, fixedImageView;
    TextView reportTitleTV, reporterTV, extraInformationTV, urgencyTV, buildingTV, areaTV, reportDateTV, workingOnTheFixTV, fixedImageTV;
    private String[] urgencyLevels = {"Not set yet", "Low", "Medium", "High"};
    String urgency;
    Bitmap photo;

    public static final int REQUEST_CAMERA_PERMISSION = 200;
    public static final int OPEN_CAMERA_FOR_FIXED_REPORT_REQUEST = 201;
    public static final int OPEN_GALLERY_FOR_FIXED_REPORT_REQUEST = 300;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ReportNameTv, ReportExInfoTv, ReportAreaTv;
        private final CardView cardView;
        private final ImageView urgencyIcon;

        LinearLayout containerll;

        public ViewHolder(View view) {
            super(view);

            ReportNameTv = view.findViewById(R.id.reportsNameTv);
            ReportExInfoTv = view.findViewById(R.id.reportExInfoTv);
            urgencyIcon = view.findViewById(R.id.urgencyIcon);
            ReportAreaTv = view.findViewById(R.id.reportAreaTv);
            cardView = view.findViewById(R.id.cardId);
            containerll = view.findViewById(R.id.containerLL);
        }

        public TextView getTextView() {
            return ReportNameTv;
        }
    }

    public ReportListAdapter(ArrayList<Report> taskDataset, ArrayList<Building> buildings, Context context) {
        this.Reports = taskDataset;
        Buildings = buildings;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_report, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Report currentReport = Reports.get(position);

        viewHolder.ReportNameTv.setText(currentReport.getReportMainType());
        viewHolder.ReportExInfoTv.setText(currentReport.getExtraInformation());
        viewHolder.ReportAreaTv.setText(Buildings.get(currentReport.getMalfunctionArea() + 1).getBuildingName());

        urgency = Reports.get(position).getUrgencyLevel();

        viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#B3E5FC"));

        if (!currentReport.getMalfunctionFixer().equals("Null")) {
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#FFC107"));
        }
        if(!currentReport.getTimeFixed().equals("Null")) // if a report is finished it should be green
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#4CAF50"));

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
            @Override
            public boolean onLongClick(View view) {
                View view2 = LayoutInflater.from(context).inflate(R.layout.openedreport_dialog, null);
                ConstraintLayout adapterConstraintLayout = view2.findViewById(R.id.openedReportDialogConstraintLayout);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.openedreport_dialog, adapterConstraintLayout);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

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
                    // which mean the report is done, he shouldn't see those buttons and it should stay there.
                    urgencySpinner.setEnabled(true);
                    updateButton.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.VISIBLE);
                    takeJobButton.setVisibility(View.VISIBLE);
                } else {
                    urgencySpinner.setEnabled(false);
                }

                if (!currentReport.getMalfunctionFixer().equals("Null") && currentReport.getTimeFixed().equals("Null")) {
                    if (currentReport.getMalfunctionFixer().equals(currentUser.getUserName())) {
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

                reportTitleTV.setText(Reports.get(position).getReportMainType());
                reporterTV.setText("Reporter: " + currentReport.getReporter());
                extraInformationTV.setText("Extra Information: " + currentReport.getExtraInformation());
                buildingTV.setText("Building: " + Buildings.get(currentReport.getMalfunctionArea() + 1).getBuildingName());
                areaTV.setText("Room: " + Buildings.get(currentReport.getMalfunctionArea() + 1).getRooms().get(Reports.get(position).getMalfunctionRoom()));
                reportDateTV.setText("Report Date: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date(parseLong(currentReport.getTimeReported()))));

                if (currentReport.getMalfunctionFixer().equals("Null")) {
                    workingOnTheFixTV.setText("Working On The Fix: No one yet");
                } else {
                    workingOnTheFixTV.setText("Working On The Fix: " + currentReport.getMalfunctionFixer());
                }
                if(!currentReport.getTimeFixed().equals("Null")){
                    // the report is fixed already, which means it should be "worked" instead of "working" and also add the fixed image
                    workingOnTheFixTV.setText("Worked On The Fix: " + currentReport.getMalfunctionFixer());
                    getImageFromFireBase(currentReport.getFixedPhoto(), context, 1);
                }

                if (!currentReport.getReportPhoto().equals("Null")) {
                    getImageFromFireBase(currentReport.getReportPhoto(), context, 0);
                    reportImageView.setVisibility(View.VISIBLE);
                }

                if (currentReport.getMalfunctionFixer().equals(currentUser.getUserName()) || !currentReport.getFixedPhoto().equals("Null")) {
                    fixedImageTV.setVisibility(View.VISIBLE);
                    fixedImageView.setVisibility(View.VISIBLE);

                    if (currentReport.getMalfunctionFixer().equals(currentUser.getUserName()))
                        finishReportButton.setVisibility(View.VISIBLE);
                }

                fixedImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                                                openGallery();
                                                break;
                                        }
                                    }
                                });
                        builder.create().show();
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
                            String finalPreviousUrgency = previousUrgency;
                            refReports.child(currentReport.getTimeReported()).child("urgencyLevel").setValue(newUrgency).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    if (currentReport.getUrgencyLevel().equals("Null"))
                                        Toast.makeText(context, "Urgency for '" + currentReport.getReportMainType() + "' Was set to " + urgencySpinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
                                    else
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
                            refReports.child(currentReport.getTimeReported()).child("malfunctionFixer").setValue("Null").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Successfully left '" + currentReport.getReportMainType() + "' job.", Toast.LENGTH_LONG).show();
                                    alertDialog.dismiss();
                                }
                            });
                        } else {
                            if (currentReport.getUrgencyLevel().equals("Null")) {
                                Toast.makeText(context, "Please set urgency level first and update it.", Toast.LENGTH_SHORT).show();
                            } else {
                                refReports.child(currentReport.getTimeReported()).child("malfunctionFixer").setValue(currentUser.getUserName()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Report '" + currentReport.getReportMainType() + "' has been claimed by " + currentUser.getUserName() + ". Good Luck!", Toast.LENGTH_LONG).show();
                                        alertDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Something Failed, Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
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
                                alertDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Something Failed, Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                finishReportButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(photo != null) {
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
                                            Toast.makeText(context, "Something Failed, Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    alertDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Something Failed, Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
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

    public void getImageFromFireBase(String PhotoTime, Context context, Integer imageViewChoice) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("imagesfromcamera/" + PhotoTime + ".jpg");
        imageRef.getBytes(1920 * 1080).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if(imageViewChoice == 0)
                reportImageView.setImageBitmap(bitmap);
            else
                fixedImageView.setImageBitmap(bitmap);

        }).addOnFailureListener(e -> {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void openCamera() {
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((Activity) context).startActivityForResult(openCamera, OPEN_CAMERA_FOR_FIXED_REPORT_REQUEST);
    }
    public void openGallery() {
        Intent openGallery = new Intent(Intent.ACTION_PICK);
        openGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) context).startActivityForResult(openGallery, OPEN_GALLERY_FOR_FIXED_REPORT_REQUEST);
    }

    // Handle the result in your Activity
    public void handleActivityResult(int requestCode, Bitmap photo, @Nullable Intent data) {
        if (photo != null) {
            this.photo = photo;
            fixedImageView.setImageBitmap(photo);
        }
        else{
            System.out.println("There's no photo");
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
}
