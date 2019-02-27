package com.techease.k_kcal.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.techease.k_kcal.R;
import com.techease.k_kcal.models.itemDataModels.ResturantDetailModel;
import com.techease.k_kcal.ui.fragment.DetailFragment;
import com.techease.k_kcal.utilities.GeneralUtills;

import java.util.List;

public class ResturantsAdapters extends RecyclerView.Adapter<ResturantsAdapters.MyViewHolder> {
    List<ResturantDetailModel> itemResturantDetailModelList;
    Context context;

    public ResturantsAdapters(Context context, List<ResturantDetailModel> itemResturantDetailModelList) {
        this.context = context;
        this.itemResturantDetailModelList = itemResturantDetailModelList;

    }


    @NonNull
    @Override
    public ResturantsAdapters.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_item_found_layout, parent, false);

        return new ResturantsAdapters.MyViewHolder(itemView);
    }

    @Override
    public long getItemId(int position) {
        final ResturantDetailModel model = itemResturantDetailModelList.get(position);
        if (itemResturantDetailModelList != null && itemResturantDetailModelList.size() > position)
            return itemResturantDetailModelList.size();
        return 0;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ResturantsAdapters.MyViewHolder viewHolder, final int position) {
        final ResturantDetailModel resturantDetailModel = itemResturantDetailModelList.get(position);

        Glide.with(context).load(resturantDetailModel.getImageLink()).into(viewHolder.ivItem);
        viewHolder.tvItemName.setText(resturantDetailModel.getName());
        viewHolder.tvItemLocation.setText(resturantDetailModel.getAddress());

        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtills.putStringValueInEditor(context, "resturant_latitude", resturantDetailModel.getLatitude());
                GeneralUtills.putStringValueInEditor(context, "resturant_longitude", resturantDetailModel.getLongitude());
                GeneralUtills.putStringValueInEditor(context, "location", resturantDetailModel.getAddress());
                GeneralUtills.putStringValueInEditor(context, "item_name", resturantDetailModel.getName());
                GeneralUtills.putStringValueInEditor(context, "item_image", resturantDetailModel.getImageLink());
                GeneralUtills.connectFragment(context, new DetailFragment());

            }
        });


    }

    @Override
    public int getItemCount() {
        return itemResturantDetailModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItem, ivDetailArrow;
        TextView tvItemName, tvItemLocation, tvItemPublish;
        RelativeLayout relativeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.item_name);
            ivItem = itemView.findViewById(R.id.iv_item);
            tvItemLocation = itemView.findViewById(R.id.tv_location);
            tvItemPublish = itemView.findViewById(R.id.tv_published);
            ivDetailArrow = itemView.findViewById(R.id.iv_detail_arrow);
            relativeLayout = itemView.findViewById(R.id.rl_profile);


        }
    }

}
