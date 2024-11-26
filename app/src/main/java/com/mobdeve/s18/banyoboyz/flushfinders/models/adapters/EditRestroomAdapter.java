package com.mobdeve.s18.banyoboyz.flushfinders.models.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.MapHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.RestroomData;
import com.mobdeve.s18.banyoboyz.flushfinders.modmode.EditRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.CreateEditRestroomActivity;
import com.mobdeve.s18.banyoboyz.flushfinders.sharedviews.SuggestRestroomLocationActivity;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class EditRestroomAdapter extends RecyclerView.Adapter<EditRestroomAdapter.EditRestroomHolder>
{
    public static final String LATITUDE = SuggestRestroomLocationActivity.LATITUDE;
    public static final String LONGITUDE =  SuggestRestroomLocationActivity.LONGITUDE;
    public static final String RESTROOM_ID = "RESTROOM_ID";

    ArrayList<RestroomData> restroom_list;

    EditRestroomActivity context;

    public EditRestroomAdapter(ArrayList<RestroomData> restroom_list, EditRestroomActivity context)
    {
        this.restroom_list = restroom_list;
        this.context = context;
    }

    @NonNull
    @Override
    public EditRestroomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layout_inflater = LayoutInflater.from(parent.getContext());

        View view  = layout_inflater.inflate(R.layout.restroom_item_list,parent,false);
        return new EditRestroomHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull EditRestroomHolder holder, int position)
    {
        final RestroomData restroom = restroom_list.get(position);

        holder.tv_building_name.setText(restroom.getBuildingName());
        holder.tv_restroom_building_address.setText(restroom.getBuildingAddress());
        holder.tv_restroom_floor.setText(restroom.getName());
        holder.iv_building_pic.setImageBitmap(PictureHelper.decodeBase64ToBitmap(restroom.getBuildingPicture()));
        holder.pb_cleanliness.setProgress(restroom.getCleanliness());
        holder.pb_maintenance.setProgress(restroom.getMaintenance());
        holder.pb_vacancy.setProgress(restroom.getVacancy());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, CreateEditRestroomActivity.class);

            GeoPoint point = MapHelper.getInstance().decodeBuildingLocation(restroom.getBuildingId());

            intent.putExtra(LATITUDE, point.getLatitude());
            intent.putExtra(LONGITUDE, point.getLongitude());
            intent.putExtra(RESTROOM_ID, restroom.getId());


            context.setResult(Activity.RESULT_OK, intent);
            context.activity_result_launcher.launch(intent);
        });
    }

    @Override
    public int getItemCount()
    {
        return restroom_list.size();
    }


    public class EditRestroomHolder extends RecyclerView.ViewHolder
    {
        ImageView iv_building_pic;
        TextView tv_building_name;
        TextView tv_restroom_building_address;
        TextView tv_restroom_floor;
        ProgressBar pb_cleanliness;
        ProgressBar pb_maintenance;
        ProgressBar pb_vacancy;

        public EditRestroomHolder(@NonNull View itemView, Context context)
        {
            super(itemView);
            iv_building_pic = itemView.findViewById(R.id.iv_building_pic);
            tv_building_name = itemView.findViewById(R.id.tv_building_name);
            tv_restroom_floor = itemView.findViewById(R.id.tv_restroom_floor);
            tv_restroom_building_address = itemView.findViewById(R.id.tv_restroom_building_address);

            pb_cleanliness = itemView.findViewById(R.id.pb_cleanliness);
            pb_maintenance = itemView.findViewById(R.id.pb_maintenance);
            pb_vacancy = itemView.findViewById(R.id.pb_vacancy);
        }
    }
}
