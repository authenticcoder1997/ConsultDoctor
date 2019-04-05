package com.abhishek360.dev.hellodoctor;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatListAdapter
{
    private String uID,username;

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    static class  ChatListViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        CardView chatListCard;



        public ChatListViewHolder(View itemView)
        {
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.chat_list_holder_name);
            chatListCard=(CardView) itemView.findViewById(R.id.chat_list_card);



        }
    }
}
