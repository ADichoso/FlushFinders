package com.mobdeve.s18.banyoboyz.flushfinders.models.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.EditRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ViewUserSuggestionsActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.CreateEditRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.SavedRestroomsActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.ViewBuildingActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.ViewRestroomActivity;

import java.util.ArrayList;

public class DeleteRestroomAdapter extends RecyclerView.Adapter<DeleteRestroomAdapter.DeleteRestroomHolder> {
    public static final String BUILDING_ID = "BUILDING_ID";
    public static final String RESTROOM_ID = "RESTROOM_ID";

    ArrayList<RestroomData> restroomData;
    ArrayList<DeleteRestroomHolder> selected_holders;

    Context context;

    public void clearSelectedHolders()
    {
        for(DeleteRestroomHolder deleteRestroomHolder : selected_holders)
            deleteRestroomHolder.toggleSelection(false);

        selected_holders.clear();
    }

    public ArrayList<RestroomData> getRestroomData() {
        return restroomData;
    }

    public DeleteRestroomAdapter(ArrayList<RestroomData> restroomData, Context context) {
        this.restroomData = restroomData;
        this.context = context;
    }

    @NonNull
    @Override
    public DeleteRestroomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view  = layoutInflater.inflate(R.layout.restroom_item_list,parent,false);
        return new DeleteRestroomHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull DeleteRestroomHolder holder, int position) {
        final RestroomData restroomDataList = restroomData.get(position);

        holder.tv_building_name.setText(restroomDataList.getBuildingName());
        holder.tv_restroom_building_address.setText(restroomDataList.getBuildingAddress());
        holder.tv_restroom_floor.setText(restroomDataList.getName());
        holder.iv_building_pic.setImageBitmap(PictureHelper.decodeBase64ToBitmap(restroomDataList.getBuildingPicture()));
        holder.pb_cleanliness.setProgress(restroomDataList.getCleanliness());
        holder.pb_maintenance.setProgress(restroomDataList.getMaintenance());
        holder.pb_vacancy.setProgress(restroomDataList.getVacancy());

        holder.itemView.setOnClickListener(view -> {
            holder.toggleSelection(!holder.is_selected);

            if(holder.is_selected)
                selected_holders.add(holder);
            else
                selected_holders.remove(holder);
        });
    }

    @Override
    public int getItemCount() {
        return restroomData.size();
    }


    public class DeleteRestroomHolder extends RecyclerView.ViewHolder{
        protected boolean is_selected;
        ImageView iv_building_pic;
        TextView tv_building_name;
        TextView tv_restroom_building_address;
        TextView tv_restroom_floor;
        ProgressBar pb_cleanliness;
        ProgressBar pb_maintenance;
        ProgressBar pb_vacancy;

        public DeleteRestroomHolder(@NonNull View itemView, Context context) {
            super(itemView);
            iv_building_pic = itemView.findViewById(R.id.iv_building_pic);
            tv_building_name = itemView.findViewById(R.id.tv_building_name);
            tv_restroom_floor = itemView.findViewById(R.id.tv_restroom_floor);
            tv_restroom_building_address = itemView.findViewById(R.id.tv_restroom_building_address);

            pb_cleanliness = itemView.findViewById(R.id.pb_cleanliness);
            pb_maintenance = itemView.findViewById(R.id.pb_maintenance);
            pb_vacancy = itemView.findViewById(R.id.pb_vacancy);

            toggleSelection(false);
        }

        public void toggleSelection(boolean is_selected)
        {
            this.is_selected = is_selected;

            if(is_selected)
                itemView.setBackgroundColor(Color.rgb(188, 188, 188));
            else
                itemView.setBackgroundColor(Color.rgb(255, 255, 255));
        }
    }
}
