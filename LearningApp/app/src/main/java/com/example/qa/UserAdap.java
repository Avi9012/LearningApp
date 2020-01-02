package com.example.qa;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qa.model.User;

import java.util.List;

public class UserAdap extends RecyclerView.Adapter<UserAdap.ViewHolder> {

    private Context context;

    private List<User> userList;

    private boolean chat;

    public UserAdap(Context context, List<User> userList, boolean chat) {
        this.context = context;
        this.userList = userList;
        this.chat = chat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.viewitem, parent, false);
        return new UserAdap.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = userList.get(position);
        holder.name.setText(user.getName());
        if(user == null)
        {
            Toast.makeText(context, "xyz", Toast.LENGTH_SHORT).show();
        }
        else if(user.getImageUrl().equals("default"))
        {
            holder.image.setImageResource(R.mipmap.ic_launcher_round);
        }
        else
        {
            Glide.with(context).load(user.getImageUrl()).into(holder.image);
        }

        if(chat)
        {
            if(user.getStatus().equals("Online"))
            {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }
            else
            {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UseransActivity.class);
                intent.putExtra("imageUrl", user.getImageUrl());
                intent.putExtra("name", user.getName());
                intent.putExtra("email", user.getEmail());
                //intent.putExtra("interests", user.getCategories());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, Messages.class);
                intent.putExtra("userid", user.getUserId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public ImageView image;

        public ImageView img_off;
        public ImageView img_on;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            name = itemView.findViewById(R.id.user_name);

            image = itemView.findViewById(R.id.user_image);

            img_on = itemView.findViewById(R.id.status);

            img_off = itemView.findViewById(R.id.status_off);
        }
    }
}
