package com.techease.k_kcal.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawableEncoder;
import com.techease.k_kcal.R;
import com.techease.k_kcal.models.profileDataModels.ProfileResponseModel;
import com.techease.k_kcal.models.travelDataModel.TravelReponseModel;
import com.techease.k_kcal.networking.ApiClient;
import com.techease.k_kcal.networking.ApiInterface;
import com.techease.k_kcal.ui.activities.MainActivity;
import com.techease.k_kcal.utilities.AlertUtils;
import com.techease.k_kcal.utilities.GeneralUtills;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    AlertDialog alertDialog;
    View view;
    @BindView(R.id.iv_profile_edit)
    ImageView ivEditProfile;
    @BindView(R.id.iv_user_profile)
    ImageView ivUserImage;
    @BindView(R.id.tv_user_email)
    TextView tvEmail;
    @BindView(R.id.tv_user_name)
    TextView tvName;
    @BindView(R.id.iv_back_arrow)
    ImageView ivBackArrow;
    @BindView(R.id.btn_logout)
    Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        ButterKnife.bind(this, view);

        alertDialog = AlertUtils.createProgressDialog(getActivity());
        alertDialog.show();
        apiCallUserProfile();

        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtills.connectFragment(getActivity(), new EditProfileFragment());
            }
        });

        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtills.connectFragment(getActivity(),new AllitemFragment());
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtills.putBooleanValueInEditor(getActivity(),"isLogin",false);
                GeneralUtills.withOutBackStackConnectFragment(getActivity(),new WelcomeFragment());
            }
        });
    }


    private void apiCallUserProfile() {
        ApiInterface services = ApiClient.getApiClient(GeneralUtills.getApiToken(getActivity())).create(ApiInterface.class);
        Call<ProfileResponseModel> allUsers = services.getUserProfile();
        allUsers.enqueue(new Callback<ProfileResponseModel>() {
            @Override
            public void onResponse(Call<ProfileResponseModel> call, Response<ProfileResponseModel> response) {
                alertDialog.dismiss();

                if (response.body() == null) {

                } else if (response.body().getStatus()) {
                    tvName.setText(response.body().getData().getName());
                    tvEmail.setText(response.body().getData().getEmail());
                    GeneralUtills.putStringValueInEditor(getActivity(),"name",response.body().getData().getName());
                    GeneralUtills.putStringValueInEditor(getActivity(),"email",response.body().getData().getEmail());
                    Glide.with(getActivity()).load(response.body().getData().getProfilePicture()).into(ivUserImage);
                }


            }

            @Override
            public void onFailure(Call<ProfileResponseModel> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

}
