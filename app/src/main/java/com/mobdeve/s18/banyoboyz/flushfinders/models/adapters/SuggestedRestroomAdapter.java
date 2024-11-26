package com.mobdeve.s18.banyoboyz.flushfinders.models.adapters;

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
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ViewUserSuggestionsActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.ViewRestroomActivity;

import java.util.ArrayList;

public class SuggestedRestroomAdapter extends RecyclerView.Adapter<SuggestedRestroomAdapter.SuggestedRestroomHolder>
{
    public static final String BUILDING_ID = "BUILDING_ID";
    public static final String RESTROOM_ID = "RESTROOM_ID";

    ArrayList<RestroomData> restroom_list;
    ViewUserSuggestionsActivity context;

    public SuggestedRestroomAdapter(ArrayList<RestroomData> restroom_list, ViewUserSuggestionsActivity context) 
    {
        this.restroom_list = restroom_list;
        this.context = context;
    }

    @NonNull
    @Override
    public SuggestedRestroomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layout_inflater = LayoutInflater.from(parent.getContext());
        View view = layout_inflater.inflate(R.layout.suggested_restrooms_item_list,parent,false);
        return new SuggestedRestroomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestedRestroomHolder holder, int position) {
        final RestroomData restroom = restroom_list.get(position);

        holder.iv_building_pic.setImageBitmap(PictureHelper.decodeBase64ToBitmap(restroom.getBuildingPicture()));
        holder.tv_building_name.setText(restroom.getBuildingName());
        holder.tv_restroom_building_address.setText(restroom.getBuildingAddress());
        holder.tv_restroom_name.setText(restroom.getName());
        holder.pb_cleanliness.setProgress(restroom.getCleanliness());
        holder.pb_maintenance.setProgress(restroom.getMaintenance());
        holder.pb_vacancy.setProgress(restroom.getVacancy());
        holder.btn_view_restroom.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewRestroomActivity.class);
            intent.putExtra(BUILDING_ID, restroom.getBuildingId());
            intent.putExtra(RESTROOM_ID, restroom.getId());
            context.startActivity(intent);
        });
        holder.btn_resolve_suggestion.setOnClickListener(view -> {
            context.deleteUserSuggestion(restroom.getBuildingId());
        });
    }

    @Override
    public int getItemCount() {
        return restroom_list.size();
    }

    public void removeAllRestroomsWithIDs(ArrayList<String> restroom_ids) {
        ArrayList<RestroomData> new_restroom_list = new ArrayList<RestroomData>();
        for(RestroomData restroom : restroom_list)
        {
            if(!restroom_ids.contains(restroom.getId()))
                new_restroom_list.add(restroom);
        }

        restroom_list = new_restroom_list;
    }


    public class SuggestedRestroomHolder extends RecyclerView.ViewHolder
    {
        ImageView iv_building_pic;
        TextView tv_building_name;
        TextView tv_restroom_building_address;
        TextView tv_restroom_name;
        ProgressBar pb_cleanliness;
        ProgressBar pb_maintenance;
        ProgressBar pb_vacancy;
        Button btn_view_restroom;
        Button btn_resolve_suggestion;

        public SuggestedRestroomHolder(@NonNull View itemView) {
            super(itemView);
            iv_building_pic = itemView.findViewById(R.id.iv_building_pic);
            tv_building_name = itemView.findViewById(R.id.tv_building_name);
            tv_restroom_building_address = itemView.findViewById(R.id.tv_restroom_building_address);
            tv_restroom_name = itemView.findViewById(R.id.tv_restroom_name);
            pb_cleanliness = itemView.findViewById(R.id.pb_cleanliness);
            pb_maintenance = itemView.findViewById(R.id.pb_maintenance);
            pb_vacancy = itemView.findViewById(R.id.pb_vacancy);
            btn_view_restroom = itemView.findViewById(R.id.btn_view_restroom);
            btn_resolve_suggestion = itemView.findViewById(R.id.btn_resolve_suggestion);
        }
    }
}
