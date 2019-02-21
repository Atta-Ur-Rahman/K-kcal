package com.techease.k_kcal.ui.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techease.k_kcal.R;
import com.techease.k_kcal.adapters.AllitemAdapters;
import com.techease.k_kcal.models.filterDataModels.ItemCategoriesModel;
import com.techease.k_kcal.models.itemDataModels.ItemDetailModel;
import com.techease.k_kcal.models.itemDataModels.ItemResponseModel;
import com.techease.k_kcal.models.itemDataModels.ItemResturantDetailModel;
import com.techease.k_kcal.networking.ApiClient;
import com.techease.k_kcal.networking.ApiInterface;
import com.techease.k_kcal.utilities.AlertUtils;
import com.techease.k_kcal.utilities.Config;
import com.techease.k_kcal.utilities.GeneralUtills;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllitemFragment extends Fragment {
    android.support.v7.app.AlertDialog alertDialog;
    @BindView(R.id.filter_layout)
    LinearLayout filterLayout;
    @BindView(R.id.rv_all_items)
    RecyclerView rvItems;
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    @BindView(R.id.clear_filter_layout)
    LinearLayout clearFilterLayout;
    AllitemAdapters allitemAdapters;
    List<ItemDetailModel> itemDataModelList;
    List<ItemResturantDetailModel> itemResturantDetailModels;
    View view;
    public static TextView tvTotalitems;

    SeekBar seekBarPrice, seekBarDistance;
    Spinner spinner;
    ArrayList<String> categoryItemArrayList = new ArrayList<>();
    ArrayAdapter<String> categoryItemArrayAdapter;

    String token, strCategoryItem, strPriceRange, strDistance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_item_found, container, false);
        tvTotalitems = view.findViewById(R.id.tv_total_items);
        initUI();
        return view;
    }

    private void initUI() {
        ButterKnife.bind(this, view);
        token = GeneralUtills.getApiToken(getActivity());
        RecyclerView.LayoutManager mLayoutManagerReviews = new LinearLayoutManager(getActivity());
        rvItems.setLayoutManager(mLayoutManagerReviews);
        itemDataModelList = new ArrayList<>();
        itemResturantDetailModels = new ArrayList<>();
        alertDialog = AlertUtils.createProgressDialog(getActivity());
        alertDialog.show();
        apiCallAllItems();
        apiCallItemsCategories();

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtills.connectFragment(getActivity(), new ProfileFragment());
            }
        });

        filterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        clearFilterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initUI();
            }
        });


    }

    private void apiCallAllItems() {
        ApiInterface services = ApiClient.getApiClient(token).create(ApiInterface.class);
        Call<ItemResponseModel> allUsers = services.getItems();
        allUsers.enqueue(new Callback<ItemResponseModel>() {
            @Override
            public void onResponse(Call<ItemResponseModel> call, Response<ItemResponseModel> response) {
                alertDialog.dismiss();
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "no data", Toast.LENGTH_SHORT).show();
                } else if (response.body().getStatus()) {

                    itemDataModelList.addAll(response.body().getData());
                    for (int i = 0; i < itemDataModelList.size(); i++) {
                        itemResturantDetailModels.addAll(response.body().getData().get(i).getRestaurants());
                    }
                    clearFilterLayout.setVisibility(View.GONE);
                    filterLayout.setVisibility(View.VISIBLE);

                    allitemAdapters = new AllitemAdapters(getActivity(), itemDataModelList, itemResturantDetailModels);
                    rvItems.setAdapter(allitemAdapters);
                    allitemAdapters.notifyDataSetChanged();

                } else {
                    Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ItemResponseModel> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    private void apiCallItemsCategories() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.CategoriesItems
                , new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject itemObject = jsonArray.getJSONObject(i);
                        String strName = itemObject.getString("name");
                        ItemCategoriesModel model = new ItemCategoriesModel();
                        model.setName(strName);
                        categoryItemArrayList.add(strName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }

        };
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(stringRequest);
    }

    private void apiCallFilterItems() {
        ApiInterface services = ApiClient.getApiClient(token).create(ApiInterface.class);
        Call<ItemResponseModel> allUsers = services.getFiltersItems(strCategoryItem, strPriceRange, "pakistan");
        allUsers.enqueue(new Callback<ItemResponseModel>() {
            @Override
            public void onResponse(Call<ItemResponseModel> call, Response<ItemResponseModel> response) {
                alertDialog.dismiss();
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "no record found", Toast.LENGTH_SHORT).show();
                } else if (response.body().getStatus()) {
                    itemDataModelList.clear();
                    itemDataModelList.addAll(response.body().getData());
                    for (int i = 0; i < itemDataModelList.size(); i++) {
                        itemResturantDetailModels.addAll(response.body().getData().get(i).getRestaurants());
                    }
                    filterLayout.setVisibility(View.GONE);
                    clearFilterLayout.setVisibility(View.VISIBLE);
                    tvTotalitems.setText(String.valueOf(itemDataModelList.size()));
                    allitemAdapters = new AllitemAdapters(getActivity(), itemDataModelList, itemResturantDetailModels);
                    rvItems.setAdapter(allitemAdapters);
                    allitemAdapters.notifyDataSetChanged();

                } else {
                    Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ItemResponseModel> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    private void showFilterDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_filter_dialog_layout);
        spinner = dialog.findViewById(R.id.spinner_choose_category);
        seekBarPrice = dialog.findViewById(R.id.sb_price);
        seekBarDistance = dialog.findViewById(R.id.sb_distance);
        final TextView tvSeekPrice = dialog.findViewById(R.id.tv_seekbar_price);
        final TextView tvSeekMiles = dialog.findViewById(R.id.tv_seekbar_miles);
        final Button btnAppluFilter = dialog.findViewById(R.id.btn_apply_filter);
        final ImageView ivClose = dialog.findViewById(R.id.iv_dialog_close);


        categoryItemArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.spinner_text, categoryItemArrayList);
        spinner.setAdapter(categoryItemArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strCategoryItem = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        seekBarPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSeekPrice.setText("$" + String.valueOf(progress));
                strPriceRange = String.valueOf(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSeekMiles.setText(String.valueOf(progress + "Miles"));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnAppluFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = AlertUtils.createProgressDialog(getActivity());
                alertDialog.show();
                apiCallFilterItems();
                dialog.dismiss();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
