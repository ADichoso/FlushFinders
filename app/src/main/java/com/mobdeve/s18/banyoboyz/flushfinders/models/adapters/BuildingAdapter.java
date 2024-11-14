package com.mobdeve.s18.banyoboyz.flushfinders.models.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.BuildingData;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.ViewBuildingActivity;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingHolder> {

    BuildingData[] buildingData;
    Context context;

    public BuildingAdapter(BuildingData[] buildingData, Context context) {
        this.buildingData = buildingData;
        this.context = context;
    }

    @NonNull
    @Override
    public BuildingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.search_building_item_list,parent,false);

        return new BuildingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingHolder holder, int position) {
        final BuildingData buildingDataList = buildingData[position];
        holder.iv_building.setImageBitmap(PictureHelper.decodeBase64ToBitmap(buildingDataList.getBuildingPicture()));
        holder.tv_restroom_building_name.setText(buildingDataList.getName());
        holder.tv_restroom_building_address.setText(buildingDataList.getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewBuildingActivity.class);

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buildingData.length;
    }


    public class BuildingHolder extends RecyclerView.ViewHolder{
        ImageView iv_building;
        TextView tv_restroom_building_name;
        TextView tv_restroom_building_address;
        TextView tv_restroom_building_walk_time;

        public BuildingHolder(@NonNull View itemView) {
            super(itemView);
            iv_building = itemView.findViewById(R.id.iv_building);

            tv_restroom_building_name = itemView.findViewById(R.id.tv_restroom_building_name);
            tv_restroom_building_address = itemView.findViewById(R.id.tv_restroom_building_address);
            tv_restroom_building_walk_time = itemView.findViewById(R.id.tv_restroom_building_walk_time);
        }
    }
}
