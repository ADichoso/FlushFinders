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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AmenityData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManageAmenityAdapter extends RecyclerView.Adapter<ManageAmenityAdapter.ManageAmenityHolder>
{
    private ArrayList<AmenityData> amenity_list;
    private Context context;
    public ManageAmenityAdapter(ArrayList<AmenityData> amenity_list, Context context)
    {
        this.amenity_list = amenity_list;
        this.context = context;
    }

    @NonNull
    @Override
    public ManageAmenityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layout_inflate = LayoutInflater.from(parent.getContext());
        View view = layout_inflate.inflate(R.layout.view_restroom_manage_amenity_item_list,parent,false);

        return new ManageAmenityHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageAmenityHolder holder, int position)
    {
        final AmenityData amenity = amenity_list.get(position);
        holder.iv_amenity_icon.setImageBitmap(PictureHelper.decodeBase64ToBitmap(amenity.getAmenityPicture()));
        holder.tv_amenity_description.setText(amenity.getName());
        holder.btn_delete_amenity.setOnClickListener(view ->
        {
            FirestoreHelper.getInstance().deleteAmenity(amenity.getName(), task ->
            {
                //Delete the account and update the recycler view accordingly
                int old_position = holder.getAbsoluteAdapterPosition();
                amenity_list.remove(old_position);
                notifyItemRemoved(old_position);
                notifyItemRangeChanged(old_position, amenity_list.size());

                //Delete the restroom entries that had this amenity
                FirestoreHelper.getInstance().getRestroomsDBRef().whereArrayContains(FirestoreReferences.Restrooms.AMENITIES, amenity.getName()).get().addOnCompleteListener(task1 ->
                {
                    if(!task1.isSuccessful())
                        return;

                    QuerySnapshot restroom_documents = task1.getResult();

                    if(restroom_documents == null || restroom_documents.isEmpty())
                        return;

                    for(DocumentSnapshot restroom_document : restroom_documents)
                    {
                        Map<String, Object> update_data = new HashMap<String, Object>();

                        update_data.put(FirestoreReferences.Restrooms.AMENITIES, FieldValue.arrayRemove(amenity.getName()));

                        FirestoreHelper.getInstance().updateRestroom(restroom_document.getId(), update_data, task2 ->
                        {

                        });
                    }
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return amenity_list.size();
    }

    public class ManageAmenityHolder extends RecyclerView.ViewHolder
    {
        ImageView iv_amenity_icon;
        TextView tv_amenity_description;
        Button btn_delete_amenity;

        public ManageAmenityHolder(@NonNull View itemView)
        {
            super(itemView);
            iv_amenity_icon = itemView.findViewById(R.id.iv_amenity_icon);
            tv_amenity_description = itemView.findViewById(R.id.tv_amenity_description);
            btn_delete_amenity = itemView.findViewById(R.id.btn_delete_amenity);
        }
    }
}
