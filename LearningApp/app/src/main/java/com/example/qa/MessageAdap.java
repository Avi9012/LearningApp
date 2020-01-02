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

import com.bumptech.glide.Glide;
import com.example.qa.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdap extends RecyclerView.Adapter<MessageAdap.ViewHolder>{

    public static final int LEFT_MSG = 0;

    public static final int RIGHT_MSG = 1;

    private Context context;

    private List<Chat> chatList;

    private String imageUrl;

    FirebaseUser user;

    public MessageAdap(Context context, List<Chat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdap.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if(viewType == RIGHT_MSG)
        {
            view = LayoutInflater.from(context).inflate(R.layout.chatright, parent, false);
        }
        else
        {
            view = LayoutInflater.from(context).inflate(R.layout.chatleft, parent, false);
        }
        return new MessageAdap.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdap.ViewHolder holder, int position) {

        Chat chat = chatList.get(position);

        holder.message.setText(chat.getMessage());
        if(chat.isIsseen() == true) {

            holder.isseen.setText("Seen");
        }
        else
        {
            //holder.isseen.setText("Delivered");
        }

        if(imageUrl.equals("default"))
        {
            holder.image.setImageResource(R.mipmap.ic_launcher_round);
        }
        else
        {
            Glide.with(context).load(imageUrl).into(holder.image);
        }
        /*
        if(position == chatList.size() - 1)
        {
            if(chat.isIsseen())
            {
                holder.isseen.setText("seen");
            }
            else
            {
                holder.isseen.setText("Delivered");
            }
        }
        else
        {
            holder.isseen.setVisibility(View.GONE);
        }
        */
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView message;

        public ImageView image;

        public TextView isseen;

        public ImageView image1;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            message = itemView.findViewById(R.id.message);

            image = itemView.findViewById(R.id.image);

            isseen = itemView.findViewById(R.id.seen);

            image1 = itemView.findViewById(R.id.img);

        }
    }


    public int getItemViewType(int position)
    {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(chatList.get(position).getSender().equals(user.getUid()))
        {
            return RIGHT_MSG;
        }
        return LEFT_MSG;
    }
}
