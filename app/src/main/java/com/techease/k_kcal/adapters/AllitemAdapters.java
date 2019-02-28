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
import com.techease.k_kcal.models.itemDataModels.ItemDetailModel;
import com.techease.k_kcal.models.itemDataModels.ResturantDetailModel;
import com.techease.k_kcal.ui.fragment.AllitemFragment;
import com.techease.k_kcal.ui.fragment.ResturantFragment;
import com.techease.k_kcal.utilities.GeneralUtills;

import java.util.List;

public class AllitemAdapters extends RecyclerView.Adapter<AllitemAdapters.MyViewHolder> {
    List<ItemDetailModel> itemDetailModels;
    List<ResturantDetailModel> itemResturantDetailModelList;
    Context context;

    public AllitemAdapters(Context context, List<ItemDetailModel> itemDetailModels, List<ResturantDetailModel> itemResturantDetailModelList) {
        this.context = context;
        this.itemDetailModels = itemDetailModels;
        this.itemResturantDetailModelList = itemResturantDetailModelList;

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
        final ResturantDetailModel resturantDetailModel = itemResturantDetailModelList.get(position);

        AllitemFragment.tvTotalitems.setText(String.valueOf(position + 1));
        Glide.with(context).load(model.getImageLink()).into(viewHolder.ivItem);
        viewHolder.tvItemName.setText(model.getName());
        viewHolder.tvItemPublish.setText(model.getPublishedAt());
        viewHolder.tvItemLocation.setText(model.getLocation());
        viewHolder.tvCalories.setText(model.getCalaries());

        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GeneralUtills.putIntegerValueInEditor(context,"itemID",model.getId());
//                GeneralUtills.putStringValueInEditor(context, "latitude", model.getLatitude());
//                GeneralUtills.putStringValueInEditor(context, "longitude", model.getLongitude());
                GeneralUtills.putStringValueInEditor(context, "published", model.getPublishedAt());
                GeneralUtills.putStringValueInEditor(context,"calories",model.getCalaries());
                GeneralUtills.connectFragment(context, new ResturantFragment());

            }
        });


    }

    @Override
    public int getItemCount() {
        return itemDetailModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItem, ivDetailArrow;
        TextView tvItemName, tvItemLocation, tvItemPublish,tvCalories;
        RelativeLayout relativeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.item_name);
            ivItem = itemView.findViewById(R.id.iv_item);
            tvItemLocation = itemView.findViewById(R.id.tv_location);
            tvItemPublish = itemView.findViewById(R.id.tv_published);
            ivDetailArrow = itemView.findViewById(R.id.iv_detail_arrow);
            relativeLayout = itemView.findViewById(R.id.rl_profile);
            tvCalories = itemView.findViewById(R.id.tv_cal);


        }
    }

}
