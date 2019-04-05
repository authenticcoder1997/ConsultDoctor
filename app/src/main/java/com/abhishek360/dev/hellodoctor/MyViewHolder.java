package com.abhishek360.dev.hellodoctor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView tv;
    public Button button;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        tv=itemView.findViewById(R.id.lab_report_holder_ref_doc);
        button=itemView.findViewById(R.id.viewbtn);
    }
}
