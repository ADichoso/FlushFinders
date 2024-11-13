package com.mobdeve.s18.banyoboyz.flushfinders.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.mobdeve.s18.banyoboyz.flushfinders.R;
import com.mobdeve.s18.banyoboyz.flushfinders.helper.ProfilePictureHelper;
import com.mobdeve.s18.banyoboyz.flushfinders.models.AccountData;
import com.mobdeve.s18.banyoboyz.flushfinders.models.FirestoreReferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ModViewHolder> {
    private CollectionReference accountsDBRef;
    ArrayList<AccountData> accountData;
    Context context;

    public AccountAdapter(ArrayList<AccountData> accountData, Context context, CollectionReference accountsDBRef) {
        this.accountData = accountData;
        this.context = context;
        this.accountsDBRef = accountsDBRef;
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
        final AccountData accountDataList = accountData.get(position);
        holder.iv_profile_pic.setImageBitmap(ProfilePictureHelper.decodeBase64ToBitmap(accountDataList.getProfilePicture()));
        holder.tv_name.setText(accountDataList.getName());
        holder.tv_email.setText(accountDataList.getEmail());

        if(accountDataList.isMe())
        {
            holder.sw_is_active.setEnabled(false);
            holder.btn_delete_mod_account.setEnabled(false);
            holder.sw_is_active.setVisibility(View.INVISIBLE);
            holder.btn_delete_mod_account.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.sw_is_active.setChecked(accountDataList.isActive());
            holder.sw_is_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    //Update account info here
                    Map<String, Object> data  = new HashMap<>();

                    data.put(FirestoreReferences.Accounts.IS_ACTIVE, b);

                    accountsDBRef.document(accountDataList.getEmail()).update(data);
                }
            });

            holder.btn_delete_mod_account.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //Delete the account in Firestore first
                    accountsDBRef.document(accountDataList.getEmail()).delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    //Delete the account and update the recycler view accordingly
                                    int old_position = holder.getAbsoluteAdapterPosition();
                                    accountData.remove(old_position);
                                    notifyItemRemoved(old_position);
                                    notifyItemRangeChanged(old_position, accountData.size());
                                }
                            });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return accountData.size();
    }

    public class ModViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_profile_pic;
        TextView tv_name;
        TextView tv_email;
        Switch sw_is_active;
        Button btn_delete_mod_account;

        public ModViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile_pic = itemView.findViewById(R.id.iv_mod_account_pp);
            tv_name = itemView.findViewById(R.id.tv_mod_name);
            tv_email = itemView.findViewById(R.id.tv_mod_email);
            sw_is_active = itemView.findViewById(R.id.sw_mod_access);
            btn_delete_mod_account = itemView.findViewById(R.id.btn_delete_mod_account);
        }
    }
}
