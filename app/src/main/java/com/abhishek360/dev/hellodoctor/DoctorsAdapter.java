package com.abhishek360.dev.hellodoctor;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

class DoctorsAdapter
{
    private String qual,specs,name,picUrl,docID;


    public String getQual() {
        return qual;
    }
    public String getPicUrl()
    {
        return picUrl;
    }
    public void setQual(String qual) {
        this.qual = qual;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocID() {
        return docID;
    }

    static class  DoctorViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,speciality,qualification;
        ImageView logo_sponsors;
        Button chatButton;


        public DoctorViewHolder(View itemView)
        {
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.team_view_holder_name);
            speciality=(TextView) itemView.findViewById(R.id.team_view_holder_specs);
            qualification=(TextView) itemView.findViewById(R.id.team_view_holder_qual);
            chatButton=itemView.findViewById(R.id.team_view_holder_chat);



            logo_sponsors=(ImageView) itemView.findViewById(R.id.team_view_holder_pic);


        }
    }
}
