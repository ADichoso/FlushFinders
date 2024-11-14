package com.mobdeve.s18.banyoboyz.flushfinders.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.banyoboyz.flushfinders.R;

public class RestroomReviewReportAdapter extends RecyclerView.Adapter<RestroomReviewReportAdapter.RestroomReviewReportHolder> {

    RestroomReviewReportData[] restroomReviewReportData;
    Context context;

    public RestroomReviewReportAdapter(RestroomReviewReportData[] restroomReviewReportData, Context context) {
        this.restroomReviewReportData = restroomReviewReportData;
        this.context = context;
    }

    @NonNull
    @Override
    public RestroomReviewReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.user_report_item_list,parent,false);

        return new RestroomReviewReportHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestroomReviewReportHolder holder, int position) {
        final RestroomReviewReportData restroomReviewReportDataList = restroomReviewReportData[position];
        holder.iv_building_pic.setImageResource(restroomReviewReportDataList.getBuildingImageResource());
        holder.tv_name.setText(restroomReviewReportDataList.getBuildingName());
        holder.tv_floor.setText(restroomReviewReportDataList.getName());

        holder.rb_rating.setRating(restroomReviewReportDataList.getRating());
        holder.tv_report.setText(restroomReviewReportDataList.getReviewReport());


        //holder.pb_cleanliness.setProgress(restroomReviewReportDataList.getMetrics().getCleanliness());
        //holder.pb_maintenance.setProgress(restroomReviewReportDataList.getMetrics().getMaintenance());
        //holder.pb_vacancy.setProgress(restroomReviewReportDataList.getMetrics().getVacancy());
    }

    @Override
    public int getItemCount() {
        return restroomReviewReportData.length;
    }


    public class RestroomReviewReportHolder extends RecyclerView.ViewHolder{
        ImageView iv_building_pic;
        RatingBar rb_rating;
        TextView tv_report;
        TextView tv_name;
        TextView tv_floor;
        ProgressBar pb_cleanliness;
        ProgressBar pb_maintenance;
        ProgressBar pb_vacancy;

        public RestroomReviewReportHolder(@NonNull View itemView) {
            super(itemView);
            iv_building_pic = itemView.findViewById(R.id.iv_building);

            rb_rating = itemView.findViewById(R.id.rb_restroom_rating);
            tv_report = itemView.findViewById(R.id.tv_user_review_report);

            tv_name = itemView.findViewById(R.id.tv_restroom_building);
            tv_floor = itemView.findViewById(R.id.tv_restroom_floor);

            pb_cleanliness = itemView.findViewById(R.id.pb_cleanliness);
            pb_maintenance = itemView.findViewById(R.id.pb_maintenance);
            pb_vacancy = itemView.findViewById(R.id.pb_vacancy);
        }
    }
}
