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
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomData;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.ViewRestroomActivity;

import java.util.ArrayList;

public class SavedRestroomAdapter extends RecyclerView.Adapter<SavedRestroomAdapter.SavedRestroomHolder>
{
    public static final String BUILDING_ID = "BUILDING_ID";
    public static final String RESTROOM_ID = "RESTROOM_ID";

    ArrayList<RestroomData> restroom_list;
    Context context;

    public SavedRestroomAdapter(ArrayList<RestroomData> restroom_list, Context context) 
    {
        this.restroom_list = restroom_list;
        this.context = context;
    }

    @NonNull
    @Override
    public SavedRestroomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layout_inflater = LayoutInflater.from(parent.getContext());
        View view = layout_inflater.inflate(R.layout.saved_restrooms_item_list,parent,false);
        return new SavedRestroomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedRestroomHolder holder, int position)
    {
        final RestroomData restroom = restroom_list.get(position);

        holder.iv_building_pic.setImageBitmap(PictureHelper.decodeBase64ToBitmap(restroom.getBuildingPicture()));
        holder.tv_building_name.setText(restroom.getBuildingName());
        holder.tv_restroom_building_address.setText(restroom.getBuildingAddress());
        holder.tv_restroom_name.setText(restroom.getName());
        holder.pb_cleanliness.setProgress(restroom.getCleanliness());
        holder.pb_maintenance.setProgress(restroom.getMaintenance());
        holder.pb_vacancy.setProgress(restroom.getVacancy());
        holder.btn_view_directions.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewRestroomActivity.class);
            intent.putExtra(BUILDING_ID, restroom.getBuildingId());
            intent.putExtra(RESTROOM_ID, restroom.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() 
    {
        return restroom_list.size();
    }


    public class SavedRestroomHolder extends RecyclerView.ViewHolder
    {
        ImageView iv_building_pic;
        TextView tv_building_name;
        TextView tv_restroom_building_address;
        TextView tv_restroom_name;
        ProgressBar pb_cleanliness;
        ProgressBar pb_maintenance;
        ProgressBar pb_vacancy;
        Button btn_view_directions;

        public SavedRestroomHolder(@NonNull View itemView)
        {
            super(itemView);
            iv_building_pic = itemView.findViewById(R.id.iv_building_pic);
            tv_building_name = itemView.findViewById(R.id.tv_building_name);
            tv_restroom_building_address = itemView.findViewById(R.id.tv_restroom_building_address);
            tv_restroom_name = itemView.findViewById(R.id.tv_restroom_name);
            pb_cleanliness = itemView.findViewById(R.id.pb_cleanliness);
            pb_maintenance = itemView.findViewById(R.id.pb_maintenance);
            pb_vacancy = itemView.findViewById(R.id.pb_vacancy);
            btn_view_directions = itemView.findViewById(R.id.btn_view_directions);
        }
    }
}
