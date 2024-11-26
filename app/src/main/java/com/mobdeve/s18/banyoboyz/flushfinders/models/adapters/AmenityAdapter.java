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
import com.mobdeve.s18.banyoboyz.flushfinders.usermode.ViewRestroomActivity;

import java.util.ArrayList;

public class AmenityAdapter extends RecyclerView.Adapter<AmenityAdapter.AmenityHolder>
{
    ArrayList<AmenityData> amenity_list;
    Context context;

    public AmenityAdapter(ArrayList<AmenityData>  amenity_list, Context context)
    {
        this.amenity_list = amenity_list;
        this.context = context;
    }

    @NonNull
    @Override
    public AmenityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layout_inflater = LayoutInflater.from(parent.getContext());
        View view = new View(context);

        if(context instanceof ViewRestroomActivity || context instanceof ViewUserSuggestionsActivity)
            view = layout_inflater.inflate(R.layout.view_restroom_amenity_item_list,parent,false);

        if(context instanceof CreateEditRestroomActivity)
            view = layout_inflater.inflate(R.layout.view_restroom_rate_amenity_item_list,parent,false);

        return new AmenityHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull AmenityHolder holder, int position)
    {
        final AmenityData amenity = amenity_list.get(position);
        holder.iv_amenity_icon.setImageBitmap(PictureHelper.decodeBase64ToBitmap(amenity.getAmenityPicture()));

        if(context instanceof ViewRestroomActivity || context instanceof ViewUserSuggestionsActivity)
            holder.tv_amenity_description.setText(amenity.getName());

        if(context instanceof CreateEditRestroomActivity)
            holder.sw_amenity_description.setText(amenity.getName());

    }

    @Override
    public int getItemCount() 
    {
        return amenity_list.size();
    }


    public class AmenityHolder extends RecyclerView.ViewHolder
    {
        ImageView iv_amenity_icon;
        TextView tv_amenity_description;
        Switch sw_amenity_description;
        public AmenityHolder(@NonNull View itemView, Context context)
        {
            super(itemView);
            iv_amenity_icon = itemView.findViewById(R.id.iv_amenity_icon);

            if(context instanceof ViewRestroomActivity || context instanceof ViewUserSuggestionsActivity)
                tv_amenity_description = itemView.findViewById(R.id.tv_amenity_description);

            if(context instanceof CreateEditRestroomActivity)
                sw_amenity_description = itemView.findViewById(R.id.sw_amenity_description);
        }

        public boolean isSwitchOn()
        {
            return sw_amenity_description != null && sw_amenity_description.isChecked();
        }

        public String getAmenityName()
        {
            return sw_amenity_description.getText().toString();
        }
    }
}
