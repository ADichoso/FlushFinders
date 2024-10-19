package com.mobdeve.s18.banyoboyz.flushfinders.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.CreateEditRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ViewUserSuggestionsActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.SuggestRestroomDetailsActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.ViewRestroomActivity;

public class AmenitiesAdapter extends RecyclerView.Adapter<AmenitiesAdapter.AmenityHolder> {

    AmenityData[] amenityData;
    Context context;

    public AmenitiesAdapter(AmenityData[] amenityData, Context context) {
        this.amenityData = amenityData;
        this.context = context;
    }

    @NonNull
    @Override
    public AmenityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = new View(context);

        if(context instanceof ViewRestroomActivity || context instanceof ViewUserSuggestionsActivity)
        {
            view = layoutInflater.inflate(R.layout.view_restroom_amenities_item_list,parent,false);
        }

        if(context instanceof SuggestRestroomDetailsActivity || context instanceof CreateEditRestroomActivity)
        {
            view = layoutInflater.inflate(R.layout.view_restroom_rate_amenities_item_list,parent,false);
        }

        return new AmenityHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull AmenityHolder holder, int position) {
        final AmenityData amenityDataList = amenityData[position];
        holder.iv_amenities_icon.setImageResource(amenityDataList.getAmenityPictureResource());

        if(context instanceof ViewRestroomActivity || context instanceof ViewUserSuggestionsActivity)
        {
            holder.tv_amenities_description.setText(amenityDataList.getName());
        }

        if(context instanceof SuggestRestroomDetailsActivity || context instanceof CreateEditRestroomActivity)
        {
            holder.sw_amenities_description.setText(amenityDataList.getName());
        }

    }

    @Override
    public int getItemCount() {
        return amenityData.length;
    }


    public class AmenityHolder extends RecyclerView.ViewHolder{
        ImageView iv_amenities_icon;
        TextView tv_amenities_description;
        Switch sw_amenities_description;
        public AmenityHolder(@NonNull View itemView, Context context) {
            super(itemView);
            iv_amenities_icon = itemView.findViewById(R.id.iv_amenities_icon);

            if(context instanceof ViewRestroomActivity || context instanceof ViewUserSuggestionsActivity)
            {
                tv_amenities_description = itemView.findViewById(R.id.tv_amenities_description);
            }

            if(context instanceof SuggestRestroomDetailsActivity || context instanceof CreateEditRestroomActivity)
            {
                sw_amenities_description = itemView.findViewById(R.id.sw_amenities_description);
            }
        }
    }
}
