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
import android.widget.Toast;

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

    GoogleMap map;
    View view;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

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
                        Uri.parse("http://maps.google.com/maps.mytracks?saddr=33.983970,71.428352&daddr=33.555749,72.832427"));
                startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.foot_dark);
        LatLng sydney = new LatLng(33.986381, 71.438575);
        map.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Peshwar")).setIcon(icon);
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.986381, 71.438575), 12.0f));
    }

    @Override
    public void onResume() {
        super.onResume();
        //mapView.onResume();
    }
}
