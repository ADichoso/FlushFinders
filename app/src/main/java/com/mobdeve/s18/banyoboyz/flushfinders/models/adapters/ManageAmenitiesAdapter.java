package com.mobdeve.s18.banyoboyz.flushfinders.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;

import java.util.ArrayList;

public class ManageAmenitiesAdapter extends RecyclerView.Adapter<ManageAmenitiesAdapter.ManageAmenitiesHolder> {
    ArrayList<AmenityData> amenityData;
    Context context;

    public ManageAmenitiesAdapter(ArrayList<AmenityData> amenityData, Context context) {
        this.amenityData = amenityData;
        this.context = context;
    }

    @NonNull
    @Override
    public ManageAmenitiesAdapter.ManageAmenitiesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.view_restroom_manage_amenity_item_list,parent,false);

        return new ManageAmenitiesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageAmenitiesAdapter.ManageAmenitiesHolder holder, int position) {
        final AmenityData amenityDataList = amenityData.get(position);

        holder.iv_amenity_icon.setImageBitmap(PictureHelper.decodeBase64ToBitmap(amenityDataList.getAmenityPicture()));
        holder.tv_amenity_description.setText(amenityDataList.getName());
        holder.btn_delete_amenity.setOnClickListener(view ->
        {
            FirestoreHelper.getInstance().deleteAmenity(amenityDataList.getName(), task ->
            {
                //Delete the account and update the recycler view accordingly
                int old_position = holder.getAbsoluteAdapterPosition();
                amenityData.remove(old_position);
                notifyItemRemoved(old_position);
                notifyItemRangeChanged(old_position, amenityData.size());
            });
        });

    }

    @Override
    public int getItemCount() {
        return amenityData.size();
    }

    public class ManageAmenitiesHolder extends RecyclerView.ViewHolder{
        ImageView iv_amenity_icon;
        TextView tv_amenity_description;
        Button btn_delete_amenity;

        public ManageAmenitiesHolder(@NonNull View itemView) {
            super(itemView);
            iv_amenity_icon = itemView.findViewById(R.id.iv_amenity_icon);
            tv_amenity_description = itemView.findViewById(R.id.tv_amenity_description);
            btn_delete_amenity = itemView.findViewById(R.id.btn_delete_amenity);
        }
    }
}
