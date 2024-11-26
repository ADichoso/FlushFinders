package com.mobdeve.s18.banyoboyz.flushfinders.models.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.adminmode.DeleteRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomData;
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.CreateEditRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.EditRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ViewUserSuggestionsActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.SavedRestroomsActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.ViewBuildingActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.ViewRestroomActivity;

import java.util.ArrayList;

public class BuildingRestroomAdapter extends RecyclerView.Adapter<BuildingRestroomAdapter.BuildingRestroomHolder> {
    public static final String BUILDING_ID = "BUILDING_ID";
    public static final String RESTROOM_ID = "RESTROOM_ID";

    ArrayList<RestroomData> restroom_list;
    Context context;

    public BuildingRestroomAdapter(ArrayList<RestroomData> restroom_list, Context context) {
        this.restroom_list = restroom_list;
        this.context = context;
    }

    @NonNull
    @Override
    public BuildingRestroomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layout_inflater = LayoutInflater.from(parent.getContext());

        View view = new View(context);

        if(context instanceof ViewBuildingActivity)
            view = layout_inflater.inflate(R.layout.search_restroom_item_list,parent,false);

        if(context instanceof DeleteRestroomActivity || context instanceof EditRestroomActivity)
            view = layout_inflater.inflate(R.layout.restroom_item_list,parent,false);

        if(context instanceof ViewUserSuggestionsActivity)
            view = layout_inflater.inflate(R.layout.user_suggestion_item_list,parent,false);

        if(context instanceof SavedRestroomsActivity)
            view = layout_inflater.inflate(R.layout.saved_restrooms_item_list,parent,false);

        return new BuildingRestroomHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingRestroomHolder holder, int position) {
        final RestroomData restroom = restroom_list.get(position);

        if(context instanceof ViewBuildingActivity)
        {
            holder.tv_name.setText(restroom.getName());
            holder.pb_cleanliness.setProgress(restroom.getCleanliness());
            holder.pb_maintenance.setProgress(restroom.getMaintenance());
            holder.pb_vacancy.setProgress(restroom.getVacancy());
            holder.btn_view_restroom.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewRestroomActivity.class);
                intent.putExtra(BUILDING_ID, restroom.getBuildingId());
                intent.putExtra(RESTROOM_ID, restroom.getId());
                context.startActivity(intent);
            });
        }

        if(context instanceof EditRestroomActivity)
        {
            holder.itemView.setOnClickListener(v -> 
            {
                Intent i = new Intent(context, CreateEditRestroomActivity.class);

                context.startActivity(i);
            });
        }

        if(context instanceof DeleteRestroomActivity || context instanceof EditRestroomActivity)
        {
            holder.iv_building_pic.setImageBitmap(PictureHelper.decodeBase64ToBitmap(restroom.getBuildingPicture()));
        }

        if(context instanceof DeleteRestroomActivity || context instanceof EditRestroomActivity || context instanceof ViewUserSuggestionsActivity)
        {
            holder.tv_name.setText(restroom.getBuildingName());
            holder.tv_floor.setText(restroom.getName());
            holder.pb_cleanliness.setProgress(restroom.getCleanliness());
            holder.pb_maintenance.setProgress(restroom.getMaintenance());
            holder.pb_vacancy.setProgress(restroom.getVacancy());
        }
    }

    @Override
    public int getItemCount() 
    {
        return restroom_list.size();
    }


    public class BuildingRestroomHolder extends RecyclerView.ViewHolder
    {
        Button btn_view_restroom;
        ImageView iv_building_pic;
        TextView tv_name;
        TextView tv_address;
        TextView tv_floor;
        ProgressBar pb_cleanliness;
        ProgressBar pb_maintenance;
        ProgressBar pb_vacancy;
        RecyclerView rv_amenities;

        public BuildingRestroomHolder(@NonNull View itemView, Context context) 
        {
            super(itemView);
            iv_building_pic = itemView.findViewById(R.id.iv_building_pic);

            tv_name = itemView.findViewById(R.id.tv_building_name);

            if(context instanceof ViewUserSuggestionsActivity)
            {
                rv_amenities = itemView.findViewById(R.id.rv_restroom_amenities);
                tv_address = itemView.findViewById(R.id.tv_restroom_address);
                iv_building_pic = itemView.findViewById(R.id.iv_building_pic);
            }

            if(context instanceof ViewBuildingActivity)
                btn_view_restroom = itemView.findViewById(R.id.btn_view_restroom);

            if(context instanceof DeleteRestroomActivity || context instanceof EditRestroomActivity || context instanceof ViewUserSuggestionsActivity)
            {
                iv_building_pic = itemView.findViewById(R.id.iv_building_pic);
                tv_floor = itemView.findViewById(R.id.tv_restroom_floor);
            }

            pb_cleanliness = itemView.findViewById(R.id.pb_cleanliness);
            pb_maintenance = itemView.findViewById(R.id.pb_maintenance);
            pb_vacancy = itemView.findViewById(R.id.pb_vacancy);
        }
    }
}