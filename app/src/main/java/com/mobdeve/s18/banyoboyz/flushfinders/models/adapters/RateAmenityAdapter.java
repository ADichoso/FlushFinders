package com.mobdeve.s18.banyoboyz.flushfinders.models.adapters;

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
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.CreateEditRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.ViewUserSuggestionsActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.SuggestRestroomDetailsActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.ViewRestroomActivity;

public class RateAmenityAdapter extends RecyclerView.Adapter<RateAmenityAdapter.AmenityHolder> {
    AmenityData[] amenityData;
    Context context;

    public RateAmenityAdapter(AmenityData[] amenityData, Context context) {
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
            view = layoutInflater.inflate(R.layout.view_restroom_amenity_item_list,parent,false);
        }

        if(context instanceof SuggestRestroomDetailsActivity || context instanceof CreateEditRestroomActivity)
        {
            view = layoutInflater.inflate(R.layout.view_restroom_rate_amenity_item_list,parent,false);
        }

        return new AmenityHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull AmenityHolder holder, int position) {
        final AmenityData amenityDataList = amenityData[position];
        holder.iv_amenity_icon.setImageBitmap(PictureHelper.decodeBase64ToBitmap(amenityDataList.getAmenityPicture()));

        if(context instanceof ViewRestroomActivity || context instanceof ViewUserSuggestionsActivity)
        {
            holder.tv_amenity_description.setText(amenityDataList.getName());
        }

        if(context instanceof SuggestRestroomDetailsActivity || context instanceof CreateEditRestroomActivity)
        {
            holder.sw_amenity_description.setText(amenityDataList.getName());
        }

    }

    @Override
    public int getItemCount() {
        return amenityData.length;
    }


    public class AmenityHolder extends RecyclerView.ViewHolder{
        ImageView iv_amenity_icon;
        TextView tv_amenity_description;
        Switch sw_amenity_description;
        public AmenityHolder(@NonNull View itemView, Context context) {
            super(itemView);
            iv_amenity_icon = itemView.findViewById(R.id.iv_amenity_icon);

            if(context instanceof ViewRestroomActivity || context instanceof ViewUserSuggestionsActivity)
            {
                tv_amenity_description = itemView.findViewById(R.id.tv_amenity_description);
            }

            if(context instanceof SuggestRestroomDetailsActivity || context instanceof CreateEditRestroomActivity)
            {
                sw_amenity_description = itemView.findViewById(R.id.sw_amenity_description);
            }
        }
    }
}
