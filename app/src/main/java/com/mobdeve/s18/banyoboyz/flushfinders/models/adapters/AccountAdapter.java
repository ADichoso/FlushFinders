package com.mobdeve.s18.banyoboyz.flushfinders.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.PictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AccountData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ModViewHolder>
{
    ArrayList<AccountData> account_list;
    Context context;

    public AccountAdapter(ArrayList<AccountData> account_list, Context context)
    {
        this.account_list = account_list;
        this.context = context;
    }

    @NonNull
    @Override
    public ModViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layout_inflater = LayoutInflater.from(parent.getContext());
        View view = layout_inflater.inflate(R.layout.mod_account_item_list,parent,false);

        return new ModViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModViewHolder holder, int position)
    {
        final AccountData account = account_list.get(position);

        holder.iv_profile_pic.setImageBitmap(PictureHelper.decodeBase64ToBitmap(account.getProfilePicture()));
        holder.tv_name.setText(account.getName());
        holder.tv_email.setText(account.getEmail());
        holder.tv_type.setText(account.getType().toString());

        if(account.isMe())
        {
            holder.sw_is_active.setEnabled(false);
            holder.btn_delete_mod_account.setEnabled(false);
            holder.sw_is_active.setVisibility(View.INVISIBLE);
            holder.btn_delete_mod_account.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.sw_is_active.setChecked(account.isActive());
            holder.sw_is_active.setOnCheckedChangeListener((compoundButton, b) ->
            {
                //Update account info here
                Map<String, Object> data  = new HashMap<>();

                data.put(FirestoreReferences.Accounts.IS_ACTIVE, b);

                FirestoreHelper.getInstance().updateAccount(account.getEmail(), data, null);
            });

            holder.btn_delete_mod_account.setOnClickListener(view ->
            {
                //Delete the account in Firestore first
                FirestoreHelper.getInstance().deleteAccount(account.getEmail(), task ->
                {
                    if(task.isSuccessful())
                    {
                        //Delete the account and update the recycler view accordingly
                        int old_position = holder.getAbsoluteAdapterPosition();
                        account_list.remove(old_position);
                        notifyItemRemoved(old_position);
                        notifyItemRangeChanged(old_position, account_list.size());
                    }
                });
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return account_list.size();
    }

    public class ModViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iv_profile_pic;
        TextView tv_name;
        TextView tv_email;
        TextView tv_type;
        Switch sw_is_active;
        Button btn_delete_mod_account;

        public ModViewHolder(@NonNull View itemView)
        {
            super(itemView);
            iv_profile_pic = itemView.findViewById(R.id.iv_mod_account_pp);
            tv_name = itemView.findViewById(R.id.tv_mod_name);
            tv_email = itemView.findViewById(R.id.tv_mod_email);
            tv_type = itemView.findViewById(R.id.tv_type);
            sw_is_active = itemView.findViewById(R.id.sw_mod_access);
            btn_delete_mod_account = itemView.findViewById(R.id.btn_delete_mod_account);
        }
    }
}
