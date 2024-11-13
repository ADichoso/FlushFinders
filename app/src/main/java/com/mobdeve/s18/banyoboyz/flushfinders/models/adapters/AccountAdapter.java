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
import com.mobdeve.s18.banyoboyz.flushfinders.helper.ProfilePictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AccountData;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ModViewHolder> {

    AccountData[] accountData;
    Context context;

    public AccountAdapter(AccountData[] accountData, Context context) {
        this.accountData = accountData;
        this.context = context;
    }

    @NonNull
    @Override
    public ModViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.mod_account_item_list,parent,false);

        return new ModViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModViewHolder holder, int position) {
        final AccountData accountDataList = accountData[position];
        holder.iv_profile_pic.setImageBitmap(ProfilePictureHelper.decodeBase64ToBitmap(accountDataList.getProfilePicture()));
        holder.tv_name.setText(accountDataList.getName());
        holder.tv_email.setText(accountDataList.getEmail());
        holder.sw_is_active.setChecked(accountDataList.isActive());
    }

    @Override
    public int getItemCount() {
        return accountData.length;
    }


    public class ModViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_profile_pic;
        TextView tv_name;
        TextView tv_email;
        Switch sw_is_active;

        public ModViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile_pic = itemView.findViewById(R.id.iv_mod_account_pp);
            tv_name = itemView.findViewById(R.id.tv_mod_name);
            tv_email = itemView.findViewById(R.id.tv_mod_email);
            sw_is_active = itemView.findViewById(R.id.sw_mod_access);
        }
    }
}
