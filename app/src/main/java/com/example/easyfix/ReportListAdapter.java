package com.example.easyfix;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The type Task list adapter.
 */
public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ViewHolder> {

    private ArrayList<Report> Reports;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView ReportNameTv, ReportStatusTv;
        private final CardView cardView;

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

            ReportNameTv = (TextView) view.findViewById(R.id.reportNameTv);
            ReportStatusTv = (TextView) view.findViewById(R.id.reportStatusTv);
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
    public ReportListAdapter(ArrayList<Report> taskDataset) {
        this.Reports = taskDataset;
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

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.ReportNameTv.setText(Reports.get(position).getReportMainType());
        viewHolder.ReportStatusTv.setText(Reports.get(position).getExtraInformation());
        System.out.println(viewHolder.ReportStatusTv.getText());


        String status = Reports.get(position).getUrgencyLevel();

        viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        if (status != null) {
            if (status.toLowerCase().equals("medium")) {
                viewHolder.ReportStatusTv.setTextColor(Color.parseColor("#FFD300"));
            } else if (status.toLowerCase().equals("high")) {
                viewHolder.ReportStatusTv.setTextColor(Color.parseColor("#d43751"));
                // viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#8AFFA9"));
            } else {
                viewHolder.ReportStatusTv.setTextColor(Color.parseColor("#FFFFFF"));
            }
        } else {
            viewHolder.ReportStatusTv.setTextColor(Color.parseColor("#000000"));
        }

        /* viewHolder.containerll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), viewHolder.containerll);
                popupMenu.inflate(R.menu.taskmenu);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getItemId()==R.id.deleteMenu){
                            FirebaseFirestore.getInstance().collection("tasks").document(taskDataset.get(position).getTaskId()).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            taskDataset.remove(position);
                                            notifyItemRemoved(position);
                                            Toast.makeText(view.getContext() , "Task Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            return true;

                        }
                        if(menuItem.getItemId()==R.id.confirmMenu){

                            TaskModel completedTask = taskDataset.get(position);
                            completedTask.setTaskStatus("COMPLETED");
                            FirebaseFirestore.getInstance().collection("tasks").document(taskDataset.get(position).getTaskId())
                                    .set(completedTask).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(view.getContext() , "Task Marked As Completed", Toast.LENGTH_SHORT).show();
                                            viewHolder.taskStatusTv.setText("COMPLETED");
                                            notifyItemChanged(position);
                                        }
                                    });



                        }




                        return false;
                    }
                });




                return false;
            }
        }); */

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Reports.size();
    }
}