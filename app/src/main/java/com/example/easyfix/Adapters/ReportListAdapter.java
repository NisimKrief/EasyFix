package com.example.easyfix.Adapters;

import static com.example.easyfix.FBref.currentUser;
import static com.example.easyfix.FBref.refReports;
import static com.example.easyfix.FBref.refUsers;
import static java.lang.Long.parseLong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyfix.Classes.Building;
import com.example.easyfix.R;
import com.example.easyfix.Classes.Report;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * The type Task list adapter.
 */
public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ViewHolder> {

    private ArrayList<Report> Reports;
    ArrayList<Building> Buildings;
    private Context context; // To know where to show the alertDialog.
    Spinner urgencySpinner;
    Button updateButton, deleteButton, takeJobButton;
    ImageView reportImageView;
    TextView reportTitleTV, reporterTV, extraInformationTV, urgencyTV, buildingTV, areaTV, reportConditionTV, reportDateTV, workingOnTheFixTV;
    private String[] urgencyLevels = {"Not set yet", "Low", "Medium", "High"};
    String urgency;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ReportNameTv, ReportExInfoTv, ReportAreaTv;
        private final CardView cardView;
        private final ImageView urgencyIcon;

        /**
         * The Containerll.
         */
        LinearLayout containerll;



        /**
         * Instantiates a new View holder.
         *
         * @param view the view
         */
        public ViewHolder(View view) {
            super(view);

            ReportNameTv = (TextView) view.findViewById(R.id.reportsNameTv);
            ReportExInfoTv = (TextView) view.findViewById(R.id.reportExInfoTv);
            urgencyIcon = view.findViewById(R.id.urgencyIcon);
            ReportAreaTv = (TextView) view.findViewById(R.id.reportAreaTv);
            cardView = (CardView) view.findViewById(R.id.cardId);
            containerll = (LinearLayout) view.findViewById(R.id.containerLL);

        }

        /**
         * Gets text view.
         *
         * @return the text view
         */
        public TextView getTextView() {
            return ReportNameTv;
        }
    }

    /**
     * Instantiates a new Task list adapter.
     *
     * @param taskDataset the task dataset
     */
    public ReportListAdapter(ArrayList<Report> taskDataset, ArrayList<Building> buildings, Context context) {
        this.Reports = taskDataset;
        Buildings = buildings;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_report, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Report currentReport = Reports.get(position);

        viewHolder.ReportNameTv.setText(currentReport.getReportMainType());
        viewHolder.ReportExInfoTv.setText(currentReport.getExtraInformation());
        viewHolder.ReportAreaTv.setText(Buildings.get(currentReport.getMalfunctionArea() + 1).getBuildingName());

        urgency = Reports.get(position).getUrgencyLevel();

        // Set default background color (for the card view)
        viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#B3E5FC"));

        // Check if there's someone on the job, set card color to yellow
        if (!currentReport.getMalfunctionFixer().equals("Null")) {
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#FFC107"));
        }

        // Adjust the urgency level display
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

                urgencySpinner = (Spinner) dialogView.findViewById(R.id.urgencySpinner);

                updateButton = (Button) dialogView.findViewById(R.id.updateButton);
                deleteButton = (Button) dialogView.findViewById(R.id.deleteButton);
                takeJobButton = (Button) dialogView.findViewById(R.id.takeJobButton);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,  android.R.layout.simple_spinner_item, urgencyLevels);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                urgencySpinner.setAdapter(adapter);
                urgency = Reports.get(position).getUrgencyLevel();

                if (urgency.equals("JMedium")) // I Call it JMedium so it will sort correctly by urgency (high,medium,low)
                    urgency = "Medium";
                setSpinnerSelection(urgency);
                if (currentUser.getUserLevel() >= 100) { // only if the user is construction worker or higher (manager,admin)
                    urgencySpinner.setEnabled(true);
                    updateButton.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.VISIBLE);
                    takeJobButton.setVisibility(View.VISIBLE);
                } else {
                    urgencySpinner.setEnabled(false);
                }
                if(!currentReport.getMalfunctionFixer().equals("Null")){
                    takeJobButton.setVisibility(View.GONE);
                }
                reportImageView = (ImageView) dialogView.findViewById(R.id.reportImageView);

                reportTitleTV = (TextView) dialogView.findViewById(R.id.openedReportTitle);
                reporterTV = (TextView) dialogView.findViewById(R.id.reporterLabel);
                extraInformationTV = (TextView) dialogView.findViewById(R.id.extraInfoLabel);
                urgencyTV = (TextView) dialogView.findViewById(R.id.urgencyLabel);
                buildingTV = (TextView) dialogView.findViewById(R.id.buildingLabel);
                areaTV = (TextView) dialogView.findViewById(R.id.areaLabel);
                reportConditionTV = (TextView) dialogView.findViewById(R.id.conditionLabel);
                reportDateTV = (TextView) dialogView.findViewById(R.id.dateTextView);
                workingOnTheFixTV = (TextView) dialogView.findViewById(R.id.workingOnTheFixLabel);

                reportTitleTV.setText(Reports.get(position).getReportMainType());
                reporterTV.setText("Reporter: " +currentReport.getReporter());
                extraInformationTV.setText("Extra Information: " +currentReport.getExtraInformation());
                buildingTV.setText("Building: " +Buildings.get(currentReport.getMalfunctionArea() + 1).getBuildingName());
                areaTV.setText("Room: " +Buildings.get(currentReport.getMalfunctionArea() + 1).getRooms().get(Reports.get(position).getMalfunctionRoom()));
                reportDateTV.setText("Report Date: " +new SimpleDateFormat("dd/MM/yyyy").format(new Date(parseLong(currentReport.getTimeReported())))); // making the date simple (dd,mm,yyyy)
                workingOnTheFixTV.setText("Working On The Fix: " +currentReport.getMalfunctionFixer());

                if(!currentReport.getReportPhoto().equals("Null")){
                    System.out.println(currentReport.getReportPhoto());
                    getImageFromFireBase(currentReport.getReportPhoto(), context);
                    reportImageView.setVisibility(View.VISIBLE);
                }

                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newUrgency = (String) urgencySpinner.getSelectedItem();
                        String previousUrgency = currentReport.getUrgencyLevel();
                        if(previousUrgency.equals("JMedium"))
                            previousUrgency = "Medium";
                        if(newUrgency.equals("Medium"))
                            newUrgency = "JMedium"; // I call it JMedium so it will sort it by urgency correctly
                        if(!currentReport.getUrgencyLevel().equals(newUrgency) && urgencySpinner.getSelectedItemPosition() != 0){
                            String finalPreviousUrgency = previousUrgency;
                            refReports.child(currentReport.getTimeReported()).child("urgencyLevel").setValue(newUrgency).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    if(currentReport.getUrgencyLevel().equals("Null")) // if they set the urgency for the first time.
                                        Toast.makeText(context, "Urgency for '" + currentReport.getReportMainType() + "' Was set to "+ urgencySpinner.getSelectedItem() , Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(context, "Urgency for '" + currentReport.getReportMainType() + "' changed from " + finalPreviousUrgency + " to " + urgencySpinner.getSelectedItem() , Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                            });
                        }
                        else if(urgencySpinner.getSelectedItemPosition() == 0){
                            Toast.makeText(context, "Report must have urgency level" , Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                takeJobButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(urgencySpinner.getSelectedItemPosition() == 0){
                            Toast.makeText(context, "Please set urgency level first.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            refReports.child(currentReport.getTimeReported()).child("malfunctionFixer").setValue(currentUser.getUserName()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Report '" + currentReport.getReportMainType() + "' has claimed by " + currentUser.getUserName() + " Good Luck!", Toast.LENGTH_LONG).show();
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
                return true;
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Reports.size();
    }
    private void setSpinnerSelection(String value) {
        System.out.println(value);
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


    public void getImageFromFireBase(String PhotoTime, Context context) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("imagesfromcamera/" + PhotoTime + ".jpg");
        imageRef.getBytes(1920*1080).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            reportImageView.setImageBitmap(bitmap);
        }).addOnFailureListener(e -> {
            Toast.makeText(context, e.getMessage().toString() , Toast.LENGTH_SHORT).show();
        });
    }

}