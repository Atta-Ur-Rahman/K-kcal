package com.techease.k_kcal.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.techease.k_kcal.R;
import com.techease.k_kcal.models.logindatamodels.LoginDataModel;
import com.techease.k_kcal.utilities.GeneralUtills;
import com.techease.k_kcal.utilities.InternetUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.btn_navigate)
    Button btnNavigate;
    @BindView(R.id.map_item_name)
    TextView tvItemName;
    @BindView(R.id.tv_map_published)
    TextView tvPublished;
    @BindView(R.id.tv_map_address)
    TextView tvAddress;
    @BindView(R.id.tv_calories)
    TextView tvCalories;
    @BindView(R.id.iv_map_item)
    ImageView ivMapItem;

    GoogleMap map;
    View view;
    Unbinder unbinder;

    String lat,lng,RestLat,RestLng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

         lat = GeneralUtills.getLat(getActivity());
         lng = GeneralUtills.getLng(getActivity());
         RestLat = GeneralUtills.latitude(getActivity());
         RestLng = GeneralUtills.longitude(getActivity());

        initUI();
        if (InternetUtils.isNetworkConnected(getActivity())) {
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
                mapView.getMapAsync(this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps.mytracks?saddr="+lat+","+lng+"&daddr="+RestLat+","+RestLng));
                startActivity(intent);
            }
        });
        return view;
    }

    private void initUI(){
        ButterKnife.bind(this,view);
        tvAddress.setText(GeneralUtills.getLocation(getActivity()));
        tvItemName.setText(GeneralUtills.getItemName(getActivity()));
        tvPublished.setText(GeneralUtills.getPublished(getActivity()));
        tvCalories.setText(GeneralUtills.getItemCalories(getActivity()));
        Glide.with(getActivity()).load(GeneralUtills.getImageLink(getActivity())).into(ivMapItem);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.foot_dark);
        LatLng sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        map.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Peshwar")).setIcon(icon);
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), 12.0f));
    }

    @Override
    public void onResume() {
        super.onResume();
        //mapView.onResume();
    }
}
