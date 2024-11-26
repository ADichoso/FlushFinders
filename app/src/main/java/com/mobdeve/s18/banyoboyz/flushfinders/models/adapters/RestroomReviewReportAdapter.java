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
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomReviewReportData;

import java.util.ArrayList;

public class RestroomReviewReportAdapter extends RecyclerView.Adapter<RestroomReviewReportAdapter.RestroomReviewReportHolder>
{

    ArrayList<RestroomReviewReportData> restroom_review_report_list;
    Context context;

    public RestroomReviewReportAdapter(ArrayList<RestroomReviewReportData> restroom_review_report_list, Context context)
    {
        this.restroom_review_report_list = restroom_review_report_list;
        this.context = context;
    }

    @NonNull
    @Override
    public RestroomReviewReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layout_inflater = LayoutInflater.from(parent.getContext());
        View view = layout_inflater.inflate(R.layout.user_report_item_list,parent,false);

        return new RestroomReviewReportHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestroomReviewReportHolder holder, int position)
    {
        final RestroomReviewReportData restroom_review_report = restroom_review_report_list.get(position);
        holder.iv_building_pic.setImageBitmap(PictureHelper.decodeBase64ToBitmap(restroom_review_report.getBuildingPicture()));
        holder.rb_rating.setRating(restroom_review_report.getRating());
        holder.tv_report.setText(restroom_review_report.getReviewReport());
        holder.tv_name.setText(restroom_review_report.getBuildingName());
        holder.tv_floor.setText(restroom_review_report.getReviewReport());
        holder.pb_cleanliness.setProgress(restroom_review_report.getCleanliness());
        holder.pb_maintenance.setProgress(restroom_review_report.getMaintenance());
        holder.pb_vacancy.setProgress(restroom_review_report.getVacancy());
    }

    @Override
    public int getItemCount() {
        return restroom_review_report_list.size();
    }


    public class RestroomReviewReportHolder extends RecyclerView.ViewHolder
    {
        ImageView iv_building_pic;
        RatingBar rb_rating;
        TextView tv_report;
        TextView tv_name;
        TextView tv_floor;
        ProgressBar pb_cleanliness;
        ProgressBar pb_maintenance;
        ProgressBar pb_vacancy;

        public RestroomReviewReportHolder(@NonNull View itemView)
        {
            super(itemView);
            iv_building_pic = itemView.findViewById(R.id.iv_building_pic);

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
