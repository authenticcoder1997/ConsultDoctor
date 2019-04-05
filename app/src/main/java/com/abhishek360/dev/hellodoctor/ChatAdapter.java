package com.abhishek360.dev.hellodoctor;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatAdapter
{
    private String message,senderID;


    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getSenderID()
    {
        return senderID;
    }

    public void setSenderID(String senderID)
    {
        this.senderID = senderID;
    }

    static class  ChatViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,my_message,their_message;
        View avatar;


        public ChatViewHolder(View itemView)
        {
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.chat_name);
            my_message=(TextView) itemView.findViewById(R.id.chat_my_message);
            their_message=(TextView) itemView.findViewById(R.id.chat_their_message);
            avatar=itemView.findViewById(R.id.chat_avatar);



        }
    }
}
