package com.techease.k_kcal.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.techease.k_kcal.R;
import com.techease.k_kcal.models.itemDataModels.ItemDetailModel;

import java.util.List;

public class AllitemAdapters extends RecyclerView.Adapter<AllitemAdapters.MyViewHolder> {
    List<ItemDetailModel> itemDetailModels;
    Context context;

    public AllitemAdapters(Context context, List<ItemDetailModel> itemDetailModels) {
        this.context = context;
        this.itemDetailModels = itemDetailModels;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_item_found_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public long getItemId(int position) {
        final ItemDetailModel model = itemDetailModels.get(position);
        if (itemDetailModels != null && itemDetailModels.size() > position)
            return itemDetailModels.size();
        return 0;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, final int position) {
        final ItemDetailModel model = itemDetailModels.get(position);

        Glide.with(context).load(model.getImageLink()).into(viewHolder.ivItem);
        viewHolder.tvItemName.setText(model.getName());
        viewHolder.tvItemPublish.setText(model.getPublishedAt());
        viewHolder.tvItemLocation.setText(model.getLocation());


    }

    @Override
    public int getItemCount() {
        return itemDetailModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItem;
        TextView tvItemName,tvItemLocation,tvItemPublish;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.item_name);
            ivItem = itemView.findViewById(R.id.iv_item);
            tvItemLocation = itemView.findViewById(R.id.tv_location);
            tvItemPublish = itemView.findViewById(R.id.tv_published);


        }
    }

}
