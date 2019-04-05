package com.abhishek360.dev.hellodoctor;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ImageLabReport> imageLabReportList;

    public ReportsAdapter(Context context,List<ImageLabReport> imageLabReportList){
        this.context=context;
        this.imageLabReportList=imageLabReportList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(context).inflate(R.layout.report_template,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


        MyViewHolder myViewHolder=(MyViewHolder)viewHolder;
        final ImageLabReport sample=imageLabReportList.get(i);
        myViewHolder.tv.setText(sample.getSenderName());
        myViewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog =new Dialog(context);
                dialog.setContentView(R.layout.labreportview);
                ImageView iv=dialog.findViewById(R.id.lbreportview);
                Button btn=dialog.findViewById(R.id.okbtndlg);
                Picasso.get().load(sample.getImgurl()).into(iv);
                dialog.show();
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageLabReportList==null ? 0:imageLabReportList.size();
    }
}
